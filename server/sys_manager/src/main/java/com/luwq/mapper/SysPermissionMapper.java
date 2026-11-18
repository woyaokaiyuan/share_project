package com.luwq.mapper;

import com.luwq.dto.PermissionQuery;
import com.luwq.entity.SysPermission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysPermissionMapper {

    List<SysPermission> selectList(PermissionQuery query);

    SysPermission selectById(@Param("id") Integer id);

    int insert(@Param("permission") SysPermission permission);

    int update(@Param("permission") SysPermission permission);

    int deleteById(@Param("id") Integer id);

    long countByCode(@Param("code") String code, @Param("excludeId") Integer excludeId);

    long countChildren(@Param("parentId") Integer parentId);
}
