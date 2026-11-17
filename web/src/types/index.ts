export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

export interface LoginParams {
  username: string
  password: string
}

export interface UserInfo {
  id: string
  username: string
  nickname: string
  avatar?: string
  roles: string[]
  permissions: string[]
}

export interface MenuItem {
  id: number
  name: string
  path: string
  component?: string
  icon?: string
  title: string
  parentId: number
  sort?: number
  children?: MenuItem[]
}

export interface PageResult<T> {
  list: T[]
  total: number
}
