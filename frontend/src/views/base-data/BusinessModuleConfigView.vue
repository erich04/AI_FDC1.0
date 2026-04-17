<template>
  <div class="page">
    <el-card shadow="never">
      <template #header>
        <div class="head">
          <strong>业务模块配置</strong>
          <el-button type="primary" @click="openCreate">新增</el-button>
        </div>
      </template>
      <el-table :data="flatRows" border>
        <el-table-column prop="moduleCode" label="编码" min-width="140" />
        <el-table-column prop="moduleName" label="名称" min-width="180" />
        <el-table-column prop="parentCode" label="上级" min-width="140">
          <template #default="{ row }">{{ row.parentCode || 'ROOT' }}</template>
        </el-table-column>
        <el-table-column prop="levelNum" label="层级" width="80" />
        <el-table-column prop="sortOrder" label="排序" width="90" />
        <el-table-column prop="enabledFlag" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.enabledFlag === 'Y' ? 'success' : 'info'">{{ row.enabledFlag === 'Y' ? '启用' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="visible" :title="mode === 'create' ? '新增业务模块' : '编辑业务模块'" width="620px">
      <el-form label-position="top">
        <el-form-item label="业务模块编码" required>
          <el-input v-model="form.moduleCode" :disabled="mode === 'edit'" />
        </el-form-item>
        <el-form-item label="业务模块名称" required>
          <el-input v-model="form.moduleName" />
        </el-form-item>
        <el-form-item label="上级编码">
          <el-input v-model="form.parentCode" placeholder="留空为根节点" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.enabledFlag">
            <el-radio value="Y">启用</el-radio>
            <el-radio value="N">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ElMessage, ElMessageBox } from 'element-plus'
import { computed, onMounted, reactive, ref } from 'vue'
import { createBusinessModule, deleteBusinessModule, fetchBusinessModuleTree, updateBusinessModule, type BusinessModuleCommand } from '../../api/modules/businessModule'
import type { BusinessModuleNode } from '../../types'

const tree = ref<BusinessModuleNode[]>([])
const visible = ref(false)
const mode = ref<'create' | 'edit'>('create')
const form = reactive<BusinessModuleCommand>({
  moduleCode: '',
  moduleName: '',
  parentCode: '',
  enabledFlag: 'Y',
  sortOrder: 1,
  securityLevel: '公开',
  integrationType: '不集成',
  description: '',
  remark: ''
})

const flatten = (nodes: BusinessModuleNode[]): BusinessModuleNode[] => nodes.flatMap((n) => [n, ...flatten(n.children || [])])
const flatRows = computed(() => flatten(tree.value))

const load = async () => {
  tree.value = await fetchBusinessModuleTree()
}

const openCreate = () => {
  mode.value = 'create'
  Object.assign(form, { moduleCode: '', moduleName: '', parentCode: '', enabledFlag: 'Y', sortOrder: 1, securityLevel: '公开', integrationType: '不集成', description: '', remark: '' })
  visible.value = true
}

const openEdit = (row: BusinessModuleNode) => {
  mode.value = 'edit'
  Object.assign(form, {
    moduleCode: row.moduleCode,
    moduleName: row.moduleName,
    parentCode: row.parentCode || '',
    enabledFlag: row.enabledFlag,
    sortOrder: row.sortOrder,
    securityLevel: row.securityLevel || '公开',
    integrationType: row.integrationType || '不集成',
    description: row.description || '',
    remark: row.remark || ''
  })
  visible.value = true
}

const save = async () => {
  if (!form.moduleCode.trim() || !form.moduleName.trim()) {
    ElMessage.warning('请填写编码和名称')
    return
  }
  if (mode.value === 'create') {
    await createBusinessModule({ ...form, moduleCode: form.moduleCode.trim(), moduleName: form.moduleName.trim(), parentCode: form.parentCode || undefined })
  } else {
    await updateBusinessModule(form.moduleCode, { ...form, moduleName: form.moduleName.trim(), parentCode: form.parentCode || undefined })
  }
  visible.value = false
  await load()
  ElMessage.success('保存成功')
}

const remove = async (row: BusinessModuleNode) => {
  await ElMessageBox.confirm(`确认删除 ${row.moduleCode} 吗？`, '提示', { type: 'warning' })
  await deleteBusinessModule(row.moduleCode)
  await load()
  ElMessage.success('删除成功')
}

onMounted(load)
</script>

<style scoped>
.page { display: grid; gap: 16px; }
.head { display: flex; justify-content: space-between; align-items: center; }
</style>
