package com.luwq.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 角色分配权限请求参数。
 */
@Data
public class RolePermissionRequest implements Serializable {

    private List<Integer> permissionIds;
}
