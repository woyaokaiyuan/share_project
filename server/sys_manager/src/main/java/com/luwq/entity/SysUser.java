package com.luwq.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统用户实体，对应 sys_user 表。
 */
@Data
public class SysUser implements Serializable {

    private String id;
    private String username;

    @JsonIgnore
    private String password;

    private String nickname;
    private String email;
    private String phone;
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    private Integer userType;
    private Integer permVersion;
}
