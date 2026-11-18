package com.luwq.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 重置密码请求参数。
 */
@Data
public class ResetPasswordRequest implements Serializable {

    /** 新密码，为空时重置为系统默认密码 */
    private String password;
}
