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
              <el-dropdown-item command="password">修改密码</el-dropdown-item>
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

  <el-dialog v-model="passwordDialogVisible" title="修改密码" width="450px">
    <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="90px">
      <el-form-item label="原密码" prop="oldPassword">
        <el-input v-model="passwordForm.oldPassword" type="password" show-password placeholder="请输入原密码" />
      </el-form-item>
      <el-form-item label="新密码" prop="newPassword">
        <el-input v-model="passwordForm.newPassword" type="password" show-password placeholder="请输入新密码" />
      </el-form-item>
      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input v-model="passwordForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="passwordDialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="passwordSubmitLoading" @click="submitPassword">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, nextTick, reactive, ref } from 'vue'
import { useRoute, useRouter, type RouteRecordRaw } from 'vue-router'
import { ElMessageBox, ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { useUserStore, usePermissionStore } from '@/store'
import { changePassword } from '@/api/user'
import { siteConfig } from '@/config'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const permissionStore = usePermissionStore()
const isCollapse = ref(false)
const nickname = computed(() => userStore.nickname)

// 修改密码相关
const passwordDialogVisible = ref(false)
const passwordSubmitLoading = ref(false)
const passwordFormRef = ref<FormInstance>()
const passwordForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })

const validateConfirm = (_rule: unknown, value: string, callback: (error?: Error) => void) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = reactive<FormRules>({
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' }
  ]
})

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
  } else if (command === 'password') {
    openPasswordDialog()
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

function openPasswordDialog() {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  passwordDialogVisible.value = true
  nextTick(() => passwordFormRef.value?.clearValidate())
}

async function submitPassword() {
  if (!passwordFormRef.value) return
  try {
    await passwordFormRef.value.validate()
  } catch {
    return
  }

  passwordSubmitLoading.value = true
  try {
    await changePassword({
      userId: userStore.userInfo?.id || '',
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    ElMessage.success('密码修改成功，请重新登录')
    passwordDialogVisible.value = false
    await userStore.logout()
    permissionStore.routes = []
    router.push('/login')
  } catch {
    /* 请求拦截器已统一提示错误 */
  } finally {
    passwordSubmitLoading.value = false
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
