<template>
  <div class="document-type-config-page">
    <section class="doc-hero">
      <div>
        <span>Document Type Config</span>
        <h2>文档类型配置</h2>
        <p>维护文档类型编码、名称、描述和启用状态，为借阅、归档和检索提供统一类型基础数据。</p>
      </div>
    </section>

    <el-card shadow="never" class="query-card">
      <el-form class="doc-query" :model="query" label-position="top">
        <el-form-item label="文档类型">
          <el-select v-model="query.typeCodes" multiple filterable collapse-tags collapse-tags-tooltip clearable placeholder="请选择文档类型">
            <el-option v-for="item in typeOptions" :key="item.docTypeCode" :label="`${item.docTypeCode} ｜ ${item.docTypeDescription}`" :value="item.docTypeCode" />
          </el-select>
        </el-form-item>
        <el-form-item label="启用标志">
          <el-select v-model="query.enabledFlag" clearable placeholder="请选择启用标志">
            <el-option label="启用" value="Y" />
            <el-option label="停用" value="N" />
          </el-select>
        </el-form-item>
        <div class="query-actions">
          <el-button type="primary" @click="applyQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </div>
      </el-form>
    </el-card>

    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="table-head">
          <strong>文档类型列表</strong>
          <el-button type="primary" @click="openDialog()">新增</el-button>
        </div>
      </template>
      <el-table :data="filteredTypes" border empty-text="暂无文档类型">
        <el-table-column prop="docTypeCode" label="文档类型编码" min-width="180" />
        <el-table-column prop="docTypeDescription" label="文档类型名称" min-width="180" />
        <el-table-column prop="enableFlag" label="启用标志" width="110">
          <template #default="{ row }">
            <el-tag :type="row.enableFlag === 'Y' ? 'success' : 'info'">{{ row.enableFlag === 'Y' ? '启用' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogMode === 'create' ? '新增文档类型' : '编辑文档类型'" width="560px">
      <el-form :model="form" label-position="top">
        <el-form-item label="文档类型编码" required>
          <el-input v-model="form.docTypeCode" :disabled="dialogMode === 'edit'" />
        </el-form-item>
        <el-form-item label="文档类型名称" required>
          <el-input v-model="form.docTypeDescription" />
        </el-form-item>
        <el-form-item label="启用标志">
          <el-radio-group v-model="form.enableFlag">
            <el-radio value="Y">启用</el-radio>
            <el-radio value="N">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saveLoading" @click="saveType">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus'
import { computed, onMounted, reactive, ref } from 'vue'
import {
  createDocumentTypeConfig,
  queryDocumentTypeConfigs,
  updateDocumentTypeConfig,
  type DocumentTypeConfigSaveCommand
} from '../../api/modules/documentTypeConfig'
import type { DocumentTypeConfig } from '../../types'

const rawTypes = ref<DocumentTypeConfig[]>([])
const dialogVisible = ref(false)
const dialogMode = ref<'create' | 'edit'>('create')
const editingNode = ref<DocumentTypeConfig>()
const saveLoading = ref(false)

const query = reactive({ typeCodes: [] as string[], enabledFlag: '' as '' | 'Y' | 'N' })
const form = reactive<DocumentTypeConfigSaveCommand>({ docTypeCode: '', docTypeDescription: '', enableFlag: 'Y' })

const typeOptions = computed(() => rawTypes.value)
const filteredTypes = computed(() => rawTypes.value.filter(item => {
  const typeMatched = !query.typeCodes.length || query.typeCodes.includes(item.docTypeCode)
  const enabledMatched = !query.enabledFlag || item.enableFlag === query.enabledFlag
  return typeMatched && enabledMatched
}))

async function loadTypes() {
  rawTypes.value = await queryDocumentTypeConfigs({
    enableFlag: query.enabledFlag || undefined
  })
}

async function applyQuery() {
  await loadTypes()
}

async function resetQuery() {
  query.typeCodes = []
  query.enabledFlag = ''
  await loadTypes()
}

function resetForm() {
  form.docTypeCode = ''
  form.docTypeDescription = ''
  form.enableFlag = 'Y'
  editingNode.value = undefined
}

function openDialog(row?: DocumentTypeConfig) {
  resetForm()
  dialogMode.value = row ? 'edit' : 'create'
  if (row) {
    editingNode.value = row
    form.docTypeCode = row.docTypeCode
    form.docTypeDescription = row.docTypeDescription
    form.enableFlag = row.enableFlag
  }
  dialogVisible.value = true
}

async function saveType() {
  if (!form.docTypeCode.trim()) return ElMessage.warning('请输入文档类型编码')
  if (!form.docTypeDescription.trim()) return ElMessage.warning('请输入文档类型名称')
  saveLoading.value = true
  try {
    if (dialogMode.value === 'create') {
      await createDocumentTypeConfig({
        docTypeCode: form.docTypeCode.trim(),
        docTypeDescription: form.docTypeDescription.trim(),
        enableFlag: form.enableFlag
      })
    } else if (editingNode.value) {
      await updateDocumentTypeConfig(editingNode.value.documentTypeId, {
        docTypeCode: form.docTypeCode.trim(),
        docTypeDescription: form.docTypeDescription.trim(),
        enableFlag: form.enableFlag
      })
    }
    dialogVisible.value = false
    await loadTypes()
    ElMessage.success('保存成功')
  } catch (error) {
    ElMessage.error(resolveErrorMessage(error, '文档类型保存失败，请检查编码是否重复或后端服务是否正常'))
  } finally {
    saveLoading.value = false
  }
}

function resolveErrorMessage(error: unknown, fallback: string) {
  if (error instanceof Error && error.message) return error.message
  const responseMessage = (error as { response?: { data?: { message?: string } } })?.response?.data?.message
  return responseMessage || fallback
}

onMounted(loadTypes)
</script>

<style scoped>
.document-type-config-page { display: grid; gap: 20px; }
.doc-hero { padding: 24px; border-radius: 24px; color: #17324a; background: linear-gradient(135deg, #eaf4ff 0%, #c8e1f7 48%, #fff5df 48%, #fffaf0 100%); box-shadow: 0 18px 40px rgba(23, 50, 74, .12); }
.doc-hero span { font-size: 12px; letter-spacing: .08em; text-transform: uppercase; opacity: .72; }
.doc-hero h2 { margin: 8px 0; font-size: 30px; }
.doc-hero p { margin: 0; max-width: 680px; color: #526678; }
.query-card, .table-card { border-radius: 20px; border: 1px solid #dce8ef; }
.doc-query { display: grid; grid-template-columns: minmax(260px, 1fr) minmax(160px, 220px) auto; gap: 12px; align-items: end; }
.query-actions { display: flex; gap: 8px; padding-bottom: 18px; }
.table-head { display: flex; justify-content: space-between; align-items: center; gap: 12px; }
@media (max-width: 900px) { .doc-query { grid-template-columns: 1fr; } .query-actions { padding-bottom: 0; } }
</style>
