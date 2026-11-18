package com.luwq.service;

import com.luwq.common.PageResult;
import com.luwq.dto.UserQuery;
import com.luwq.dto.UserSaveRequest;
import com.luwq.entity.SysUser;

public interface SysUserService {

    PageResult<SysUser> page(UserQuery query);

    void create(UserSaveRequest request);

    void update(UserSaveRequest request);

    void delete(String id);
}
