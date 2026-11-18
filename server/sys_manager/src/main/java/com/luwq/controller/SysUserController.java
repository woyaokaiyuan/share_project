package com.luwq.controller;

import com.luwq.common.ApiResponse;
import com.luwq.common.PageResult;
import com.luwq.dto.ChangePasswordRequest;
import com.luwq.dto.ResetPasswordRequest;
import com.luwq.dto.UserQuery;
import com.luwq.dto.UserRoleRequest;
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

import java.util.List;

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

    @GetMapping("/{id}/roles")
    public ApiResponse<List<Integer>> getRoles(@PathVariable String id) {
        return ApiResponse.ok(userService.getRoleIds(id));
    }

    @PutMapping("/{id}/roles")
    public ApiResponse<Void> assignRoles(@PathVariable String id,
                                         @RequestBody UserRoleRequest request) {
        userService.assignRoles(id, request.getRoleIds());
        return ApiResponse.ok();
    }

    @PutMapping("/{id}/password")
    public ApiResponse<Void> resetPassword(@PathVariable String id,
                                           @RequestBody(required = false) ResetPasswordRequest request) {
        userService.resetPassword(id, request == null ? null : request.getPassword());
        return ApiResponse.ok();
    }

    @PutMapping("/change-password")
    public ApiResponse<Void> changePassword(@RequestBody ChangePasswordRequest request) {
        userService.changePassword(request.getUserId(), request.getOldPassword(), request.getNewPassword());
        return ApiResponse.ok();
    }
}
