package com.luwq.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 用户分配角色请求参数。
 */
@Data
public class UserRoleRequest implements Serializable {

    private List<Integer> roleIds;
}
