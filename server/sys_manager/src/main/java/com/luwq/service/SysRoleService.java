package com.luwq.service;

import com.luwq.common.PageResult;
import com.luwq.dto.RoleQuery;
import com.luwq.dto.RoleSaveRequest;
import com.luwq.entity.SysRole;

public interface SysRoleService {

    PageResult<SysRole> page(RoleQuery query);

    void create(RoleSaveRequest request);

    void update(RoleSaveRequest request);

    void delete(Integer id);
}
