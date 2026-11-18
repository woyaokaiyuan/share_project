import request from './request'
import type { PermissionForm, PermissionQuery, SysPermission } from '@/types'

export function getPermissionList(params: PermissionQuery): Promise<SysPermission[]> {
  return request.get('/permission/list', { params })
}

export function createPermission(data: PermissionForm): Promise<void> {
  return request.post('/permission', data)
}

export function updatePermission(data: PermissionForm): Promise<void> {
  return request.put('/permission', data)
}

export function deletePermission(id: number): Promise<void> {
  return request.delete(`/permission/${id}`)
}
