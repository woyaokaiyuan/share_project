import { defineStore } from 'pinia'
import type { RouteRecordRaw } from 'vue-router'
import { constantRoutes, asyncRoutes } from '@/router/routes'
import type { UserInfo } from '@/types'

function hasPermission(route: RouteRecordRaw, permissions: string[]): boolean {
  const permission = route.meta?.permission
  return (
    !permission ||
    permissions.includes(permission) ||
    permissions.includes('*:*:*') ||
    permissions.includes('admin')
  )
}

function filterRoutes(routes: RouteRecordRaw[], permissions: string[]): RouteRecordRaw[] {
  const result: RouteRecordRaw[] = []
  routes.forEach((route) => {
    const temp = { ...route }
    if (hasPermission(temp, permissions)) {
      if (temp.children) {
        temp.children = filterRoutes(temp.children, permissions)
      }
      result.push(temp)
    }
  })
  return result
}

export const usePermissionStore = defineStore('permission', {
  state: () => ({
    routes: [] as RouteRecordRaw[]
  }),
  actions: {
    buildRoutes(userInfo: UserInfo | null) {
      const permissions = userInfo?.permissions || []
      // 无权限时不放行任何需要权限的路由，避免越权访问
      const accessible = permissions.length
        ? filterRoutes(asyncRoutes, permissions)
        : []
      this.routes = [...constantRoutes, ...accessible]
      return this.routes
    }
  }
})
