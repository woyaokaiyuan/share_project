package com.luwq.service;

import com.luwq.vo.LoginResponse;
import com.luwq.vo.UserInfoVO;

public interface AuthService {

    LoginResponse login(String username, String password);

    UserInfoVO getUserInfo(String userId);

    void logout(String token);
}
