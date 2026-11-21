package com.luwq.common;

import com.luwq.entity.SysUser;
import com.luwq.mapper.AuthMapper;
import com.luwq.vo.UserAuthCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 权限判断：从 UserContext 取当前用户，优先读 Redis 权限缓存，未命中回源数据库并回填。
 * 兼容超管 *:*:* 与 * 通配匹配。
 */
@Component
public class PermissionChecker {

    private static final Logger log = LoggerFactory.getLogger(PermissionChecker.class);

    private final UserContext userContext;
    private final PermissionCacheStore permissionCacheStore;
    private final AuthMapper authMapper;

    public PermissionChecker(UserContext userContext,
                             PermissionCacheStore permissionCacheStore,
                             AuthMapper authMapper) {
        this.userContext = userContext;
        this.permissionCacheStore = permissionCacheStore;
        this.authMapper = authMapper;
    }

    public boolean hasPermission(String[] required, boolean anyOf) {
        String userId = userContext.getUserId();
        if (userId == null || userId.isBlank()) {
            return false;
        }

        UserAuthCache cache = permissionCacheStore.get(userId);
        if (cache == null) {
            cache = loadFromDatabase(userId);
            if (cache == null) {
                return false;
            }
            permissionCacheStore.save(userId, cache.getRoles(), cache.getPermissions());
        }

        List<String> permissions = cache.getPermissions();
        if (permissions == null || permissions.isEmpty()) {
            return false;
        }

        // 超管全局通配
        if (permissions.contains("*:*:*")) {
            return true;
        }

        if (anyOf) {
            return Arrays.stream(required).anyMatch(p -> match(permissions, p));
        }
        return Arrays.stream(required).allMatch(p -> match(permissions, p));
    }

    private boolean match(List<String> permissions, String required) {
        if (required == null || required.isBlank()) {
            return false;
        }
        for (String granted : permissions) {
            if (wildcardMatch(granted, required)) {
                return true;
            }
        }
        return false;
    }

    /** 支持 * 通配，如 system:*:* 匹配 system:user:add */
    private boolean wildcardMatch(String pattern, String target) {
        if (pattern == null || target == null) {
            return false;
        }
        String[] p = pattern.split(":", -1);
        String[] t = target.split(":", -1);
        int len = Math.min(p.length, t.length);
        for (int i = 0; i < len; i++) {
            if (!"*".equals(p[i]) && !p[i].equals(t[i])) {
                return false;
            }
        }
        if (p.length < t.length) {
            return false;
        }
        // 多余段必须全部是 *
        for (int i = t.length; i < p.length; i++) {
            if (!"*".equals(p[i])) {
                return false;
            }
        }
        return true;
    }

    private UserAuthCache loadFromDatabase(String userId) {
        try {
            SysUser user = authMapper.selectById(userId);
            if (user == null) {
                return null;
            }
            boolean isAdmin = user.getUserType() != null && user.getUserType() == 1;
            List<String> roles = isAdmin
                    ? List.of("admin")
                    : authMapper.selectRoleCodesByUserId(userId);
            List<String> permissions = isAdmin
                    ? List.of("*:*:*")
                    : authMapper.selectPermissionCodesByUserId(userId);
            return new UserAuthCache(roles, permissions);
        } catch (Exception e) {
            log.warn("回源查询权限失败, userId={}, error={}", userId, e.getClass().getSimpleName());
            return null;
        }
    }
}
