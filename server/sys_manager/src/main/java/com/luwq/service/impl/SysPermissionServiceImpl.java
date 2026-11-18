package com.luwq.service.impl;

import com.luwq.dto.PermissionQuery;
import com.luwq.dto.PermissionSaveRequest;
import com.luwq.entity.SysPermission;
import com.luwq.mapper.SysPermissionMapper;
import com.luwq.service.SysPermissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SysPermissionServiceImpl implements SysPermissionService {

    private final SysPermissionMapper permissionMapper;

    public SysPermissionServiceImpl(SysPermissionMapper permissionMapper) {
        this.permissionMapper = permissionMapper;
    }

    @Override
    public List<SysPermission> list(PermissionQuery query) {
        return permissionMapper.selectList(query);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(PermissionSaveRequest request) {
        String name = requireName(request.getName());
        String code = requireCode(request.getCode());
        checkCodeAvailable(code, null);

        int parentId = request.getParentId() == null ? 0 : request.getParentId();
        if (parentId != 0 && permissionMapper.selectById(parentId) == null) {
            throw new IllegalArgumentException("上级权限不存在");
        }

        SysPermission permission = new SysPermission();
        permission.setName(name);
        permission.setCode(code);
        permission.setParentId(parentId);
        permission.setType(request.getType() == null ? 1 : request.getType());
        permission.setModule(request.getModule());
        permission.setSort(request.getSort() == null ? 0 : request.getSort());
        permission.setStatus(request.getStatus() == null ? 1 : request.getStatus());
        permissionMapper.insert(permission);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(PermissionSaveRequest request) {
        if (request.getId() == null) {
            throw new IllegalArgumentException("权限ID不能为空");
        }
        if (permissionMapper.selectById(request.getId()) == null) {
            throw new IllegalArgumentException("权限不存在");
        }

        String name = requireName(request.getName());
        String code = requireCode(request.getCode());
        checkCodeAvailable(code, request.getId());

        int parentId = request.getParentId() == null ? 0 : request.getParentId();
        if (parentId != 0) {
            if (permissionMapper.selectById(parentId) == null) {
                throw new IllegalArgumentException("上级权限不存在");
            }
            if (isSelfOrDescendant(parentId, request.getId())) {
                throw new IllegalArgumentException("上级权限不能是自身或其子级");
            }
        }

        SysPermission permission = new SysPermission();
        permission.setId(request.getId());
        permission.setName(name);
        permission.setCode(code);
        permission.setParentId(parentId);
        permission.setType(request.getType() == null ? 1 : request.getType());
        permission.setModule(request.getModule());
        permission.setSort(request.getSort() == null ? 0 : request.getSort());
        permission.setStatus(request.getStatus() == null ? 1 : request.getStatus());
        permissionMapper.update(permission);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Integer id) {
        if (permissionMapper.countChildren(id) > 0) {
            throw new IllegalArgumentException("存在子权限，请先删除子权限");
        }
        permissionMapper.deleteById(id);
    }

    /** 从候选上级一路向上追溯，判断是否会形成环 */
    private boolean isSelfOrDescendant(int parentId, int selfId) {
        Integer current = parentId;
        int guard = 0;
        while (current != null && current != 0 && guard++ < 100) {
            if (current.equals(selfId)) {
                return true;
            }
            SysPermission parent = permissionMapper.selectById(current);
            if (parent == null) {
                return false;
            }
            current = parent.getParentId();
        }
        return false;
    }

    private String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("权限名称不能为空");
        }
        return name.trim();
    }

    private String requireCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("权限编码不能为空");
        }
        return code.trim();
    }

    private void checkCodeAvailable(String code, Integer excludeId) {
        if (permissionMapper.countByCode(code, excludeId) > 0) {
            throw new IllegalArgumentException("权限编码已存在");
        }
    }
}
