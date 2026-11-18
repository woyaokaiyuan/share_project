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

export interface SysUser {
  id: string
  username: string
  nickname: string
  email: string
  phone?: string
  status: number
  createTime: string
  updateTime?: string
}

export interface UserQuery {
  username: string
  status: number | ''
  page: number
  size: number
}

export interface UserForm {
  id?: string
  username: string
  nickname: string
  email: string
  status: number
}

export interface SysRole {
  id: number
  name: string
  code: string
  description: string
  status: number
  createTime: string
}

export interface RoleQuery {
  name: string
  code: string
  status: number | ''
  page: number
  size: number
}

export interface RoleForm {
  id?: number
  name: string
  code: string
  description: string
  status: number
}

export interface SysPermission {
  id: number
  name: string
  code: string
  parentId: number
  type: number
  module: string
  sort: number
  status: number
  createTime: string
}

export interface PermissionNode extends SysPermission {
  children: PermissionNode[]
}

export interface PermissionQuery {
  name: string
  code: string
}

export interface PermissionForm {
  id?: number
  name: string
  code: string
  parentId: number
  type: number
  module: string
  sort: number
  status: number
}

export interface ChangePasswordParams {
  userId: string
  oldPassword: string
  newPassword: string
}
