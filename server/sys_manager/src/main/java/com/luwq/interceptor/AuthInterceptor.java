package com.luwq.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luwq.common.ApiResponse;
import com.luwq.common.TokenStore;
import com.luwq.common.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录态拦截器：校验 Authorization 请求头中的 Bearer Token。
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final String BEARER_PREFIX = "Bearer ";

    private final TokenStore tokenStore;
    private final UserContext userContext;
    private final ObjectMapper objectMapper;

    public AuthInterceptor(TokenStore tokenStore, UserContext userContext, ObjectMapper objectMapper) {
        this.tokenStore = tokenStore;
        this.userContext = userContext;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith(BEARER_PREFIX)) {
            writeUnauthorized(response, "未登录");
            return false;
        }
        String token = authorization.substring(BEARER_PREFIX.length());
        String userId = tokenStore.getUserId(token);
        if (userId == null) {
            writeUnauthorized(response, "登录已过期，请重新登录");
            return false;
        }
        userContext.setUserId(userId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        userContext.clear();
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.fail(401, message)));
    }
}
