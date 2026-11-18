package com.luwq.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luwq.vo.UserAuthCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collection;
import java.util.List;

/**
 * 用户权限缓存。
 * auth:permissions:{userId} -> UserAuthCache JSON，TTL 由配置控制（默认 2 小时）。
 */
@Component
public class PermissionCacheStore {

    private static final Logger log = LoggerFactory.getLogger(PermissionCacheStore.class);
    private static final String PERMISSION_KEY_PREFIX = "auth:permissions:";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${auth.permission.ttl-seconds:7200}")
    private long ttlSeconds;

    public PermissionCacheStore(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void save(String userId, List<String> roles, List<String> permissions) {
        if (userId == null || userId.isBlank()) {
            return;
        }
        try {
            String json = objectMapper.writeValueAsString(new UserAuthCache(roles, permissions));
            redisTemplate.opsForValue().set(permissionKey(userId), json, Duration.ofSeconds(ttlSeconds));
        } catch (JsonProcessingException e) {
            log.warn("序列化权限缓存失败, userId={}", userId, e);
        } catch (Exception e) {
            log.warn("保存权限缓存失败, userId={}, error={}", userId, e.getClass().getSimpleName());
        }
    }

    public UserAuthCache get(String userId) {
        if (userId == null || userId.isBlank()) {
            return null;
        }
        try {
            String json = redisTemplate.opsForValue().get(permissionKey(userId));
            if (json == null || json.isBlank()) {
                return null;
            }
            return objectMapper.readValue(json, UserAuthCache.class);
        } catch (JsonProcessingException e) {
            log.warn("反序列化权限缓存失败, userId={}", userId, e);
            return null;
        } catch (Exception e) {
            log.warn("读取权限缓存失败, userId={}, error={}", userId, e.getClass().getSimpleName());
            return null;
        }
    }

    public void remove(String userId) {
        if (userId == null || userId.isBlank()) {
            return;
        }
        try {
            redisTemplate.delete(permissionKey(userId));
        } catch (Exception e) {
            log.warn("删除权限缓存失败, userId={}, error={}", userId, e.getClass().getSimpleName());
        }
    }

    public void removeAll(Collection<String> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }
        List<String> keys = userIds.stream()
                .filter(id -> id != null && !id.isBlank())
                .distinct()
                .map(this::permissionKey)
                .toList();
        if (!keys.isEmpty()) {
            try {
                redisTemplate.delete(keys);
            } catch (Exception e) {
                log.warn("批量删除权限缓存失败, count={}, error={}", keys.size(), e.getClass().getSimpleName());
            }
        }
    }

    private String permissionKey(String userId) {
        return PERMISSION_KEY_PREFIX + userId;
    }
}
