package com.luwq.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录请求参数。
 */
@Data
public class LoginRequest implements Serializable {

    private String username;
    private String password;
}
