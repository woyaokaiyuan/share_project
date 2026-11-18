package com.luwq.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 登录响应，在用户信息基础上携带 Token。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LoginResponse extends UserInfoVO {

    private String token;
}
