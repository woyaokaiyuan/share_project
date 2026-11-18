import request from './request'
import type { PageResult, RoleForm, RoleQuery, SysRole } from '@/types'

export function getRoleList(params: RoleQuery): Promise<PageResult<SysRole>> {
  const { status, ...rest } = params
  return request.get('/role/list', {
    params: {
      ...rest,
      ...(status === '' || status === null ? {} : { status })
    }
  })
}

export function createRole(data: RoleForm): Promise<void> {
  return request.post('/role', data)
}

export function updateRole(data: RoleForm): Promise<void> {
  return request.put('/role', data)
}

export function deleteRole(id: number): Promise<void> {
  return request.delete(`/role/${id}`)
}

export function getRolePermissionIds(id: number): Promise<number[]> {
  return request.get(`/role/${id}/permissions`)
}

export function assignRolePermissions(id: number, permissionIds: number[]): Promise<void> {
  return request.put(`/role/${id}/permissions`, { permissionIds })
}
