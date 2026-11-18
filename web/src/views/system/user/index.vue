<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <el-button type="primary" :icon="Plus" @click="openDialog()">新增用户</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="query" class="search-form" @submit.prevent>
        <el-form-item label="用户名">
          <el-input
            v-model="query.username"
            placeholder="请输入用户名"
            clearable
            @keyup.enter="handleSearch"
          />
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
        <el-table-column prop="id" label="ID" width="190" />
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
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { nextTick, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus, Search, Refresh } from '@element-plus/icons-vue'
import { createUser, deleteUser, getUserList, updateUser } from '@/api/user'
import type { SysUser, UserForm, UserQuery } from '@/types'

const loading = ref(false)
const submitLoading = ref(false)
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('新增用户')
const formRef = ref<FormInstance>()

const query = reactive<UserQuery>({ username: '', status: '', page: 1, size: 10 })
const tableData = ref<SysUser[]>([])
const form = reactive<UserForm>({ id: '', username: '', nickname: '', email: '', status: 1 })

const rules = reactive<FormRules<UserForm>>({
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  email: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }]
})

async function loadData() {
  loading.value = true
  try {
    const result = await getUserList(query)
    tableData.value = result.list
    total.value = result.total
  } catch {
    /* 请求拦截器已统一提示错误 */
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  query.page = 1
  loadData()
}

function resetQuery() {
  query.username = ''
  query.status = ''
  query.page = 1
  loadData()
}

watch(() => [query.page, query.size], () => {
  loadData()
})

async function openDialog(row?: SysUser) {
  dialogTitle.value = row ? '编辑用户' : '新增用户'
  Object.assign(
    form,
    row
      ? { id: row.id, username: row.username, nickname: row.nickname, email: row.email, status: row.status }
      : { id: '', username: '', nickname: '', email: '', status: 1 }
  )
  dialogVisible.value = true
  await nextTick()
  formRef.value?.clearValidate()
}

async function submitForm() {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch {
    return
  }

  const isEdit = Boolean(form.id)
  submitLoading.value = true
  try {
    if (isEdit) {
      await updateUser(form)
    } else {
      await createUser(form)
    }
    ElMessage.success(isEdit ? '修改成功' : '新增成功')
    dialogVisible.value = false
    await loadData()
  } catch {
    /* 请求拦截器已统一提示错误 */
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(row: SysUser) {
  try {
    await ElMessageBox.confirm(`确认删除用户「${row.username}」？`, '提示', { type: 'warning' })
  } catch {
    return
  }

  try {
    await deleteUser(row.id)
    ElMessage.success('删除成功')
    if (tableData.value.length === 1 && query.page > 1) {
      query.page -= 1
    } else {
      await loadData()
    }
  } catch {
    /* 请求拦截器已统一提示错误 */
  }
}

onMounted(loadData)
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
