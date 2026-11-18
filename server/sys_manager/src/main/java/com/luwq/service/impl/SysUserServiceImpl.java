package com.luwq.service.impl;

import com.luwq.Snowflake;
import com.luwq.common.PageResult;
import com.luwq.dto.UserQuery;
import com.luwq.dto.UserSaveRequest;
import com.luwq.entity.SysUser;
import com.luwq.mapper.SysUserMapper;
import com.luwq.mapper.SysUserRoleMapper;
import com.luwq.service.SysUserService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class SysUserServiceImpl implements SysUserService {

    /** 默认初始密码 */
    private static final String DEFAULT_PASSWORD = "123456";

    private final SysUserMapper userMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;

    @Value("${snowflake.worker-id:1}")
    private long workerId;

    @Value("${snowflake.datacenter-id:1}")
    private long datacenterId;

    private Snowflake snowflake;

    public SysUserServiceImpl(SysUserMapper userMapper, SysUserRoleMapper userRoleMapper,
                              PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.userRoleMapper = userRoleMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        this.snowflake = new Snowflake(workerId, datacenterId);
    }

    @Override
    public PageResult<SysUser> page(UserQuery query) {
        List<SysUser> list = userMapper.selectPage(query);
        long total = userMapper.countPage(query);
        return new PageResult<>(list, total);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(UserSaveRequest request) {
        String username = requireUsername(request.getUsername());
        checkUsernameAvailable(username, null);

        SysUser user = new SysUser();
        user.setId(String.valueOf(snowflake.nextId()));
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setStatus(request.getStatus() == null ? 1 : request.getStatus());
        user.setUserType(0);
        user.setPermVersion(0);
        userMapper.insert(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UserSaveRequest request) {
        if (request.getId() == null || request.getId().isBlank()) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (userMapper.selectById(request.getId()) == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        String username = requireUsername(request.getUsername());
        checkUsernameAvailable(username, request.getId());

        SysUser user = new SysUser();
        user.setId(request.getId());
        user.setUsername(username);
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setStatus(request.getStatus() == null ? 1 : request.getStatus());
        userMapper.update(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        userMapper.deleteById(id);
    }

    @Override
    public List<Integer> getRoleIds(String userId) {
        if (userId == null || userId.isBlank() || userMapper.selectById(userId) == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        return userRoleMapper.selectRoleIdsByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(String userId, List<Integer> roleIds) {
        if (userId == null || userId.isBlank() || userMapper.selectById(userId) == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        // 全量覆盖：先清空旧角色，再插入新角色
        userRoleMapper.deleteByUserId(userId);
        if (roleIds == null) {
            return;
        }
        List<Integer> ids = roleIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (!ids.isEmpty()) {
            userRoleMapper.batchInsert(userId, ids);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(String userId, String password) {
        if (userId == null || userId.isBlank() || userMapper.selectById(userId) == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        String rawPassword = password == null || password.isBlank() ? DEFAULT_PASSWORD : password;
        userMapper.updatePassword(userId, passwordEncoder.encode(rawPassword));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(String userId, String oldPassword, String newPassword) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        String storedPassword = userMapper.selectPasswordById(userId);
        if (storedPassword == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        if (oldPassword == null || !passwordEncoder.matches(oldPassword, storedPassword)) {
            throw new IllegalArgumentException("原密码不正确");
        }
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("新密码不能为空");
        }
        userMapper.updatePassword(userId, passwordEncoder.encode(newPassword));
    }

    private String requireUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        return username.trim();
    }

    private void checkUsernameAvailable(String username, String excludeId) {
        if (userMapper.countByUsername(username, excludeId) > 0) {
            throw new IllegalArgumentException("用户名已存在");
        }
    }
}
