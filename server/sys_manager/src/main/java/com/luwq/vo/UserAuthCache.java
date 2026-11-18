package com.luwq.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 权限缓存对象，存入 Redis 的 auth:permissions:{userId}。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthCache implements Serializable {

    private List<String> roles;
    private List<String> permissions;
}
