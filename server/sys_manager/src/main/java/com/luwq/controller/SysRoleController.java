package com.luwq.controller;

import com.luwq.common.ApiResponse;
import com.luwq.common.PageResult;
import com.luwq.dto.RoleQuery;
import com.luwq.dto.RoleSaveRequest;
import com.luwq.entity.SysRole;
import com.luwq.service.SysRoleService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/role")
public class SysRoleController {

    private final SysRoleService roleService;

    public SysRoleController(SysRoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/list")
    public ApiResponse<PageResult<SysRole>> list(RoleQuery query) {
        return ApiResponse.ok(roleService.page(query));
    }

    @PostMapping
    public ApiResponse<Void> create(@RequestBody RoleSaveRequest request) {
        roleService.create(request);
        return ApiResponse.ok();
    }

    @PutMapping
    public ApiResponse<Void> update(@RequestBody RoleSaveRequest request) {
        roleService.update(request);
        return ApiResponse.ok();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        roleService.delete(id);
        return ApiResponse.ok();
    }
}
