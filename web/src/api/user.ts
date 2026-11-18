import request from './request'
import type { PageResult, SysUser, UserForm, UserQuery } from '@/types'

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
