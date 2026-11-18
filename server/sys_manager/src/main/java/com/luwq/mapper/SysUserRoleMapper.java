package com.luwq.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysUserRoleMapper {

    List<Integer> selectRoleIdsByUserId(@Param("userId") String userId);

    int deleteByUserId(@Param("userId") String userId);

    int batchInsert(@Param("userId") String userId, @Param("roleIds") List<Integer> roleIds);
}
