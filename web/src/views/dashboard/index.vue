<template>
  <div class="page-container">
    <el-row :gutter="16">
      <el-col v-for="card in cards" :key="card.title" :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon" :style="{ background: card.color }">
            <el-icon :size="24"><component :is="card.icon" /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ card.value }}</div>
            <div class="stat-title">{{ card.title }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="welcome-card" style="margin-top: 16px">
      <template #header><span>欢迎使用</span></template>
      <p>这是基于 Vue3 + Element Plus + Pinia 的后台管理框架，已集成：</p>
      <ul>
        <li>登录鉴权与 Token 持久化</li>
        <li>动态路由与 RBAC 按钮级权限</li>
        <li>侧边菜单、面包屑、用户下拉</li>
        <li>axios 请求封装与 401 自动跳转</li>
      </ul>
      <p style="color: #909399; font-size: 13px">
        当前为 Mock 演示模式，配置 <code>.env.development</code> 中
        <code>VITE_USE_MOCK=false</code> 即可对接 Spring Boot 后端。
      </p>
    </el-card>
  </div>
</template>

<script setup lang="ts">
const cards = [
  { title: '用户总数', value: '1,024', icon: 'User', color: '#409eff' },
  { title: '角色数量', value: '8', icon: 'UserFilled', color: '#67c23a' },
  { title: '权限数量', value: '56', icon: 'Lock', color: '#e6a23c' },
  { title: '今日访问', value: '3,872', icon: 'TrendCharts', color: '#f56c6c' }
]
</script>

<style scoped>
.stat-card :deep(.el-card__body) {
  display: flex;
  align-items: center;
  gap: 16px;
}
.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}
.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}
.stat-title {
  color: #909399;
  font-size: 13px;
  margin-top: 4px;
}
</style>
