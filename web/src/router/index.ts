import { createRouter, createWebHistory } from 'vue-router'
import { constantRoutes, lastRoute } from './routes'
import { useUserStore, usePermissionStore } from '@/store'
import { getToken } from '@/utils/auth'
import { siteConfig } from '@/config'

const router = createRouter({
  history: createWebHistory(),
  routes: [...constantRoutes, lastRoute],
  scrollBehavior: () => ({ left: 0, top: 0 })
})

const whiteList = ['/login', '/404']

router.beforeEach(async (to, from, next) => {
  document.title = `${to.meta.title ? to.meta.title + ' - ' : ''}${siteConfig.documentTitle}`

  const token = getToken()
  if (!token) {
    if (whiteList.includes(to.path)) {
      next()
    } else {
      next(`/login?redirect=${encodeURIComponent(to.fullPath)}`)
    }
    return
  }

  if (to.path === '/login') {
    next({ path: '/' })
    return
  }

  const permissionStore = usePermissionStore()
  if (permissionStore.routes.length === 0) {
    const userStore = useUserStore()
    try {
      if (!userStore.userInfo) await userStore.getInfo()
      const routes = permissionStore.buildRoutes(userStore.userInfo)
      routes.forEach((route) => {
        if (route.name && !router.hasRoute(route.name as string)) {
          router.addRoute(route)
        }
      })
      router.addRoute(lastRoute)
      next({ ...to, replace: true })
    } catch {
      await userStore.logout()
      next(`/login?redirect=${encodeURIComponent(to.fullPath)}`)
    }
    return
  }

  next()
})

export default router
