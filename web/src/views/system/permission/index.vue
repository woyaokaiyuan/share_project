<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>权限管理</span>
          <el-button type="primary" :icon="Plus">新增权限</el-button>
        </div>
      </template>
      <el-table
        :data="tableData"
        row-key="id"
        border
        default-expand-all
        :tree-props="{ children: 'children' }"
      >
        <el-table-column prop="name" label="权限名称" min-width="180" />
        <el-table-column prop="code" label="权限编码" min-width="200" />
        <el-table-column prop="type" label="类型" width="90">
          <template #default="{ row }">
            <el-tag :type="row.type === 1 ? 'primary' : row.type === 2 ? 'success' : 'warning'">
              {{ row.type === 1 ? '目录' : row.type === 2 ? '菜单' : '按钮' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Plus } from '@element-plus/icons-vue'

const tableData = ref([
  {
    id: 1, name: '系统管理', code: 'system', type: 1, sort: 1, status: 1,
    children: [
      { id: 2, name: '用户管理', code: 'system:user:list', type: 2, sort: 1, status: 1 },
      { id: 3, name: '用户新增', code: 'system:user:add', type: 3, sort: 1, status: 1 },
      { id: 4, name: '角色管理', code: 'system:role:list', type: 2, sort: 2, status: 1 },
      { id: 5, name: '权限管理', code: 'system:permission:list', type: 2, sort: 3, status: 1 }
    ]
  }
])
</script>

<style scoped>
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
</style>
