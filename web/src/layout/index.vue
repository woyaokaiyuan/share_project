<template>
  <el-container class="layout">
    <el-aside :width="isCollapse ? '64px' : '220px'" class="layout-aside">
      <div class="logo" @click="router.push('/dashboard')">
        <el-icon :size="24"><Setting /></el-icon>
        <span v-show="!isCollapse">{{ siteConfig.title }}</span>
      </div>
      <el-scrollbar>
        <el-menu
          :default-active="route.path"
          :collapse="isCollapse"
          :unique-opened="true"
          router
          background-color="#001529"
          text-color="#bfcbd9"
          active-text-color="#409eff"
        >
          <template v-for="item in menuRoutes" :key="item.path">
            <el-menu-item
              v-if="!item.children || item.children.length === 0"
              :index="item.path"
            >
              <el-icon><component :is="item.meta?.icon || 'Menu'" /></el-icon>
              <template #title>{{ item.meta?.title }}</template>
            </el-menu-item>
            <el-sub-menu v-else :index="item.path">
              <template #title>
                <el-icon><component :is="item.meta?.icon || 'Menu'" /></el-icon>
                <span>{{ item.meta?.title }}</span>
              </template>
              <el-menu-item
                v-for="child in item.children"
                :key="child.path"
                :index="`${item.path}/${child.path}`"
              >
                <el-icon><component :is="child.meta?.icon || 'Menu'" /></el-icon>
                <template #title>{{ child.meta?.title }}</template>
              </el-menu-item>
            </el-sub-menu>
          </template>
        </el-menu>
      </el-scrollbar>
    </el-aside>

    <el-container>
      <el-header class="layout-header">
        <div class="header-left">
          <el-icon class="collapse-btn" :size="20" @click="isCollapse = !isCollapse">
            <component :is="isCollapse ? 'Expand' : 'Fold'" />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item
              v-for="item in breadcrumbs"
              :key="item.path"
            >{{ item.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <el-dropdown @command="handleCommand">
          <span class="user-info">
            <el-avatar :size="30" style="background: #409eff">
              {{ nickname.charAt(0) }}
            </el-avatar>
            <span>{{ nickname }}</span>
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="dashboard">首页</el-dropdown-item>
              <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>

      <el-main class="layout-main">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRoute, useRouter, type RouteRecordRaw } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useUserStore, usePermissionStore } from '@/store'
import { siteConfig } from '@/config'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const permissionStore = usePermissionStore()
const isCollapse = ref(false)
const nickname = computed(() => userStore.nickname)

const menuRoutes = computed(() => {
  const list: RouteRecordRaw[] = []
  permissionStore.routes.forEach((r) => {
    if (r.meta?.hidden || r.name === 'CatchAll') return
    if (r.path === '/' && r.children) {
      r.children.forEach((child) => {
        if (!child.meta?.hidden) {
          list.push({ ...child, path: `/${child.path}` })
        }
      })
    } else {
      list.push(r)
    }
  })
  return list
})

const breadcrumbs = computed(() =>
  route.matched
    .filter((r) => r.meta?.title && r.path !== '/')
    .map((r) => ({ path: r.path, title: r.meta.title as string }))
)

async function handleCommand(command: string) {
  if (command === 'dashboard') {
    router.push('/dashboard')
  } else if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确认退出登录？', '提示', { type: 'warning' })
      await userStore.logout()
      permissionStore.routes = []
      router.push('/login')
      ElMessage.success('已退出登录')
    } catch {
      /* 用户取消 */
    }
  }
}
</script>

<style scoped>
.layout {
  height: 100%;
}

.layout-aside {
  background-color: #001529;
  transition: width 0.3s;
  overflow: hidden;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 0 20px;
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  white-space: nowrap;
  cursor: pointer;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.el-menu {
  border-right: none;
}

.layout-header {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.collapse-btn {
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  outline: none;
}

.layout-main {
  background: #f5f7fa;
  padding: 0;
  overflow: auto;
}
</style>
