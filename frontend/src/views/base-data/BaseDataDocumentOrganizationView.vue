<template>
  <div class="document-organization-page">
    <h1 class="page-title">文档组织配置</h1>
    <el-card shadow="never">
      <div class="toolbar">
        <el-form :inline="true" :model="query" class="query-form" label-position="top">
          <el-row :gutter="20">
            <el-col :span="4">
              <el-form-item label="文档组织编码">
                <el-select v-model="query.keyword" placeholder="请选择编码" clearable filterable allow-create style="width: 100%">
                  <el-option v-for="item in documentOrganizationOptions" :key="item.code" :label="item.code" :value="item.code" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="4">
              <el-form-item label="文档组织名称">
                <el-select v-model="query.documentOrganizationName" placeholder="请选择名称" clearable filterable allow-create style="width: 100%">
                  <el-option v-for="item in documentOrganizationOptions" :key="item.name" :label="item.name" :value="item.name" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="4">
              <el-form-item label="国家">
                <el-select v-model="query.countryCode" placeholder="全部" clearable style="width: 100%" @change="handleQueryCountryChange">
                  <el-option v-for="item in countries" :key="item.countryCode" :label="item.countryName" :value="item.countryCode" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="4">
              <el-form-item label="城市">
                <el-select v-model="query.cityCode" placeholder="全部" clearable style="width: 100%">
                  <el-option v-for="item in queryCities" :key="item.cityCode" :label="item.cityName" :value="item.cityCode" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="4">
              <el-form-item label="启用标识">
                <el-select v-model="query.enabledFlag" placeholder="全部" clearable style="width: 100%">
                  <el-option label="启用" value="Y" />
                  <el-option label="停用" value="N" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <div class="toolbar-actions">
          <el-button @click="resetQuery">
            <span class="btn-icon">restart_alt</span>重置
          </el-button>
          <el-button type="primary" @click="loadList">
            <span class="btn-icon">search</span>查询
          </el-button>
          <el-button type="success" @click="startCreate">
            <span class="btn-icon">add</span>新建文档组织
          </el-button>
        </div>
      </div>

      <div v-if="editor.visible" class="editor-panel">
        <div class="editor-title">
          <span class="btn-icon">{{ editor.mode === 'create' ? 'add_box' : editor.mode === 'edit' ? 'edit_square' : 'visibility' }}</span>
          {{ editor.mode === 'create' ? '新建文档组织' : editor.mode === 'edit' ? `编辑文档组织：${form.documentOrganizationCode}` : `查看文档组织：${form.documentOrganizationCode}` }}
        </div>
        <el-form ref="formRef" :model="form" :rules="rules" label-width="120px" class="editor-form">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="文档组织编码" prop="documentOrganizationCode">
                <el-input v-model.trim="form.documentOrganizationCode" :disabled="editor.mode !== 'create' || isReadonly" maxlength="64" placeholder="请输入编码" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="文档组织名称" prop="documentOrganizationName">
                <el-input v-model.trim="form.documentOrganizationName" :disabled="isReadonly" maxlength="128" placeholder="请输入名称" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="描述信息" prop="description">
            <el-input v-model.trim="form.description" type="textarea" :rows="3" maxlength="500" show-word-limit :disabled="isReadonly" placeholder="请输入详细描述" />
          </el-form-item>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="国家" prop="countryCode">
                <el-select v-model="form.countryCode" placeholder="请选择" :disabled="isReadonly" style="width: 100%" @change="handleFormCountryChange">
                  <el-option v-for="item in countries" :key="item.countryCode" :label="item.countryName" :value="item.countryCode" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="城市" prop="cityCode">
                <el-select v-model="form.cityCode" placeholder="请选择" :disabled="isReadonly" style="width: 100%">
                  <el-option v-for="item in formCities" :key="item.cityCode" :label="item.cityName" :value="item.cityCode" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="启用标识" prop="enabledFlag">
                <el-segmented v-model="form.enabledFlag" :options="flagOptions" :disabled="isReadonly" />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <div class="editor-actions">
          <el-button @click="cancelEditor">
            <span class="btn-icon">cancel</span>取消
          </el-button>
          <el-button v-if="editor.mode === 'view'" type="primary" plain @click="startEdit(form.documentOrganizationCode)">
            <span class="btn-icon">edit</span>进入编辑
          </el-button>
          <el-button v-else type="primary" @click="submit">
            <span class="btn-icon">save</span>保存
          </el-button>
        </div>
      </div>

      <el-table :data="displayedItems" border empty-text="暂无文档组织数据，请调整筛选条件或新建数据">
        <el-table-column prop="documentOrganizationCode" label="文档组织编码" min-width="160" />
        <el-table-column prop="documentOrganizationName" label="文档组织名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="description" label="描述信息" min-width="220" show-overflow-tooltip />
        <el-table-column label="国家" min-width="140">
          <template #default="{ row }">
            <div class="flex items-center gap-1">
              <span class="btn-icon text-gray-400">public</span>
              {{ formatCountry(row.countryCode) }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="城市" min-width="160">
          <template #default="{ row }">
            <div class="flex items-center gap-1">
              <span class="btn-icon text-gray-400">location_city</span>
              {{ formatCity(row.cityCode) }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="启用标识" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="row.enabledFlag === 'Y' ? 'success' : 'info'" effect="light" round>
              {{ row.enabledFlag === 'Y' ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastUpdateDate" label="最后更新时间" min-width="180" />
        <el-table-column label="操作" width="220" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="startView(row.documentOrganizationCode)">
              <span class="btn-icon">visibility</span>查看
            </el-button>
            <el-button link type="primary" @click="startEdit(row.documentOrganizationCode)">
              <span class="btn-icon">edit</span>编辑
            </el-button>
            <el-popconfirm title="确认软删除该文档组织吗？" @confirm="removeItem(row.documentOrganizationCode)">
              <template #reference>
                <el-button link type="danger">
                  <span class="btn-icon">delete</span>删除
                </el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { computed, onMounted, reactive, ref } from 'vue'
import {
  createDocumentOrganization,
  deleteDocumentOrganization,
  fetchDocumentOrganizationCities,
  fetchDocumentOrganizationCountries,
  fetchDocumentOrganizationDetail,
  fetchDocumentOrganizations,
  updateDocumentOrganization,
  type DocumentOrganizationCreateCommand
} from '../../api/modules/documentOrganization'
import type { CountryOption, DocumentOrganizationCityOption, DocumentOrganizationDetail, DocumentOrganizationSummary } from '../../types'

const formRef = ref<FormInstance>()
const items = ref<DocumentOrganizationSummary[]>([])
const countries = ref<CountryOption[]>([])
const allCities = ref<DocumentOrganizationCityOption[]>([])
const queryCities = ref<DocumentOrganizationCityOption[]>([])
const formCities = ref<DocumentOrganizationCityOption[]>([])
const documentOrganizationOptions = ref<Array<{code: string, name: string}>>([])

const flagOptions = [
  { label: '启用', value: 'Y' },
  { label: '停用', value: 'N' }
]

const query = reactive({
  keyword: '',
  documentOrganizationName: '',
  countryCode: '',
  cityCode: '',
  enabledFlag: ''
})

const editor = reactive({
  visible: false,
  mode: 'create' as 'create' | 'edit' | 'view',
  lastUpdateDate: ''
})

const form = reactive<DocumentOrganizationCreateCommand>({
  documentOrganizationCode: '',
  documentOrganizationName: '',
  description: '',
  countryCode: '',
  cityCode: '',
  enabledFlag: 'Y'
})

const rules: FormRules<DocumentOrganizationCreateCommand> = {
  documentOrganizationCode: [
    { required: true, message: '请输入文档组织编码', trigger: 'blur' },
    { max: 64, message: '长度不能超过64位', trigger: 'blur' }
  ],
  documentOrganizationName: [
    { required: true, message: '请输入文档组织名称', trigger: 'blur' },
    { max: 128, message: '长度不能超过128位', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入描述信息', trigger: 'blur' },
    { max: 500, message: '长度不能超过500位', trigger: 'blur' }
  ],
  countryCode: [{ required: true, message: '请选择国家', trigger: 'change' }],
  cityCode: [{ max: 64, message: '长度不能超过64位', trigger: 'change' }],
  enabledFlag: [{ required: true, message: '请选择启用标识', trigger: 'change' }]
}

const isReadonly = computed(() => editor.mode === 'view')

const displayedItems = computed(() => {
  if (editor.visible && editor.mode === 'edit' && form.documentOrganizationCode) {
    return items.value.filter(item => item.documentOrganizationCode !== form.documentOrganizationCode)
  }
  return items.value
})

const resetFormState = () => {
  form.documentOrganizationCode = ''
  form.documentOrganizationName = ''
  form.description = ''
  form.countryCode = countries.value[0]?.countryCode ?? ''
  form.cityCode = ''
  form.enabledFlag = 'Y'
  editor.lastUpdateDate = ''
  formCities.value = form.countryCode ? allCities.value.filter((item) => item.countryCode === form.countryCode) : allCities.value
}

const fillForm = (detail: DocumentOrganizationDetail) => {
  form.documentOrganizationCode = detail.documentOrganizationCode
  form.documentOrganizationName = detail.documentOrganizationName
  form.description = detail.description
  form.countryCode = detail.countryCode
  form.cityCode = detail.cityCode ?? ''
  form.enabledFlag = detail.enabledFlag
  editor.lastUpdateDate = detail.lastUpdateDate
  formCities.value = form.countryCode ? allCities.value.filter((item) => item.countryCode === form.countryCode) : allCities.value
}

const loadList = async () => {
  items.value = await fetchDocumentOrganizations({
    keyword: query.keyword?.trim() || undefined,
    documentOrganizationName: query.documentOrganizationName?.trim() || undefined,
    countryCode: query.countryCode || undefined,
    cityCode: query.cityCode || undefined,
    enabledFlag: query.enabledFlag || undefined
  })
  // 更新文档组织选项
  documentOrganizationOptions.value = items.value.map(item => ({
    code: item.documentOrganizationCode,
    name: item.documentOrganizationName
  }))
}

const loadMeta = async () => {
  countries.value = await fetchDocumentOrganizationCountries()
  allCities.value = await fetchDocumentOrganizationCities()
  queryCities.value = query.countryCode ? allCities.value.filter((item) => item.countryCode === query.countryCode) : allCities.value
  resetFormState()
}

const handleQueryCountryChange = () => {
  query.cityCode = ''
  queryCities.value = query.countryCode ? allCities.value.filter((item) => item.countryCode === query.countryCode) : allCities.value
}

const handleFormCountryChange = () => {
  form.cityCode = ''
  formCities.value = form.countryCode ? allCities.value.filter((item) => item.countryCode === form.countryCode) : allCities.value
}

const resetQuery = async () => {
  query.keyword = ''
  query.documentOrganizationName = ''
  query.countryCode = ''
  query.cityCode = ''
  query.enabledFlag = ''
  queryCities.value = allCities.value
  await loadList()
}

const startCreate = () => {
  editor.visible = true
  editor.mode = 'create'
  resetFormState()
}

const openDetailInEditor = async (documentOrganizationCode: string, mode: 'edit' | 'view') => {
  const detail = await fetchDocumentOrganizationDetail(documentOrganizationCode)
  editor.visible = true
  editor.mode = mode
  fillForm(detail)
}

const startEdit = async (documentOrganizationCode: string) => {
  await openDetailInEditor(documentOrganizationCode, 'edit')
}

const startView = async (documentOrganizationCode: string) => {
  await openDetailInEditor(documentOrganizationCode, 'view')
}

const cancelEditor = () => {
  editor.visible = false
  resetFormState()
  formRef.value?.clearValidate()
}

const submit = async () => {
  try {
    const valid = await formRef.value?.validate().catch(() => false)
    if (valid === false) return
    if (editor.mode === 'create') {
      await createDocumentOrganization({
        documentOrganizationCode: form.documentOrganizationCode,
        documentOrganizationName: form.documentOrganizationName,
        description: form.description,
        countryCode: form.countryCode,
        cityCode: form.cityCode || undefined,
        enabledFlag: form.enabledFlag
      })
      ElMessage.success('文档组织创建成功')
    } else {
      await updateDocumentOrganization(form.documentOrganizationCode, {
        documentOrganizationName: form.documentOrganizationName,
        description: form.description,
        countryCode: form.countryCode,
        cityCode: form.cityCode || undefined,
        enabledFlag: form.enabledFlag
      })
      ElMessage.success('文档组织更新成功')
    }
    cancelEditor()
    await loadList()
  } catch (error: any) {
    ElMessage.error(error?.message || '保存失败')
  }
}

const removeItem = async (documentOrganizationCode: string) => {
  try {
    await deleteDocumentOrganization(documentOrganizationCode)
    ElMessage.success('文档组织已软删除')
    if (form.documentOrganizationCode === documentOrganizationCode) {
      cancelEditor()
    }
    await loadList()
  } catch (error: any) {
    ElMessage.error(error?.message || '删除失败')
  }
}

const formatCountry = (countryCode?: string) => countries.value.find((item) => item.countryCode === countryCode)?.countryName ?? countryCode ?? '-'
const formatCity = (cityCode?: string) => allCities.value.find((item) => item.cityCode === cityCode)?.cityName ?? cityCode ?? '-'

onMounted(async () => {
  await loadMeta()
  await loadList()
})
</script>

<style scoped>
/* FDC-DS Core Styles */
:deep(.el-card) {
  border-radius: 12px;
  border-color: #dbe0e6;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

:deep(.el-card:hover) {
  transform: translateY(-4px);
  box-shadow: 0 10px 20px rgba(17, 115, 212, 0.1);
}

.document-organization-page {
  max-width: 1440px;
  margin: 0 auto;
  padding: 24px;
  min-height: calc(100vh - 64px);
  background: linear-gradient(135deg, #f6f7f8 0%, #e8f0f7 100%);
  animation: fadeIn 0.6s ease-out;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 24px;
  background: linear-gradient(135deg, #1173d4 0%, #3d8ce8 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 16px;
  margin-bottom: 24px;
  padding: 24px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  border: 1px solid #dbe0e6;
}

.query-form {
  flex: 1;
}

:deep(.el-form-item__label) {
  color: #617589;
  font-weight: 500;
}

.toolbar-actions {
  display: flex;
  gap: 12px;
  padding-bottom: 18px;
}

.editor-panel {
  margin-bottom: 24px;
  padding: 24px;
  background: #ffffff;
  border-radius: 12px;
  border: 1px solid #1173d4;
  box-shadow: 0 4px 12px rgba(17, 115, 212, 0.1);
  animation: slideUp 0.5s ease-out;
}

.editor-title {
  font-size: 18px;
  font-weight: 600;
  color: #111418;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.editor-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #dbe0e6;
}

/* Material Symbols Integration */
.btn-icon {
  font-family: 'Material Symbols Outlined';
  font-size: 18px;
  vertical-align: middle;
  margin-right: 4px;
}

/* Table Enhancements */
:deep(.el-table) {
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #dbe0e6;
}

:deep(.el-table th.el-table__cell) {
  background-color: #f8fafc;
  color: #617589;
  font-weight: 600;
}

/* Animations */
@keyframes fadeIn {
  0% { opacity: 0; }
  100% { opacity: 1; }
}

@keyframes slideUp {
  0% { transform: translateY(20px); opacity: 0; }
  100% { transform: translateY(0); opacity: 1; }
}
</style>
