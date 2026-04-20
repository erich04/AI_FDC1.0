<template>
  <div class="transfer-field-config-page">
    <el-card shadow="never">
      <el-form :inline="true" class="toolbar">
        <el-form-item label="文档类型">
          <CommonTreeSelect
            v-model="documentTypeCode"
            :data="documentTypeLevel1Tree"
            :props="{ label: 'typeName', value: 'typeCode' }"
            class="input-w280"
            placeholder="请选择一级文档类型"
            @change="loadConfig"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="success" :loading="saving" @click="addDefaultConfig">新增</el-button>
          <el-button :disabled="!documentTypeCode" :loading="loading" @click="loadConfig">查询</el-button>
          <el-button type="primary" :disabled="!documentTypeCode" :loading="saving" @click="saveConfig">保存</el-button>
        </el-form-item>
      </el-form>

      <el-alert type="info" :closable="false" show-icon>
        未配置文档类型默认全部显示。以下 13 个通用字段支持按文档类型配置显隐。
      </el-alert>

      <el-table :data="fieldRows" border class="field-table" v-loading="loading">
        <el-table-column type="index" label="序号" width="70" />
        <el-table-column label="文档类型" min-width="200" show-overflow-tooltip>
          <template #default>
            {{ selectedDocumentTypeName || documentTypeCode || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="fieldName" label="字段名称" min-width="240" />
        <el-table-column prop="fieldCode" label="字段编码" min-width="200" />
        <el-table-column label="是否显示" width="140">
          <template #default="{ row }">
            <el-switch
              v-model="row.visible"
              active-value="Y"
              inactive-value="N"
              active-text="显示"
              inactive-text="隐藏"
            />
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import CommonTreeSelect from '../../components/CommonTreeSelect.vue'
import { fetchDocumentTypeTree } from '../../api/modules/documentType'
import {
  getTransferApplyFieldConfig,
  saveTransferApplyFieldConfig,
  type TransferApplyFieldConfigItem
} from '../../api/modules/transferApplyFieldConfig'
import type { DocumentTypeTreeNode } from '../../types'

interface EditableFieldRow extends TransferApplyFieldConfigItem {
  visible: 'Y' | 'N'
}

const DEFAULT_TENANT_ID = 1
const loading = ref(false)
const saving = ref(false)
const documentTypeTree = ref<DocumentTypeTreeNode[]>([])
const documentTypeCode = ref('')
const fieldRows = ref<EditableFieldRow[]>([])
const DEFAULT_VISIBLE_FIELD_CODES = [
  'companyProjectCode',
  'docBusiNo',
  'docName',
  'busiModuleCode',
  'archPlaceAlpha2Code',
  'documentOrganizationCode',
  'startArchPeriod',
  'endArchPeriod',
  'docGenerationDate',
  'carrierType',
  'archCopies',
  'remark',
  'description'
]

const documentTypeLevel1Tree = computed(() =>
  (Array.isArray(documentTypeTree.value) ? documentTypeTree.value : []).map((node) => ({ ...node, children: [] }))
)
const selectedDocumentTypeName = computed(
  () => documentTypeLevel1Tree.value.find((node) => node.typeCode === documentTypeCode.value)?.typeName || ''
)

async function loadConfig() {
  if (!documentTypeCode.value) {
    fieldRows.value = []
    return
  }
  loading.value = true
  try {
    const response = await getTransferApplyFieldConfig(documentTypeCode.value, DEFAULT_TENANT_ID)
    fieldRows.value = (response.fields || []).map((item) => ({
      ...item,
      visible: item.visibleFlag === 'N' ? 'N' : 'Y'
    }))
  } catch (error) {
    console.error(error)
    ElMessage.error('加载字段配置失败')
  } finally {
    loading.value = false
  }
}

async function saveConfig() {
  if (!documentTypeCode.value) {
    ElMessage.warning('请先选择一级文档类型')
    return
  }
  saving.value = true
  try {
    const response = await saveTransferApplyFieldConfig(documentTypeCode.value, {
      tenantid: DEFAULT_TENANT_ID,
      fields: fieldRows.value.map((row) => ({
        fieldCode: row.fieldCode,
        visibleFlag: row.visible
      }))
    })
    fieldRows.value = (response.fields || []).map((item) => ({
      ...item,
      visible: item.visibleFlag === 'N' ? 'N' : 'Y'
    }))
    ElMessage.success('配置保存成功')
  } catch (error) {
    console.error(error)
    ElMessage.error('配置保存失败')
  } finally {
    saving.value = false
  }
}

async function addDefaultConfig() {
  if (!documentTypeCode.value) {
    ElMessage.warning('请先选择一级文档类型')
    return
  }
  saving.value = true
  try {
    await saveTransferApplyFieldConfig(documentTypeCode.value, {
      tenantid: DEFAULT_TENANT_ID,
      fields: DEFAULT_VISIBLE_FIELD_CODES.map((fieldCode) => ({
        fieldCode,
        visibleFlag: 'Y' as const
      }))
    })
    await loadConfig()
    ElMessage.success('已新增当前文档类型默认配置（13个字段均显示）')
  } catch (error) {
    console.error(error)
    ElMessage.error('新增默认配置失败')
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  try {
    documentTypeTree.value = await fetchDocumentTypeTree()
  } catch (error) {
    console.error(error)
    ElMessage.error('加载文档类型失败')
  }
})
</script>

<style scoped>
.transfer-field-config-page {
  display: grid;
  gap: 16px;
}

.toolbar {
  margin-bottom: 8px;
}

.input-w280 {
  width: 280px;
}

.field-table {
  margin-top: 12px;
}
</style>
