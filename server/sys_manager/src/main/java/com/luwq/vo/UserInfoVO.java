package com.luwq.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 当前登录用户信息。
 */
@Data
public class UserInfoVO implements Serializable {

    private String id;
    private String username;
    private String nickname;
    private String avatar;
    private List<String> roles;
    private List<String> permissions;
}
