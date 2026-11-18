package com.luwq.service;

import com.luwq.dto.PermissionQuery;
import com.luwq.dto.PermissionSaveRequest;
import com.luwq.entity.SysPermission;

import java.util.List;

public interface SysPermissionService {

    List<SysPermission> list(PermissionQuery query);

    void create(PermissionSaveRequest request);

    void update(PermissionSaveRequest request);

    void delete(Integer id);
}
