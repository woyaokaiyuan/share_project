package com.luwq.controller;

import com.luwq.common.ApiResponse;
import com.luwq.common.UserContext;
import com.luwq.dto.LoginRequest;
import com.luwq.service.AuthService;
import com.luwq.vo.LoginResponse;
import com.luwq.vo.UserInfoVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final String BEARER_PREFIX = "Bearer ";

    private final AuthService authService;
    private final UserContext userContext;

    public AuthController(AuthService authService, UserContext userContext) {
        this.authService = authService;
        this.userContext = userContext;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        return ApiResponse.ok(authService.login(request.getUsername(), request.getPassword()));
    }

    @GetMapping("/userInfo")
    public ApiResponse<UserInfoVO> userInfo() {
        return ApiResponse.ok(authService.getUserInfo(userContext.getUserId()));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        String token = authorization != null && authorization.startsWith(BEARER_PREFIX)
                ? authorization.substring(BEARER_PREFIX.length())
                : null;
        authService.logout(token);
        return ApiResponse.ok();
    }
}
