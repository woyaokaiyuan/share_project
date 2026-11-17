<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <el-button type="primary" :icon="Plus" @click="openDialog()">新增用户</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="query" class="search-form">
        <el-form-item label="用户名">
          <el-input v-model="query.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
          <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="nickname" label="昵称" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="query.page"
        v-model:page-size="query.size"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 16px; justify-content: flex-end"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="form.nickname" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="dialogVisible = false">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh } from '@element-plus/icons-vue'

interface UserRow {
  id: number
  username: string
  nickname: string
  email: string
  status: number
  createTime: string
}

const loading = ref(false)
const total = ref(5)
const dialogVisible = ref(false)
const dialogTitle = ref('新增用户')
const query = reactive({ username: '', status: '' as number | '', page: 1, size: 10 })

const tableData = ref<UserRow[]>([
  { id: 1, username: 'admin', nickname: '系统管理员', email: 'admin@share.com', status: 1, createTime: '2026-01-01 10:00:00' },
  { id: 2, username: 'zhangsan', nickname: '张三', email: 'zhangsan@share.com', status: 1, createTime: '2026-02-15 14:30:00' },
  { id: 3, username: 'lisi', nickname: '李四', email: 'lisi@share.com', status: 0, createTime: '2026-03-08 09:12:00' },
  { id: 4, username: 'wangwu', nickname: '王五', email: 'wangwu@share.com', status: 1, createTime: '2026-04-20 16:45:00' },
  { id: 5, username: 'zhaoliu', nickname: '赵六', email: 'zhaoliu@share.com', status: 1, createTime: '2026-05-11 11:20:00' }
])

const form = reactive({ username: '', nickname: '', email: '', status: 1 })

function handleSearch() {
  loading.value = true
  setTimeout(() => { loading.value = false }, 300)
}

function resetQuery() {
  query.username = ''
  query.status = ''
  query.page = 1
  handleSearch()
}

function openDialog(row?: UserRow) {
  dialogTitle.value = row ? '编辑用户' : '新增用户'
  Object.assign(form, row || { username: '', nickname: '', email: '', status: 1 })
  dialogVisible.value = true
}

async function handleDelete(row: UserRow) {
  try {
    await ElMessageBox.confirm(`确认删除用户「${row.username}」？`, '提示', { type: 'warning' })
    tableData.value = tableData.value.filter((item) => item.id !== row.id)
    ElMessage.success('删除成功')
  } catch {
    /* 用户取消 */
  }
}
</script>

<style scoped>
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.search-form {
  margin-bottom: 16px;
}
</style>
