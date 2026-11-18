package com.luwq.mapper;

import com.luwq.dto.UserQuery;
import com.luwq.entity.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysUserMapper {

    List<SysUser> selectPage(UserQuery query);

    long countPage(UserQuery query);

    SysUser selectById(@Param("id") String id);

    int insert(@Param("user") SysUser user);

    int update(@Param("user") SysUser user);

    int deleteById(@Param("id") String id);

    long countByUsername(@Param("username") String username, @Param("excludeId") String excludeId);

    int updatePassword(@Param("id") String id, @Param("password") String password);

    String selectPasswordById(@Param("id") String id);
}
