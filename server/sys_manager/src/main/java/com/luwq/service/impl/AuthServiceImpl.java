package com.luwq.service.impl;

import com.luwq.common.PermissionCacheStore;
import com.luwq.common.TokenStore;
import com.luwq.entity.SysUser;
import com.luwq.mapper.AuthMapper;
import com.luwq.service.AuthService;
import com.luwq.vo.LoginResponse;
import com.luwq.vo.UserAuthCache;
import com.luwq.vo.UserInfoVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;
    private final TokenStore tokenStore;
    private final PermissionCacheStore permissionCacheStore;

    public AuthServiceImpl(AuthMapper authMapper, PasswordEncoder passwordEncoder,
                           TokenStore tokenStore, PermissionCacheStore permissionCacheStore) {
        this.authMapper = authMapper;
        this.passwordEncoder = passwordEncoder;
        this.tokenStore = tokenStore;
        this.permissionCacheStore = permissionCacheStore;
    }

    @Override
    public LoginResponse login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("用户名或密码不能为空");
        }
        SysUser user = authMapper.selectByUsername(username.trim());
        if (user == null) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() != 1) {
            throw new IllegalArgumentException("账号已被禁用");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }

        UserAuthCache authCache = buildAuthCache(user);
        LoginResponse response = new LoginResponse();
        fillUserInfo(response, user, authCache);
        String token = tokenStore.createToken(user.getId());
        response.setToken(token);
        permissionCacheStore.save(user.getId(), authCache.getRoles(), authCache.getPermissions());
        return response;
    }

    @Override
    public UserInfoVO getUserInfo(String userId) {
        SysUser user = authMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        UserAuthCache cached = permissionCacheStore.get(userId);
        UserAuthCache authCache = cached != null ? cached : buildAuthCache(user);
        if (cached == null) {
            permissionCacheStore.save(userId, authCache.getRoles(), authCache.getPermissions());
        }

        UserInfoVO info = new UserInfoVO();
        fillUserInfo(info, user, authCache);
        return info;
    }

    @Override
    public void logout(String token) {
        String userId = tokenStore.remove(token);
        if (userId != null) {
            permissionCacheStore.remove(userId);
        }
    }

    private UserAuthCache buildAuthCache(SysUser user) {
        boolean isAdmin = user.getUserType() != null && user.getUserType() == 1;
        List<String> roles = isAdmin ? List.of("admin") : authMapper.selectRoleCodesByUserId(user.getId());
        List<String> permissions = isAdmin
                ? List.of("*:*:*")
                : authMapper.selectPermissionCodesByUserId(user.getId());
        return new UserAuthCache(roles, permissions);
    }

    private void fillUserInfo(UserInfoVO info, SysUser user, UserAuthCache authCache) {
        info.setId(user.getId());
        info.setUsername(user.getUsername());
        info.setNickname(user.getNickname());
        info.setRoles(authCache.getRoles());
        info.setPermissions(authCache.getPermissions());
    }
}
