package com.luwq.service.impl;

import com.luwq.common.PageResult;
import com.luwq.dto.RoleQuery;
import com.luwq.dto.RoleSaveRequest;
import com.luwq.entity.SysRole;
import com.luwq.mapper.SysRoleMapper;
import com.luwq.mapper.SysRolePermissionMapper;
import com.luwq.service.SysRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class SysRoleServiceImpl implements SysRoleService {

    private final SysRoleMapper roleMapper;
    private final SysRolePermissionMapper rolePermissionMapper;

    public SysRoleServiceImpl(SysRoleMapper roleMapper, SysRolePermissionMapper rolePermissionMapper) {
        this.roleMapper = roleMapper;
        this.rolePermissionMapper = rolePermissionMapper;
    }

    @Override
    public PageResult<SysRole> page(RoleQuery query) {
        List<SysRole> list = roleMapper.selectPage(query);
        long total = roleMapper.countPage(query);
        return new PageResult<>(list, total);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(RoleSaveRequest request) {
        String code = requireCode(request.getCode());
        String name = requireName(request.getName());
        checkCodeAvailable(code, null);

        SysRole role = new SysRole();
        role.setName(name);
        role.setCode(code);
        role.setDescription(request.getDescription());
        role.setStatus(request.getStatus() == null ? 1 : request.getStatus());
        roleMapper.insert(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(RoleSaveRequest request) {
        if (request.getId() == null) {
            throw new IllegalArgumentException("角色ID不能为空");
        }
        if (roleMapper.selectById(request.getId()) == null) {
            throw new IllegalArgumentException("角色不存在");
        }
        String code = requireCode(request.getCode());
        String name = requireName(request.getName());
        checkCodeAvailable(code, request.getId());

        SysRole role = new SysRole();
        role.setId(request.getId());
        role.setName(name);
        role.setCode(code);
        role.setDescription(request.getDescription());
        role.setStatus(request.getStatus() == null ? 1 : request.getStatus());
        roleMapper.update(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Integer id) {
        roleMapper.deleteById(id);
    }

    @Override
    public List<Integer> getPermissionIds(Integer roleId) {
        if (roleId == null || roleMapper.selectById(roleId) == null) {
            throw new IllegalArgumentException("角色不存在");
        }
        return rolePermissionMapper.selectPermissionIdsByRoleId(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(Integer roleId, List<Integer> permissionIds) {
        if (roleId == null || roleMapper.selectById(roleId) == null) {
            throw new IllegalArgumentException("角色不存在");
        }
        // 全量覆盖：先清空旧授权，再插入新授权
        rolePermissionMapper.deleteByRoleId(roleId);
        if (permissionIds == null) {
            return;
        }
        List<Integer> ids = permissionIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (!ids.isEmpty()) {
            rolePermissionMapper.batchInsert(roleId, ids);
        }
    }

    private String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("角色名称不能为空");
        }
        return name.trim();
    }

    private String requireCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("角色编码不能为空");
        }
        return code.trim();
    }

    private void checkCodeAvailable(String code, Integer excludeId) {
        if (roleMapper.countByCode(code, excludeId) > 0) {
            throw new IllegalArgumentException("角色编码已存在");
        }
    }
}
