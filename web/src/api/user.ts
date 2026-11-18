import request from './request'
import type { ChangePasswordParams, PageResult, SysUser, UserForm, UserQuery } from '@/types'

export function getUserList(params: UserQuery): Promise<PageResult<SysUser>> {
  const { status, ...rest } = params
  return request.get('/user/list', {
    params: {
      ...rest,
      ...(status === '' || status === null ? {} : { status })
    }
  })
}

export function createUser(data: UserForm): Promise<void> {
  return request.post('/user', data)
}

export function updateUser(data: UserForm): Promise<void> {
  return request.put('/user', data)
}

export function deleteUser(id: string): Promise<void> {
  return request.delete(`/user/${encodeURIComponent(id)}`)
}

export function getUserRoleIds(id: string): Promise<number[]> {
  return request.get(`/user/${encodeURIComponent(id)}/roles`)
}

export function assignUserRoles(id: string, roleIds: number[]): Promise<void> {
  return request.put(`/user/${encodeURIComponent(id)}/roles`, { roleIds })
}

export function resetUserPassword(id: string, password?: string): Promise<void> {
  return request.put(`/user/${encodeURIComponent(id)}/password`, { password })
}

export function changePassword(data: ChangePasswordParams): Promise<void> {
  return request.put('/user/change-password', data)
}
