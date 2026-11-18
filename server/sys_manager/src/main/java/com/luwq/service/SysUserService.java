package com.luwq.service;

import com.luwq.common.PageResult;
import com.luwq.dto.UserQuery;
import com.luwq.dto.UserSaveRequest;
import com.luwq.entity.SysUser;

import java.util.List;

public interface SysUserService {

    PageResult<SysUser> page(UserQuery query);

    void create(UserSaveRequest request);

    void update(UserSaveRequest request);

    void delete(String id);

    /** 查询用户已分配的角色ID */
    List<Integer> getRoleIds(String userId);

    /** 保存用户角色（全量覆盖） */
    void assignRoles(String userId, List<Integer> roleIds);

    /** 重置用户密码，password 为空时使用默认密码 */
    void resetPassword(String userId, String password);

    /** 修改密码：校验旧密码后更新为新密码 */
    void changePassword(String userId, String oldPassword, String newPassword);
}
