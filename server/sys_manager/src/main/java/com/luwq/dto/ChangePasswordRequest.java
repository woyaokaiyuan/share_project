package com.luwq.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 修改密码请求参数。
 */
@Data
public class ChangePasswordRequest implements Serializable {

    private String userId;
    private String oldPassword;
    private String newPassword;
}
