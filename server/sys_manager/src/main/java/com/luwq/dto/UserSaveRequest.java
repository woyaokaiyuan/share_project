package com.luwq.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 新增/编辑用户请求参数。
 */
@Data
public class UserSaveRequest implements Serializable {

    /** 编辑时必传，新增时为空 */
    private String id;
    private String username;
    private String nickname;
    private String email;
    private String phone;
    private Integer status = 1;
}
