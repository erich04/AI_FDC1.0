<template>
  <div class="signatory-page">
    <section class="page-banner">
      <div>
        <span>Department Signatory</span>
        <h2>权签人维护</h2>
        <p>维护部门层级与部门权签人，用于借阅、审批和档案业务流转时快速带出处理人。</p>
      </div>
    </section>

    <el-card shadow="never" class="query-card">
      <el-form :model="query" label-position="top" class="query-grid">
        <el-form-item label="部门名称">
          <el-input v-model="query.departmentName" clearable placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="部门权签人">
          <el-select v-model="query.signatories" multiple filterable clearable collapse-tags collapse-tags-tooltip placeholder="请选择部门权签人">
            <el-option v-for="item in signatoryOptions" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <div class="query-actions">
          <el-button type="primary" @click="loadRows">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </div>
      </el-form>
    </el-card>

    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="table-head">
          <strong>权签人列表</strong>
          <div class="toolbar">
            <el-button type="primary" @click="openDialog()">新增</el-button>
            <el-button :disabled="!currentRow" @click="openDialog(currentRow || undefined)">编辑</el-button>
            <el-button @click="triggerImport">导入</el-button>
            <el-button @click="exportRows">导出</el-button>
            <input ref="fileInputRef" class="hidden-input" type="file" accept=".csv,text/csv" @change="handleImportFile" />
          </div>
        </div>
      </template>
      <el-table :data="rows" border highlight-current-row empty-text="暂无权签人维护数据" @current-change="onCurrentChange">
        <el-table-column prop="firstLevelDepartment" label="一级部门" min-width="150" />
        <el-table-column prop="secondLevelDepartment" label="二级部门" min-width="150">
          <template #default="{ row }">{{ row.secondLevelDepartment || '-' }}</template>
        </el-table-column>
        <el-table-column prop="thirdLevelDepartment" label="三级部门" min-width="150">
          <template #default="{ row }">{{ row.thirdLevelDepartment || '-' }}</template>
        </el-table-column>
        <el-table-column prop="fourthLevelDepartment" label="四级部门" min-width="150">
          <template #default="{ row }">{{ row.fourthLevelDepartment || '-' }}</template>
        </el-table-column>
        <el-table-column label="部门权签人" min-width="220">
          <template #default="{ row }">
            <el-tag v-for="item in row.signatories" :key="item" class="tag-chip" type="primary">{{ item }}</el-tag>
            <span v-if="!row.signatories?.length">-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
            <el-button link type="danger" @click="removeRow(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogMode === 'create' ? '新增权签人' : '编辑权签人'" width="680px">
      <el-form :model="form" label-position="top" class="form-grid">
        <el-form-item label="一级部门" required><el-input v-model="form.firstLevelDepartment" /></el-form-item>
        <el-form-item label="二级部门"><el-input v-model="form.secondLevelDepartment" /></el-form-item>
        <el-form-item label="三级部门"><el-input v-model="form.thirdLevelDepartment" /></el-form-item>
        <el-form-item label="四级部门"><el-input v-model="form.fourthLevelDepartment" /></el-form-item>
        <el-form-item label="部门权签人" required class="span-all">
          <el-select v-model="form.signatories" multiple filterable allow-create default-first-option clearable collapse-tags collapse-tags-tooltip placeholder="请选择或输入部门权签人">
            <el-option v-for="item in signatoryOptions" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveRow">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ElMessage, ElMessageBox } from 'element-plus'
import { computed, onMounted, reactive, ref } from 'vue'
import {
  createDepartmentSignatory,
  deleteDepartmentSignatory,
  fetchDepartmentSignatories,
  updateDepartmentSignatory,
  type DepartmentSignatoryCommand
} from '../../api/modules/departmentSignatory'
import type { DepartmentSignatory } from '../../types'

const CSV_HEADERS = ['一级部门', '二级部门', '三级部门', '四级部门', '部门权签人']

const rows = ref<DepartmentSignatory[]>([])
const allRows = ref<DepartmentSignatory[]>([])
const currentRow = ref<DepartmentSignatory>()
const dialogVisible = ref(false)
const dialogMode = ref<'create' | 'edit'>('create')
const editingId = ref<number>()
const fileInputRef = ref<HTMLInputElement>()

const query = reactive({ departmentName: '', signatories: [] as string[] })
const form = reactive<DepartmentSignatoryCommand>({
  firstLevelDepartment: '',
  secondLevelDepartment: '',
  thirdLevelDepartment: '',
  fourthLevelDepartment: '',
  signatories: []
})

const signatoryOptions = computed(() => Array.from(new Set([
  ...allRows.value.flatMap(item => item.signatories || []),
  ...rows.value.flatMap(item => item.signatories || []),
  ...form.signatories
].filter(Boolean))).sort())

async function loadRows() {
  rows.value = await fetchDepartmentSignatories({
    departmentName: query.departmentName || undefined,
    signatories: query.signatories
  })
  if (!query.departmentName && !query.signatories.length) {
    allRows.value = rows.value
  }
  currentRow.value = undefined
}

async function resetQuery() {
  query.departmentName = ''
  query.signatories = []
  await loadRows()
}

function resetForm() {
  form.firstLevelDepartment = ''
  form.secondLevelDepartment = ''
  form.thirdLevelDepartment = ''
  form.fourthLevelDepartment = ''
  form.signatories = []
  editingId.value = undefined
}

function openDialog(row?: DepartmentSignatory) {
  resetForm()
  dialogMode.value = row ? 'edit' : 'create'
  if (row) {
    editingId.value = row.departmentSignatoryId
    form.firstLevelDepartment = row.firstLevelDepartment
    form.secondLevelDepartment = row.secondLevelDepartment || ''
    form.thirdLevelDepartment = row.thirdLevelDepartment || ''
    form.fourthLevelDepartment = row.fourthLevelDepartment || ''
    form.signatories = [...(row.signatories || [])]
  }
  dialogVisible.value = true
}

async function saveRow() {
  const payload = normalizeForm()
  if (!payload) return
  if (dialogMode.value === 'create') {
    await createDepartmentSignatory(payload)
  } else if (editingId.value) {
    await updateDepartmentSignatory(editingId.value, payload)
  }
  dialogVisible.value = false
  await reloadAll()
  ElMessage.success('保存成功')
}

async function removeRow(row: DepartmentSignatory) {
  await ElMessageBox.confirm(`确认删除 ${formatDepartmentPath(row)} 的权签人配置吗？`, '提示', { type: 'warning' })
  await deleteDepartmentSignatory(row.departmentSignatoryId)
  await reloadAll()
  ElMessage.success('删除成功')
}

function onCurrentChange(row?: DepartmentSignatory) {
  currentRow.value = row
}

function normalizeForm(): DepartmentSignatoryCommand | undefined {
  const signatories = form.signatories.map(item => item.trim()).filter(Boolean)
  if (!form.firstLevelDepartment.trim()) {
    ElMessage.warning('请输入一级部门')
    return undefined
  }
  if (!signatories.length) {
    ElMessage.warning('请选择部门权签人')
    return undefined
  }
  return {
    firstLevelDepartment: form.firstLevelDepartment.trim(),
    secondLevelDepartment: form.secondLevelDepartment?.trim(),
    thirdLevelDepartment: form.thirdLevelDepartment?.trim(),
    fourthLevelDepartment: form.fourthLevelDepartment?.trim(),
    signatories: Array.from(new Set(signatories))
  }
}

async function reloadAll() {
  allRows.value = await fetchDepartmentSignatories()
  await loadRows()
}

function triggerImport() {
  fileInputRef.value?.click()
}

async function handleImportFile(event: Event) {
  const input = event.target as HTMLInputElement
  const file = input.files?.[0]
  input.value = ''
  if (!file) return
  const text = await file.text()
  const records = parseCsv(text)
  if (!records.length) {
    ElMessage.warning('导入文件没有可识别的数据')
    return
  }
  for (const record of records) {
    const payload = mapCsvRecordToCommand(record)
    if (!payload) continue
    const existing = allRows.value.find(item => isSameDepartment(item, payload))
    if (existing) {
      await updateDepartmentSignatory(existing.departmentSignatoryId, payload)
    } else {
      await createDepartmentSignatory(payload)
    }
  }
  await reloadAll()
  ElMessage.success(`导入完成，共处理 ${records.length} 条数据`)
}

function exportRows() {
  const source = rows.value.length ? rows.value : allRows.value
  const lines = [CSV_HEADERS, ...source.map(item => [
    item.firstLevelDepartment,
    item.secondLevelDepartment || '',
    item.thirdLevelDepartment || '',
    item.fourthLevelDepartment || '',
    (item.signatories || []).join(';')
  ])]
  const blob = new Blob(['\uFEFF' + lines.map(line => line.map(escapeCsvCell).join(',')).join('\n')], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = `权签人维护_${new Date().toISOString().slice(0, 10)}.csv`
  link.click()
  URL.revokeObjectURL(url)
}

function parseCsv(text: string) {
  const lines = text.replace(/^\uFEFF/, '').split(/\r?\n/).filter(line => line.trim())
  if (lines.length <= 1) return []
  return lines.slice(1).map(line => splitCsvLine(line))
}

function splitCsvLine(line: string) {
  const cells: string[] = []
  let current = ''
  let quoted = false
  for (let index = 0; index < line.length; index += 1) {
    const char = line[index]
    const next = line[index + 1]
    if (char === '"' && quoted && next === '"') {
      current += '"'
      index += 1
    } else if (char === '"') {
      quoted = !quoted
    } else if (char === ',' && !quoted) {
      cells.push(current.trim())
      current = ''
    } else {
      current += char
    }
  }
  cells.push(current.trim())
  return cells
}

function mapCsvRecordToCommand(cells: string[]): DepartmentSignatoryCommand | undefined {
  const signatories = (cells[4] || '').split(/[;；、]/).map(item => item.trim()).filter(Boolean)
  if (!cells[0]?.trim() || !signatories.length) return undefined
  return {
    firstLevelDepartment: cells[0].trim(),
    secondLevelDepartment: cells[1]?.trim(),
    thirdLevelDepartment: cells[2]?.trim(),
    fourthLevelDepartment: cells[3]?.trim(),
    signatories: Array.from(new Set(signatories))
  }
}

function escapeCsvCell(value?: string) {
  const text = value || ''
  return /[",\n]/.test(text) ? `"${text.replace(/"/g, '""')}"` : text
}

function isSameDepartment(row: DepartmentSignatory, payload: DepartmentSignatoryCommand) {
  return row.firstLevelDepartment === payload.firstLevelDepartment &&
    (row.secondLevelDepartment || '') === (payload.secondLevelDepartment || '') &&
    (row.thirdLevelDepartment || '') === (payload.thirdLevelDepartment || '') &&
    (row.fourthLevelDepartment || '') === (payload.fourthLevelDepartment || '')
}

function formatDepartmentPath(row: DepartmentSignatory) {
  return [row.firstLevelDepartment, row.secondLevelDepartment, row.thirdLevelDepartment, row.fourthLevelDepartment].filter(Boolean).join('/')
}

onMounted(reloadAll)
</script>

<style scoped>
.signatory-page { display: grid; gap: 18px; }
.page-banner { padding: 24px; border-radius: 8px; background: linear-gradient(135deg, #14395b 0%, #1f6f8b 56%, #f4f8fb 56%, #ffffff 100%); box-shadow: 0 12px 28px rgba(31, 111, 139, 0.14); }
.page-banner span { color: rgba(255,255,255,.78); font-size: 12px; text-transform: uppercase; }
.page-banner h2 { margin: 8px 0; color: #fff; font-size: 28px; }
.page-banner p { margin: 0; color: rgba(255,255,255,.88); max-width: 680px; line-height: 1.7; }
.query-grid { display: grid; grid-template-columns: repeat(3, minmax(0, 1fr)); gap: 14px; align-items: end; }
.query-actions { display: flex; gap: 10px; align-items: center; padding-bottom: 18px; }
.table-head { display: flex; align-items: center; justify-content: space-between; gap: 12px; }
.toolbar { display: flex; flex-wrap: wrap; gap: 8px; }
.hidden-input { display: none; }
.tag-chip { margin: 2px 6px 2px 0; }
.form-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 14px; }
.span-all { grid-column: 1 / -1; }
@media (max-width: 768px) {
  .query-grid, .form-grid { grid-template-columns: 1fr; }
  .table-head { align-items: flex-start; flex-direction: column; }
}
</style>
