package com.luwq.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysRolePermissionMapper {

    List<Integer> selectPermissionIdsByRoleId(@Param("roleId") Integer roleId);

    int deleteByRoleId(@Param("roleId") Integer roleId);

    int batchInsert(@Param("roleId") Integer roleId, @Param("permissionIds") List<Integer> permissionIds);
}
