package com.luwq.controller;

import com.luwq.common.ApiResponse;
import com.luwq.common.PageResult;
import com.luwq.dto.UserQuery;
import com.luwq.dto.UserSaveRequest;
import com.luwq.entity.SysUser;
import com.luwq.service.SysUserService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class SysUserController {

    private final SysUserService userService;

    public SysUserController(SysUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/list")
    public ApiResponse<PageResult<SysUser>> list(UserQuery query) {
        return ApiResponse.ok(userService.page(query));
    }

    @PostMapping
    public ApiResponse<Void> create(@RequestBody UserSaveRequest request) {
        userService.create(request);
        return ApiResponse.ok();
    }

    @PutMapping
    public ApiResponse<Void> update(@RequestBody UserSaveRequest request) {
        userService.update(request);
        return ApiResponse.ok();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        userService.delete(id);
        return ApiResponse.ok();
    }
}
