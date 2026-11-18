<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>权限管理</span>
          <el-button v-permission="'system:permission:add'" type="primary" :icon="Plus" @click="openDialog()">新增权限</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="query" class="search-form" @submit.prevent>
        <el-form-item label="权限名称">
          <el-input
            v-model="query.name"
            placeholder="请输入权限名称"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="权限编码">
          <el-input v-model="query.code" placeholder="请输入权限编码" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
          <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table
        :data="treeData"
        row-key="id"
        border
        stripe
        default-expand-all
        v-loading="loading"
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
        <el-table-column prop="module" label="模块" width="120" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button v-permission="'system:permission:edit'" link type="primary" @click="openDialog(row)">编辑</el-button>
            <el-button v-permission="'system:permission:delete'" link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="520px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="权限名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入权限名称" />
        </el-form-item>
        <el-form-item label="权限编码" prop="code">
          <el-input v-model="form.code" placeholder="如 system:user:list" />
        </el-form-item>
        <el-form-item label="上级权限" prop="parentId">
          <el-tree-select
            v-model="form.parentId"
            :data="parentOptions"
            :props="{ label: 'name', children: 'children' }"
            node-key="id"
            check-strictly
            default-expand-all
            placeholder="顶级权限"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio :value="1">目录</el-radio>
            <el-radio :value="2">菜单</el-radio>
            <el-radio :value="3">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="所属模块" prop="module">
          <el-input v-model="form.module" placeholder="如 system" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" />
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
import { computed, nextTick, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus, Search, Refresh } from '@element-plus/icons-vue'
import { createPermission, deletePermission, getPermissionList, updatePermission } from '@/api/permission'
import type { PermissionForm, PermissionNode, PermissionQuery, SysPermission } from '@/types'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新增权限')
const formRef = ref<FormInstance>()

const query = reactive<PermissionQuery>({ name: '', code: '' })
const treeData = ref<PermissionNode[]>([])
const form = reactive<PermissionForm>({
  id: undefined,
  name: '',
  code: '',
  parentId: 0,
  type: 1,
  module: '',
  sort: 0,
  status: 1
})

const rules = reactive<FormRules<PermissionForm>>({
  name: [{ required: true, message: '请输入权限名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入权限编码', trigger: 'blur' }]
})

// 上级权限下拉：虚拟"顶级权限"节点 + 全部权限；编辑时剔除自身及后代，避免成环
const parentOptions = computed<PermissionNode[]>(() => {
  const root: PermissionNode = {
    id: 0,
    name: '顶级权限',
    code: '',
    parentId: 0,
    type: 1,
    module: '',
    sort: 0,
    status: 1,
    createTime: '',
    children: filterTree(treeData.value, form.id)
  }
  return [root]
})

function filterTree(nodes: PermissionNode[], excludeId?: number): PermissionNode[] {
  return nodes
    .filter((node) => node.id !== excludeId)
    .map((node) => ({ ...node, children: filterTree(node.children, excludeId) }))
}

function buildTree(list: SysPermission[]): PermissionNode[] {
  const map = new Map<number, PermissionNode>()
  list.forEach((item) => map.set(item.id, { ...item, children: [] }))
  const roots: PermissionNode[] = []
  list.forEach((item) => {
    const node = map.get(item.id)!
    const parent = map.get(item.parentId)
    if (parent && parent.id !== item.id) {
      parent.children.push(node)
    } else {
      roots.push(node)
    }
  })
  return roots
}

async function loadData() {
  loading.value = true
  try {
    treeData.value = buildTree(await getPermissionList(query))
  } catch {
    /* 请求拦截器已统一提示错误 */
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  loadData()
}

function resetQuery() {
  query.name = ''
  query.code = ''
  loadData()
}

async function openDialog(row?: SysPermission) {
  dialogTitle.value = row ? '编辑权限' : '新增权限'
  Object.assign(
    form,
    row
      ? {
          id: row.id,
          name: row.name,
          code: row.code,
          parentId: row.parentId,
          type: row.type,
          module: row.module || '',
          sort: row.sort,
          status: row.status
        }
      : { id: undefined, name: '', code: '', parentId: 0, type: 1, module: '', sort: 0, status: 1 }
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

  const isEdit = form.id != null
  submitLoading.value = true
  try {
    if (isEdit) {
      await updatePermission(form)
    } else {
      await createPermission(form)
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

async function handleDelete(row: SysPermission) {
  try {
    await ElMessageBox.confirm(`确认删除权限「${row.name}」？`, '提示', { type: 'warning' })
  } catch {
    return
  }

  try {
    await deletePermission(row.id)
    ElMessage.success('删除成功')
    await loadData()
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
