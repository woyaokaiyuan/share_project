package com.luwq.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

/**
 * 基于 Redis 的 Token 存储。
 * auth:token:{token} -> 用户ID，TTL 由配置控制（默认 2 小时）。
 */
@Component
public class TokenStore {

    private static final Logger log = LoggerFactory.getLogger(TokenStore.class);
    private static final String TOKEN_KEY_PREFIX = "auth:token:";

    private final StringRedisTemplate redisTemplate;

    @Value("${auth.token.ttl-seconds:7200}")
    private long ttlSeconds;

    public TokenStore(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String createToken(String userId) {
        String token = UUID.randomUUID().toString().replace("-", "");
        try {
            redisTemplate.opsForValue().set(tokenKey(token), userId, Duration.ofSeconds(ttlSeconds));
        } catch (Exception e) {
            log.warn("保存 Token 到 Redis 失败, userId={}, error={}", userId, e.getClass().getSimpleName());
            throw new IllegalStateException("登录态存储不可用，请稍后重试");
        }
        return token;
    }

    public String getUserId(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        try {
            return redisTemplate.opsForValue().get(tokenKey(token));
        } catch (Exception e) {
            log.warn("读取 Token 失败, error={}", e.getClass().getSimpleName());
            return null;
        }
    }

    /**
     * 删除 Token 并返回其对应的用户ID（便于同时清理权限缓存）。
     */
    public String remove(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        String key = tokenKey(token);
        try {
            String userId = redisTemplate.opsForValue().get(key);
            redisTemplate.delete(key);
            return userId;
        } catch (Exception e) {
            log.warn("删除 Token 失败, error={}", e.getClass().getSimpleName());
            return null;
        }
    }

    private String tokenKey(String token) {
        return TOKEN_KEY_PREFIX + token;
    }
}
