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
.welcome-card :deep(.el-card__body) {
  padding: 0;
}
</style>
