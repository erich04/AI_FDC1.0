<template>
  <div class="page">
    <el-card shadow="never">
      <template #header><strong>文档类型配置</strong></template>
      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="文档类型编码">
          <el-input v-model="queryForm.docTypeCode" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="文档类型名称">
          <el-input v-model="queryForm.docTypeDescription" clearable style="width: 220px" />
        </el-form-item>
        <el-form-item label="是否启用">
          <el-select v-model="queryForm.enableFlag" style="width: 180px">
            <el-option label="是" value="Y" />
            <el-option label="否" value="N" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadList">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <template #header>
        <div class="toolbar">
          <el-button type="primary" @click="openCreate">新增</el-button>
          <el-button type="primary" :disabled="!hasEditingRows" @click="saveEditingRows">保存</el-button>
          <el-dropdown split-button type="default" @click="triggerImportCsv" @command="handleImportCommand">
            导入CSV
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="csv">导入CSV</el-dropdown-item>
                <el-dropdown-item command="excel">导入Excel</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <el-dropdown split-button type="default" @click="onExportCsv" @command="handleExportCommand">
            导出CSV
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="csv">导出CSV</el-dropdown-item>
                <el-dropdown-item command="excel">导出Excel</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </template>
      <input ref="importCsvRef" type="file" accept=".csv,text/csv" style="display: none" @change="onImportCsvChange" />
      <input ref="importExcelRef" type="file" accept=".xlsx,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" style="display: none" @change="onImportExcelChange" />
      <el-table :data="listData" border>
        <el-table-column type="index" label="序号" width="70" />
        <el-table-column label="操作" width="130">
          <template #default="{ row }">
            <el-button v-if="row.__editing && row.__isNew" link type="danger" @click="cancelNewRow(row)">删除</el-button>
            <el-button v-if="!row.__editing" link type="primary" @click="startEditRow(row)">编辑</el-button>
          </template>
        </el-table-column>
        <el-table-column label="文档类型编码" min-width="180">
          <template #default="{ row }">
            <el-input v-if="row.__editing" v-model="row.docTypeCode" />
            <span v-else>{{ row.docTypeCode }}</span>
          </template>
        </el-table-column>
        <el-table-column label="文档类型名称" min-width="240">
          <template #default="{ row }">
            <el-input v-if="row.__editing" v-model="row.docTypeDescription" />
            <span v-else>{{ row.docTypeDescription }}</span>
          </template>
        </el-table-column>
        <el-table-column label="是否启用" width="100">
          <template #default="{ row }">
            <el-switch v-if="row.__editing" v-model="row.enableFlag" active-value="Y" inactive-value="N" />
            <span v-else>{{ row.enableFlag === 'Y' ? '是' : '否' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createdBy" label="创建人" width="100" />
        <el-table-column label="创建时间" min-width="120">
          <template #default="{ row }">{{ formatDate(row.creationDate) }}</template>
        </el-table-column>
        <el-table-column prop="lastUpdatedBy" label="最后更新人" width="110" />
        <el-table-column label="最后更新时间" min-width="120">
          <template #default="{ row }">{{ formatDate(row.lastUpdateDate) }}</template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus'
import { computed, onMounted, reactive, ref } from 'vue'
import {
  createDocumentTypeConfig,
  exportDocumentTypeConfigsCsv,
  exportDocumentTypeConfigsExcel,
  importDocumentTypeConfigsCsv,
  importDocumentTypeConfigsExcel,
  queryDocumentTypeConfigs,
  updateDocumentTypeConfig,
  type DocumentTypeConfigSaveCommand
} from '../../api/modules/documentTypeConfig'
import type { DocumentTypeConfig } from '../../types'

type DocumentTypeConfigRow = DocumentTypeConfig & {
  __isNew?: boolean
  __editing?: boolean
}

const queryForm = reactive({ docTypeCode: '', docTypeDescription: '', enableFlag: 'Y' as 'Y' | 'N' })
const listData = ref<DocumentTypeConfigRow[]>([])
const importCsvRef = ref<HTMLInputElement>()
const importExcelRef = ref<HTMLInputElement>()
const hasEditingRows = computed(() => listData.value.some(row => row.__editing))

const loadList = async () => {
  try {
    const rows = await queryDocumentTypeConfigs({ ...queryForm })
    listData.value = rows.map(row => ({ ...row, __isNew: false, __editing: false }))
  } catch (e: any) {
    ElMessage.error(e?.message || '查询失败')
  }
}

const resetQuery = async () => {
  queryForm.docTypeCode = ''
  queryForm.docTypeDescription = ''
  queryForm.enableFlag = 'Y'
  await loadList()
}

const openCreate = () => {
  const emptyRow: DocumentTypeConfigRow = {
    documentTypeId: 0,
    docTypeCode: '',
    docTypeDescription: '',
    enableFlag: 'Y',
    __isNew: true,
    __editing: true
  }
  listData.value = [emptyRow, ...listData.value]
}

const startEditRow = (row: DocumentTypeConfigRow) => {
  row.__editing = true
}

const cancelNewRow = (row: DocumentTypeConfigRow) => {
  listData.value = listData.value.filter(item => item !== row)
}

const saveRow = async (row: DocumentTypeConfigRow) => {
  const payload: DocumentTypeConfigSaveCommand = {
    docTypeCode: row.docTypeCode?.trim(),
    docTypeDescription: row.docTypeDescription?.trim(),
    enableFlag: row.enableFlag
  }
  if (!payload.docTypeCode || !payload.docTypeDescription) {
    return ElMessage.warning('请完整填写文档类型编码和名称')
  }
  try {
    if (row.__isNew) await createDocumentTypeConfig(payload)
    else await updateDocumentTypeConfig(row.documentTypeId, payload)
    await loadList()
    ElMessage.success('保存成功')
  } catch (e: any) {
    ElMessage.error(e?.message || '保存失败')
  }
}

const saveEditingRows = async () => {
  const targets = listData.value.filter(row => row.__editing)
  if (!targets.length) return
  for (const row of targets) {
    await saveRow(row)
  }
}

const triggerImportCsv = () => {
  if (!importCsvRef.value) return
  importCsvRef.value.value = ''
  importCsvRef.value.click()
}

const triggerImportExcel = () => {
  if (!importExcelRef.value) return
  importExcelRef.value.value = ''
  importExcelRef.value.click()
}

const handleImportCommand = (command: string) => {
  if (command === 'excel') {
    triggerImportExcel()
    return
  }
  triggerImportCsv()
}

const onImportCsvChange = async (event: Event) => {
  const input = event.target as HTMLInputElement
  const file = input.files?.[0]
  if (!file) return
  try {
    const imported = await importDocumentTypeConfigsCsv(file)
    ElMessage.success(`CSV 导入完成，共处理 ${imported} 行`)
    await loadList()
  } catch (e: any) {
    ElMessage.error(e?.message || 'CSV 导入失败')
  }
}

const onImportExcelChange = async (event: Event) => {
  const input = event.target as HTMLInputElement
  const file = input.files?.[0]
  if (!file) return
  try {
    const imported = await importDocumentTypeConfigsExcel(file)
    ElMessage.success(`Excel 导入完成，共处理 ${imported} 行`)
    await loadList()
  } catch (e: any) {
    ElMessage.error(e?.message || 'Excel 导入失败')
  }
}

const downloadBlob = (blob: Blob, filename: string) => {
  const url = window.URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = filename
  a.click()
  window.URL.revokeObjectURL(url)
}

const onExportCsv = async () => {
  try {
    const blob = await exportDocumentTypeConfigsCsv({ ...queryForm })
    downloadBlob(blob, 'document-type-configs.csv')
    ElMessage.success('CSV 导出成功')
  } catch (e: any) {
    ElMessage.error(e?.message || 'CSV 导出失败')
  }
}

const onExportExcel = async () => {
  try {
    const blob = await exportDocumentTypeConfigsExcel({ ...queryForm })
    downloadBlob(blob, 'document-type-configs.xlsx')
    ElMessage.success('Excel 导出成功')
  } catch (e: any) {
    ElMessage.error(e?.message || 'Excel 导出失败')
  }
}

const handleExportCommand = (command: string) => {
  if (command === 'excel') {
    void onExportExcel()
    return
  }
  void onExportCsv()
}

const formatDate = (value?: string) => {
  if (!value) return ''
  return value.replace('T', ' ').slice(0, 19)
}

onMounted(async () => {
  await loadList()
})
</script>

<style scoped>
.page {
  display: grid;
  gap: 16px;
}
.query-form {
  display: flex;
  flex-wrap: wrap;
}
.toolbar {
  display: flex;
  gap: 10px;
}
</style>
