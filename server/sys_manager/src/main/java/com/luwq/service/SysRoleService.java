package com.luwq.service;

import com.luwq.common.PageResult;
import com.luwq.dto.RoleQuery;
import com.luwq.dto.RoleSaveRequest;
import com.luwq.entity.SysRole;

import java.util.List;

public interface SysRoleService {

    PageResult<SysRole> page(RoleQuery query);

    void create(RoleSaveRequest request);

    void update(RoleSaveRequest request);

    void delete(Integer id);

    /** 查询角色已分配的权限ID */
    List<Integer> getPermissionIds(Integer roleId);

    /** 保存角色权限（全量覆盖） */
    void assignPermissions(Integer roleId, List<Integer> permissionIds);
}
