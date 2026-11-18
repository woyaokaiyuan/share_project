package com.luwq.controller;

import com.luwq.common.ApiResponse;
import com.luwq.dto.PermissionQuery;
import com.luwq.dto.PermissionSaveRequest;
import com.luwq.entity.SysPermission;
import com.luwq.service.SysPermissionService;
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
@RequestMapping("/permission")
public class SysPermissionController {

    private final SysPermissionService permissionService;

    public SysPermissionController(SysPermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping("/list")
    public ApiResponse<List<SysPermission>> list(PermissionQuery query) {
        return ApiResponse.ok(permissionService.list(query));
    }

    @PostMapping
    public ApiResponse<Void> create(@RequestBody PermissionSaveRequest request) {
        permissionService.create(request);
        return ApiResponse.ok();
    }

    @PutMapping
    public ApiResponse<Void> update(@RequestBody PermissionSaveRequest request) {
        permissionService.update(request);
        return ApiResponse.ok();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        permissionService.delete(id);
        return ApiResponse.ok();
    }
}
