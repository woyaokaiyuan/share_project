package com.luwq.mapper;

import com.luwq.entity.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AuthMapper {

    SysUser selectByUsername(@Param("username") String username);

    SysUser selectById(@Param("id") String id);

    List<String> selectRoleCodesByUserId(@Param("userId") String userId);

    List<String> selectPermissionCodesByUserId(@Param("userId") String userId);
}
