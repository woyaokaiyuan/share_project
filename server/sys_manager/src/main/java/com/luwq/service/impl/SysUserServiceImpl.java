package com.luwq.service.impl;

import com.luwq.Snowflake;
import com.luwq.common.PageResult;
import com.luwq.dto.UserQuery;
import com.luwq.dto.UserSaveRequest;
import com.luwq.entity.SysUser;
import com.luwq.mapper.SysUserMapper;
import com.luwq.service.SysUserService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SysUserServiceImpl implements SysUserService {

    /** 演示用初始密码，正式环境应接入密码加密与修改密码流程 */
    private static final String DEFAULT_PASSWORD = "123456";

    private final SysUserMapper userMapper;

    @Value("${snowflake.worker-id:1}")
    private long workerId;

    @Value("${snowflake.datacenter-id:1}")
    private long datacenterId;

    private Snowflake snowflake;

    public SysUserServiceImpl(SysUserMapper userMapper) {
        this.userMapper = userMapper;
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
        user.setPassword(DEFAULT_PASSWORD);
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
