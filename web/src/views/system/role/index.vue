<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>角色管理</span>
          <el-button v-permission="'system:role:add'" type="primary" :icon="Plus" @click="openDialog()">新增角色</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="query" class="search-form" @submit.prevent>
        <el-form-item label="角色名称">
          <el-input
            v-model="query.name"
            placeholder="请输入角色名称"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="角色编码">
          <el-input v-model="query.code" placeholder="请输入角色编码" clearable />
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
        <el-table-column prop="name" label="角色名称" />
        <el-table-column prop="code" label="角色编码" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button v-permission="'system:role:edit'" link type="primary" @click="openDialog(row)">编辑</el-button>
            <el-button v-permission="'system:role:assign'" link type="warning" @click="openPermissionDialog(row)">分配权限</el-button>
            <el-button v-permission="'system:role:delete'" link type="danger" @click="handleDelete(row)">删除</el-button>
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

    <!-- 新增/编辑角色 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入角色编码" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" placeholder="请输入角色描述" />
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

    <!-- 分配权限 -->
    <el-dialog v-model="permDialogVisible" :title="permDialogTitle" width="500px">
      <div v-loading="permLoading" style="min-height: 200px">
        <el-tree
          v-if="permissionTree.length > 0"
          ref="permTreeRef"
          :data="permissionTree"
          :props="{ label: 'name', children: 'children' }"
          node-key="id"
          show-checkbox
          check-strictly
          default-expand-all
          @check="handleTreeCheck"
        />
        <el-empty v-else description="暂无权限数据" />
      </div>
      <template #footer>
        <el-button @click="permDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="permSubmitLoading" @click="submitPermissions">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { nextTick, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus, Search, Refresh } from '@element-plus/icons-vue'
import { getPermissionList } from '@/api/permission'
import {
  assignRolePermissions,
  createRole,
  deleteRole,
  getRoleList,
  getRolePermissionIds,
  updateRole
} from '@/api/role'
import type { PermissionNode, RoleForm, RoleQuery, SysPermission, SysRole } from '@/types'

const loading = ref(false)
const submitLoading = ref(false)
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('新增角色')
const formRef = ref<FormInstance>()

const query = reactive<RoleQuery>({ name: '', code: '', status: '', page: 1, size: 10 })
const tableData = ref<SysRole[]>([])
const form = reactive<RoleForm>({ id: undefined, name: '', code: '', description: '', status: 1 })

const rules = reactive<FormRules<RoleForm>>({
  name: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入角色编码', trigger: 'blur' }]
})

// 分配权限相关
interface PermissionTreeRef {
  setCheckedKeys: (keys: (string | number)[]) => void
  setChecked: (key: string | number, checked: boolean, deep?: boolean) => void
  getCheckedKeys: (leafOnly?: boolean) => (string | number)[]
  getHalfCheckedKeys: () => (string | number)[]
}

const permDialogVisible = ref(false)
const permDialogTitle = ref('分配权限')
const permLoading = ref(false)
const permSubmitLoading = ref(false)
const currentRoleId = ref<number | null>(null)
const permissionTree = ref<PermissionNode[]>([])
const permTreeRef = ref<PermissionTreeRef>()
// 程序化设置勾选状态时为 true，避免 @check 递归触发
const isProgrammaticCheck = ref(false)

function buildPermissionTree(list: SysPermission[]): PermissionNode[] {
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

/** 递归收集某节点的全部后代ID */
function collectDescendantIds(node: PermissionNode): number[] {
  const ids: number[] = []
  const walk = (list: PermissionNode[]) => {
    list.forEach((child) => {
      ids.push(child.id)
      if (child.children && child.children.length > 0) {
        walk(child.children)
      }
    })
  }
  walk(node.children || [])
  return ids
}

/** 按 id 建立节点索引，便于沿 parentId 向上追溯祖先 */
function buildNodeMap(): Map<number, PermissionNode> {
  const map = new Map<number, PermissionNode>()
  const walk = (list: PermissionNode[]) => {
    list.forEach((node) => {
      map.set(node.id, node)
      if (node.children && node.children.length > 0) {
        walk(node.children)
      }
    })
  }
  walk(permissionTree.value)
  return map
}

/** 从当前节点向上收集全部祖先ID（不含自身） */
function collectAncestorIds(node: PermissionNode): number[] {
  const nodeMap = buildNodeMap()
  const ancestorIds: number[] = []
  let parentId = node.parentId
  let guard = 0
  while (parentId && parentId !== 0 && guard++ < 20) {
    ancestorIds.push(parentId)
    const parent = nodeMap.get(parentId)
    if (!parent) break
    parentId = parent.parentId
  }
  return ancestorIds
}

/** 勾选节点时的联动：目录向下全联动；二三级仅向上联动，取消不回退 */
function handleTreeCheck(node: PermissionNode, info: { checked: boolean }) {
  if (isProgrammaticCheck.value) return
  if (!permTreeRef.value) return

  const isTopDirectory = node.type === 1 || node.parentId === 0

  let targetIds: number[] = []
  let checked = info.checked

  if (isTopDirectory) {
    // 一级目录：勾选/取消都联动全部后代
    targetIds = collectDescendantIds(node)
  } else if (info.checked) {
    // 二级菜单、三级按钮：仅在勾选时向上联动祖先
    targetIds = collectAncestorIds(node)
    checked = true
  } else {
    // 取消二级/三级时不动上级
    return
  }

  if (targetIds.length === 0) return

  isProgrammaticCheck.value = true
  try {
    targetIds.forEach((id) => permTreeRef.value?.setChecked(id, checked, true))
  } finally {
    isProgrammaticCheck.value = false
  }
}

async function loadData() {
  loading.value = true
  try {
    const result = await getRoleList(query)
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
  query.name = ''
  query.code = ''
  query.status = ''
  query.page = 1
  loadData()
}

watch(() => [query.page, query.size], () => {
  loadData()
})

async function openDialog(row?: SysRole) {
  dialogTitle.value = row ? '编辑角色' : '新增角色'
  Object.assign(
    form,
    row
      ? {
          id: row.id,
          name: row.name,
          code: row.code,
          description: row.description,
          status: row.status
        }
      : { id: undefined, name: '', code: '', description: '', status: 1 }
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
      await updateRole(form)
    } else {
      await createRole(form)
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

async function openPermissionDialog(row: SysRole) {
  currentRoleId.value = row.id
  permDialogTitle.value = `分配权限 - ${row.name}`
  permissionTree.value = []   // 先清空，让 el-tree 重新挂载，避免残留勾选状态
  permDialogVisible.value = true
  permLoading.value = true
  try {
    const [permissions, checkedIds] = await Promise.all([
      getPermissionList({ name: '', code: '' }),
      getRolePermissionIds(row.id)
    ])
    permissionTree.value = buildPermissionTree(permissions)
    // 独立勾选模式：按数据库保存的权限ID原样回填（含目录、二级菜单、按钮）
    await nextTick()
    isProgrammaticCheck.value = true
    try {
      permTreeRef.value?.setCheckedKeys(checkedIds)
    } finally {
      isProgrammaticCheck.value = false
    }
  } catch {
    /* 请求拦截器已统一提示错误 */
  } finally {
    permLoading.value = false
  }
}

async function submitPermissions() {
  if (currentRoleId.value == null || !permTreeRef.value) return
  // 提交所有实际勾选的节点ID（含二级菜单与按钮），二级菜单可独立保存、三级可留空
  const checkedIds = permTreeRef.value.getCheckedKeys() as number[]
  permSubmitLoading.value = true
  try {
    await assignRolePermissions(currentRoleId.value, checkedIds)
    ElMessage.success('权限分配成功')
    permDialogVisible.value = false
  } catch {
    /* 请求拦截器已统一提示错误 */
  } finally {
    permSubmitLoading.value = false
  }
}

async function handleDelete(row: SysRole) {
  try {
    await ElMessageBox.confirm(`确认删除角色「${row.name}」？`, '提示', { type: 'warning' })
  } catch {
    return
  }

  try {
    await deleteRole(row.id)
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
