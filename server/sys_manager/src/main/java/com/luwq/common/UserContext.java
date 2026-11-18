package com.luwq.common;

import org.springframework.stereotype.Component;

/**
 * 当前请求的用户上下文。
 */
@Component
public class UserContext {

    private static final ThreadLocal<String> CURRENT_USER_ID = new ThreadLocal<>();

    public void setUserId(String userId) {
        CURRENT_USER_ID.set(userId);
    }

    public String getUserId() {
        return CURRENT_USER_ID.get();
    }

    public void clear() {
        CURRENT_USER_ID.remove();
    }
}
