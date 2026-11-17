import request from './request'
import type { LoginParams, UserInfo } from '@/types'

export function loginApi(data: LoginParams): Promise<UserInfo & { token: string }> {
  return request.post('/auth/login', data)
}

export function getUserInfoApi(): Promise<UserInfo> {
  return request.get('/auth/userInfo')
}

export function logoutApi(): Promise<void> {
  return request.post('/auth/logout')
}
