package com.luwq.mapper;

import com.luwq.dto.RoleQuery;
import com.luwq.entity.SysRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysRoleMapper {

    List<SysRole> selectPage(RoleQuery query);

    long countPage(RoleQuery query);

    SysRole selectById(@Param("id") Integer id);

    int insert(@Param("role") SysRole role);

    int update(@Param("role") SysRole role);

    int deleteById(@Param("id") Integer id);

    long countByCode(@Param("code") String code, @Param("excludeId") Integer excludeId);
}
