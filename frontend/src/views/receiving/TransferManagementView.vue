<template>
  <div class="transfer-page">
    <el-card shadow="never">
      <el-collapse v-model="activePanels" class="transfer-collapse">
        <el-collapse-item name="header">
          <template #title>
            <div class="sub-card__title">申请头</div>
          </template>
          <el-form ref="headerFormRef" :model="headerForm" :rules="headerRules" label-width="120px">
            <div class="header-grid">
            <el-form-item label="申请人">
              <el-input v-model="currentUserName" class="input-w180" disabled />
            </el-form-item>
            <el-form-item label="申请人部门">
              <el-input v-model="currentUserDept" class="input-w180" disabled />
            </el-form-item>
            <el-form-item label="申请日期" prop="applicationDate">
              <el-input :model-value="headerForm.applicationDate" class="input-w180" disabled />
            </el-form-item>
            <el-form-item label="文档类型" prop="documentTypeCode">
              <CommonTreeSelect
                v-model="headerForm.documentTypeCode"
                :data="documentTypeTree"
                :props="{ label: 'typeName', value: 'typeCode' }"
                class="input-w180"
                @change="handleHeaderDocTypeChange"
              />
            </el-form-item>
            <el-form-item label="移交方式" prop="applyMethod">
              <el-select v-model="headerForm.applyMethod" class="input-w180" @change="onApplyMethodChanged">
                <el-option v-for="o in applyMethodOptions" :key="o.code" :label="o.name" :value="o.code" />
              </el-select>
            </el-form-item>
            <el-form-item label="邮寄方式" prop="expressType">
              <el-select v-model="headerForm.expressType" class="input-w180" clearable :disabled="headerForm.applyMethod !== 'MAIL'">
                <el-option v-for="o in expressTypeOptions" :key="o.code" :label="o.name" :value="o.code" />
              </el-select>
            </el-form-item>
            <el-form-item label="邮寄单号" prop="expressNumber">
              <el-input v-model="headerForm.expressNumber" class="input-w180" :disabled="headerForm.applyMethod !== 'MAIL'" />
            </el-form-item>
            <el-form-item label="文档接收人" prop="documentRecipient">
              <el-select v-model="headerForm.documentRecipient" class="input-w180" filterable>
                <el-option v-for="u in userOptions" :key="u.id" :label="u.name" :value="u.id" />
              </el-select>
            </el-form-item>
            <el-form-item v-if="showHandoverFormField" label="移交形式">
              <el-select v-model="headerForm.handoverForm" class="input-w180" clearable>
                <el-option v-for="o in handoverFormOptions" :key="o.code" :label="o.name" :value="o.code" />
              </el-select>
            </el-form-item>
            <el-form-item label="申请描述" class="span-2">
              <el-input v-model="headerForm.applicationDescription" type="textarea" :rows="2" />
            </el-form-item>
            </div>
          </el-form>
        </el-collapse-item>

        <el-collapse-item name="details">
          <template #title>
            <div class="sub-card__title row-between collapse-title">
              <span>申请行</span>
              <div>
                <el-button type="primary" @click.stop="addRow">新增</el-button>
                <el-button type="primary" plain @click.stop="pickerVisible = true">添加待归档数据</el-button>
              </div>
            </div>
          </template>
          <el-table :data="detailRows" border>
          <el-table-column type="index" label="序号" width="64" />
          <el-table-column label="操作" width="160" fixed="left">
            <template #default="{ row, $index }">
              <el-button link type="primary" @click="openAttachmentDialog(row, $index)">上传附件</el-button>
              <el-button link type="danger" @click="removeRow($index)">删除</el-button>
            </template>
          </el-table-column>
          <el-table-column label="公司" min-width="160">
            <template #default="{ row }">
              <el-select v-model="row.companyProjectCode" filterable clearable><el-option v-for="o in companyOptions" :key="o.code" :label="o.name" :value="o.code" /></el-select>
            </template>
          </el-table-column>
          <el-table-column label="文档业务编码" min-width="160"><template #default="{ row }"><el-input v-model="row.docBusiNo" /></template></el-table-column>
          <el-table-column label="文档名称" min-width="180"><template #default="{ row }"><el-input v-model="row.docName" /></template></el-table-column>
          <el-table-column label="业务模块" min-width="160">
            <template #default="{ row }"><el-select v-model="row.busiModuleCode" filterable clearable><el-option v-for="o in busiModuleOptions" :key="o.code" :label="o.name" :value="o.code" /></el-select></template>
          </el-table-column>
          <el-table-column label="归档地" min-width="160"><template #default="{ row }"><el-input v-model="row.archPlaceAlpha2Code" /></template></el-table-column>
          <el-table-column label="开始档期" min-width="150"><template #default="{ row }"><el-date-picker v-model="row.startArchPeriod" type="month" value-format="YYYY-MM" /></template></el-table-column>
          <el-table-column label="结束档期" min-width="150"><template #default="{ row }"><el-date-picker v-model="row.endArchPeriod" type="month" value-format="YYYY-MM" /></template></el-table-column>
          <el-table-column label="文档生成日期" min-width="150"><template #default="{ row }"><el-date-picker v-model="row.docGenerationDate" type="date" value-format="YYYY-MM-DD" /></template></el-table-column>
          <el-table-column label="载体类型" min-width="140">
            <template #default="{ row }">
              <el-select v-model="row.carrierType" clearable>
                <el-option v-for="o in carrierTypeOptions" :key="o.code" :label="o.name" :value="o.code" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column v-for="f in extFields" :key="f.fieldCode" :label="f.fieldName" min-width="150">
            <template #default="{ row }"><el-input v-model="row.extValues[f.fieldCode]" /></template>
          </el-table-column>
          <el-table-column label="份数" min-width="120"><template #default="{ row }"><el-input-number v-model="row.archCopies" :min="1" :precision="0" /></template></el-table-column>
          <el-table-column label="备注" min-width="150"><template #default="{ row }"><el-input v-model="row.remark" /></template></el-table-column>
          <el-table-column label="描述" min-width="180"><template #default="{ row }"><el-input v-model="row.description" type="textarea" :rows="1" /></template></el-table-column>
          </el-table>
        </el-collapse-item>
      </el-collapse>

      <div class="footer-actions">
        <el-button @click="cancel">取消</el-button>
        <el-button :loading="saving" @click="saveDraft">保存</el-button>
        <el-button type="primary" :loading="submitting" @click="submit">提交</el-button>
      </div>
    </el-card>

    <el-dialog v-model="pickerVisible" title="添加待归档数据" width="80%">
      <el-form :inline="true" :model="pickerFilter" class="picker-form">
        <el-form-item label="文档类型"><el-input :model-value="headerForm.documentTypeCode" class="picker-input" disabled /></el-form-item>
        <el-form-item label="文档名称"><el-input v-model="pickerFilter.documentName" /></el-form-item>
        <el-form-item label="业务编码"><el-input v-model="pickerFilter.businessCode" /></el-form-item>
        <el-form-item label="公司"><el-select v-model="pickerFilter.companyProjectCode" class="picker-input" clearable><el-option v-for="o in companyOptions" :key="o.code" :label="o.name" :value="o.code" /></el-select></el-form-item>
        <el-form-item class="picker-query-item"><el-button type="primary" @click="queryPicker">查询</el-button></el-form-item>
      </el-form>
      <el-table :data="pickerRows" @selection-change="onPickerSelect" border>
        <el-table-column type="selection" width="50" :selectable="isPickerSelectable" />
        <el-table-column prop="businessCode" label="文档业务编码" min-width="160" />
        <el-table-column prop="documentName" label="文档名称" min-width="180" />
        <el-table-column prop="companyProjectCode" label="公司" min-width="120" />
        <el-table-column prop="archiveTypeCode" label="文档类型" min-width="120" />
      </el-table>
      <template #footer>
        <el-button @click="pickerVisible = false">取消</el-button>
        <el-button type="primary" @click="addFromPicker">添加</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="attachmentDialogVisible" title="申请行附件" width="760px">
      <div class="row-between" style="margin-bottom: 12px;">
        <el-upload :show-file-list="false" :before-upload="beforeUploadDetailAttachment">
          <el-button type="primary">选择文件并上传</el-button>
        </el-upload>
      </div>
      <el-table :data="detailAttachmentList" border v-loading="attachmentLoading">
        <el-table-column prop="fileName" label="文件名" min-width="260" show-overflow-tooltip />
        <el-table-column prop="fileSize" label="大小(B)" width="120" />
        <el-table-column prop="creationDate" label="上传时间" min-width="180" />
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button link type="primary" @click="downloadAttachment(row)">下载</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import axios from 'axios'
import CommonTreeSelect from '../../components/CommonTreeSelect.vue'
import { fetchDocumentTypeTree } from '../../api/modules/documentType'
import { fetchArchiveCreateOptions, fetchEffectiveDocumentTypeExtFields, queryArchives } from '../../api/modules/archiveManagement'
import { fetchDictionaryItems } from '../../api/modules/dictionary'
import {
  createTransferApplication,
  downloadTransferApplicationDetailAttachment,
  getTransferApplication,
  listTransferApplicationDetailAttachments,
  updateTransferApplication,
  uploadTransferApplicationDetailAttachment
} from '../../api/modules/transferApplications'
import type {
  ArchiveCreateOptions,
  DictionaryItem,
  DocumentTypeExtField,
  DocumentTypeTreeNode,
  TransferApplicationCreateCommand,
  TransferApplicationDetailAttachment
} from '../../types'

interface LabelOption { code: string; name: string }
interface TransferDetailRow {
  applicationDetailId?: number
  companyProjectCode: string
  docBusiNo: string
  docName: string
  busiModuleCode: string
  archPlaceAlpha2Code: string
  carrierType: string
  endArchPeriod: string
  startArchPeriod: string
  docGenerationDate?: string
  archCopies: number
  remark?: string
  description?: string
  extValues: Record<string, string>
  attachments?: TransferApplicationDetailAttachment[]
}

const DEFAULT_TENANT_ID = 1
const route = useRoute()
const router = useRouter()
const headerFormRef = ref<FormInstance>()
const activePanels = ref(['header', 'details'])
const submitting = ref(false)
const saving = ref(false)
const savedApplicationId = ref<number | null>(null)
const applicationNumber = ref(`TR-${Date.now()}`)
const pickerVisible = ref(false)
const pickerRows = ref<any[]>([])
const pickerSelection = ref<any[]>([])
const extFields = ref<DocumentTypeExtField[]>([])
const documentTypeTree = ref<DocumentTypeTreeNode[]>([])
const companyOptions = ref<LabelOption[]>([])
const carrierTypeOptions = ref<LabelOption[]>([])
const busiModuleOptions = ref<LabelOption[]>([])
const userOptions = ref([{ id: 1, name: '张三' }, { id: 2, name: '李四' }, { id: 3, name: '王五' }])
const applyMethodOptions = ref<LabelOption[]>([{ code: 'DIRECT', name: '直接移交' }, { code: 'MAIL', name: '邮寄' }])
const expressTypeOptions = ref<LabelOption[]>([{ code: 'SF', name: '顺丰' }, { code: 'EMS', name: 'EMS' }, { code: 'OTHER', name: '其他' }])
const handoverFormOptions = ref<LabelOption[]>([])
const SPECIAL_DOCUMENT_TYPE_NAME = '会计凭证补充资料'
const attachmentDialogVisible = ref(false)
const attachmentLoading = ref(false)
const currentAttachmentDetailId = ref<number | null>(null)
const currentAttachmentRowIndex = ref<number | null>(null)
const detailAttachmentList = ref<TransferApplicationDetailAttachment[]>([])

const currentUserId = ref(1)
const currentUserName = ref('张三')
const currentUserDept = ref('财务部')
const headerForm = reactive({
  applicationDate: '',
  documentTypeCode: '',
  applyMethod: 'DIRECT',
  expressType: '',
  expressNumber: '',
  documentRecipient: undefined as number | undefined,
  handoverForm: '',
  applicationDescription: ''
})
const detailRows = ref<TransferDetailRow[]>([])
const pickerFilter = reactive({
  documentName: '',
  businessCode: '',
  companyProjectCode: ''
})

const headerRules: FormRules = {
  documentTypeCode: [{ required: true, message: '请选择文档类型', trigger: 'change' }],
  applyMethod: [{ required: true, message: '请选择移交方式', trigger: 'change' }],
  documentRecipient: [{ required: true, message: '请选择文档接收人', trigger: 'change' }],
  expressType: [{ validator: (_r, v, cb) => (headerForm.applyMethod === 'MAIL' && !v ? cb(new Error('邮寄方式必填')) : cb()), trigger: 'change' }],
  expressNumber: [{ validator: (_r, v, cb) => (headerForm.applyMethod === 'MAIL' && !v ? cb(new Error('邮寄单号必填')) : cb()), trigger: 'blur' }]
}

function findDocumentTypeNameByCode(nodes: DocumentTypeTreeNode[], code: string): string {
  for (const node of nodes) {
    if (node.typeCode === code) return node.typeName
    if (Array.isArray(node.children) && node.children.length) {
      const found = findDocumentTypeNameByCode(node.children as DocumentTypeTreeNode[], code)
      if (found) return found
    }
  }
  return ''
}

const selectedDocumentTypeName = computed(() =>
  findDocumentTypeNameByCode(documentTypeTree.value, headerForm.documentTypeCode)
)

const showHandoverFormField = computed(() => selectedDocumentTypeName.value === SPECIAL_DOCUMENT_TYPE_NAME)

function addRow() {
  detailRows.value.push({
    companyProjectCode: '',
    docBusiNo: '',
    docName: '',
    busiModuleCode: '',
    archPlaceAlpha2Code: '',
    carrierType: carrierTypeOptions.value[0]?.code || '',
    endArchPeriod: '',
    startArchPeriod: '',
    docGenerationDate: '',
    archCopies: 1,
    remark: '',
    description: '',
    extValues: {}
  })
}

function removeRow(index: number) {
  detailRows.value.splice(index, 1)
}

async function openAttachmentDialog(row: TransferDetailRow, index: number) {
  if (!savedApplicationId.value) {
    ElMessage.warning('请先保存申请后再上传附件')
    return
  }
  const detailId = Number(row.applicationDetailId || 0)
  if (!detailId) {
    ElMessage.warning('请先保存申请后再上传附件')
    return
  }
  currentAttachmentDetailId.value = detailId
  currentAttachmentRowIndex.value = index
  attachmentDialogVisible.value = true
  await loadDetailAttachments()
}

async function loadDetailAttachments() {
  if (!savedApplicationId.value || !currentAttachmentDetailId.value) {
    detailAttachmentList.value = []
    return
  }
  attachmentLoading.value = true
  try {
    detailAttachmentList.value = await listTransferApplicationDetailAttachments(
      savedApplicationId.value,
      currentAttachmentDetailId.value
    )
  } finally {
    attachmentLoading.value = false
  }
}

const beforeUploadDetailAttachment = async (file: File) => {
  if (!savedApplicationId.value || !currentAttachmentDetailId.value) {
    ElMessage.warning('请先保存申请后再上传附件')
    return false
  }
  await uploadTransferApplicationDetailAttachment(savedApplicationId.value, currentAttachmentDetailId.value, file)
  ElMessage.success('附件上传成功')
  await loadDetailAttachments()
  if (currentAttachmentRowIndex.value != null) {
    detailRows.value[currentAttachmentRowIndex.value].attachments = [...detailAttachmentList.value]
  }
  return false
}

async function downloadAttachment(row: TransferApplicationDetailAttachment) {
  if (!savedApplicationId.value || !currentAttachmentDetailId.value) return
  const blob = await downloadTransferApplicationDetailAttachment(
    savedApplicationId.value,
    currentAttachmentDetailId.value,
    row.attachmentId
  )
  const url = window.URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = row.fileName
  a.click()
  window.URL.revokeObjectURL(url)
}

async function handleHeaderDocTypeChange() {
  detailRows.value.forEach((r) => { r.extValues = {} })
  if (!showHandoverFormField.value) {
    headerForm.handoverForm = ''
  }
  if (!headerForm.documentTypeCode) {
    extFields.value = []
    return
  }
  extFields.value = (await fetchEffectiveDocumentTypeExtFields(headerForm.documentTypeCode))
    .sort((a, b) => (a.formSortOrder ?? 0) - (b.formSortOrder ?? 0))
  detailRows.value.forEach((r) => {
    extFields.value.forEach((f) => { r.extValues[f.fieldCode] = '' })
  })
}

async function loadForEdit(applicationId: number) {
  const detail = await getTransferApplication(applicationId)
  savedApplicationId.value = detail.applicationId
  applicationNumber.value = detail.applicationNumber || applicationNumber.value
  headerForm.applicationDate = detail.applicationDate ? String(detail.applicationDate).slice(0, 10) : headerForm.applicationDate
  headerForm.documentTypeCode = detail.documentTypeCode || ''
  headerForm.applyMethod = detail.applyMethod || 'DIRECT'
  headerForm.expressType = detail.expressType || ''
  headerForm.expressNumber = detail.expressNumber || ''
  headerForm.documentRecipient = detail.documentRecipient ?? undefined
  headerForm.handoverForm = detail.handoverForm || ''
  headerForm.applicationDescription = detail.applicationDescription || ''
  await handleHeaderDocTypeChange()
  detailRows.value = (detail.details || []).map((item) => {
    const extMap: Record<string, string> = {}
    extFields.value.forEach((f) => { extMap[f.fieldCode] = '' })
    ;(item.extValues || []).forEach((ext) => { extMap[ext.fieldCode] = ext.value || '' })
    return {
      applicationDetailId: item.applicationDetailId,
      companyProjectCode: item.companyProjectCode || '',
      docBusiNo: item.docBusiNo || '',
      docName: item.docName || '',
      busiModuleCode: item.busiModuleCode || '',
      archPlaceAlpha2Code: item.archPlaceAlpha2Code || '',
      carrierType: item.carrierType || '',
      endArchPeriod: item.endArchPeriod || '',
      startArchPeriod: item.startArchPeriod || '',
      docGenerationDate: item.docGenerationDate || '',
      archCopies: Number(item.archCopies || 1),
      remark: item.remark || '',
      description: item.description || '',
      extValues: extMap,
      attachments: item.attachments || []
    }
  })
}

function onApplyMethodChanged() {
  if (headerForm.applyMethod !== 'MAIL') {
    headerForm.expressType = ''
    headerForm.expressNumber = ''
  }
}

function validateDetails(): boolean {
  if (!detailRows.value.length) {
    ElMessage.warning('请至少新增一条申请行')
    return false
  }
  for (const [idx, row] of detailRows.value.entries()) {
    if (!row.companyProjectCode || !row.docBusiNo || !row.docName || !row.busiModuleCode || !row.archPlaceAlpha2Code ||
      !row.startArchPeriod || !row.endArchPeriod || !headerForm.documentTypeCode || !row.archCopies || !row.carrierType) {
      ElMessage.error(`第${idx + 1}行存在必填项未填写`)
      return false
    }
    for (const f of extFields.value) {
      if (f.requiredFlag === 'Y' && !row.extValues[f.fieldCode]) {
        ElMessage.error(`第${idx + 1}行扩展字段“${f.fieldName}”必填`)
        return false
      }
    }
  }
  return true
}

function toCreateCommand(applicationStatus: string): TransferApplicationCreateCommand {
  return {
    applicationId: savedApplicationId.value ?? undefined,
    applicationNumber: applicationNumber.value,
    applicant: currentUserId.value,
    applicationDate: `${headerForm.applicationDate}T00:00:00`,
    department: currentUserDept.value,
    documentTypeCode: headerForm.documentTypeCode,
    applyMethod: headerForm.applyMethod,
    expressType: headerForm.expressType || undefined,
    expressNumber: headerForm.expressNumber || undefined,
    documentRecipient: Number(headerForm.documentRecipient),
    handoverForm: headerForm.handoverForm || undefined,
    applicationStatus,
    applicationDescription: headerForm.applicationDescription || undefined,
    tenantid: DEFAULT_TENANT_ID,
    details: detailRows.value.map((row) => ({
      companyProjectCode: row.companyProjectCode,
      docBusiNo: row.docBusiNo,
      docName: row.docName,
      busiModuleCode: row.busiModuleCode,
      archPlaceAlpha2Code: row.archPlaceAlpha2Code,
      endArchPeriod: row.endArchPeriod,
      startArchPeriod: row.startArchPeriod,
      archTypeCode: headerForm.documentTypeCode,
      carrierType: row.carrierType,
      docGenerationDate: row.docGenerationDate || undefined,
      archCopies: Number(row.archCopies),
      remark: row.remark || undefined,
      description: row.description || undefined,
      extValues: Object.entries(row.extValues).map(([fieldCode, value]) => ({ fieldCode, value }))
    }))
  }
}

async function saveDraft() {
  const valid = await headerFormRef.value?.validate().catch(() => false)
  if (!valid || !validateDetails()) return
  saving.value = true
  try {
    const payload = toCreateCommand('DRAFT')
    const res = savedApplicationId.value
      ? await updateTransferApplication(savedApplicationId.value, payload)
      : await createTransferApplication(payload)
    savedApplicationId.value = res.applicationId
    ElMessage.success('已保存草稿')
  } catch (e) {
    console.error(e)
    ElMessage.error(resolveErrorMessage(e, '保存失败，请重试'))
  } finally {
    saving.value = false
  }
}

async function submit() {
  const valid = await headerFormRef.value?.validate().catch(() => false)
  if (!valid || !validateDetails()) return
  submitting.value = true
  try {
    const payload = toCreateCommand('SUBMITTED')
    const res = savedApplicationId.value
      ? await updateTransferApplication(savedApplicationId.value, payload)
      : await createTransferApplication(payload)
    savedApplicationId.value = res.applicationId
    ElMessage.success('提交成功，已向文档接收人发起审批流程')
    router.push('/archive-management/transfer-query')
  } catch (e) {
    console.error(e)
    ElMessage.error(resolveErrorMessage(e, '提交失败，请重试'))
  } finally {
    submitting.value = false
  }
}

function cancel() {
  router.push('/archive-management/transfer-query')
}

async function queryPicker() {
  if (!headerForm.documentTypeCode) {
    ElMessage.warning('请先选择申请头文档类型')
    return
  }
  const res = await queryArchives({
    documentTypeCode: headerForm.documentTypeCode,
    documentName: pickerFilter.documentName || undefined,
    businessCode: pickerFilter.businessCode || undefined,
    companyProjectCode: pickerFilter.companyProjectCode || undefined,
    excludeSubmittedTransferApplied: true
  })
  pickerRows.value = res.records ?? []
}

function onPickerSelect(rows: any[]) {
  pickerSelection.value = rows
}

function isRowBlank(row: TransferDetailRow) {
  const noExtValue = !Object.values(row.extValues || {}).some((v) => String(v || '').trim())
  return !row.companyProjectCode
    && !row.docBusiNo
    && !row.docName
    && !row.busiModuleCode
    && !row.archPlaceAlpha2Code
    && !row.endArchPeriod
    && !row.startArchPeriod
    && !row.docGenerationDate
    && Number(row.archCopies || 0) <= 1
    && !String(row.remark || '').trim()
    && !String(row.description || '').trim()
    && noExtValue
}

function isPickerSelectable(row: any) {
  const businessCode = String(row.businessCode ?? '').trim()
  if (!businessCode) return false
  return !detailRows.value.some((item) => String(item.docBusiNo || '').trim() === businessCode)
}

function addFromPicker() {
  if (!pickerSelection.value.length) {
    ElMessage.warning('请先选择待归档数据')
    return
  }
  const addedBusinessCodes = new Set(detailRows.value.map((item) => String(item.docBusiNo || '').trim()).filter(Boolean))
  let addedCount = 0
  pickerSelection.value.forEach((item) => {
    const businessCode = String(item.businessCode ?? '').trim()
    if (!businessCode || addedBusinessCodes.has(businessCode)) {
      return
    }
    const row: TransferDetailRow = {
      companyProjectCode: item.companyProjectCode ?? '',
      docBusiNo: businessCode,
      docName: item.documentName ?? '',
      busiModuleCode: '',
      archPlaceAlpha2Code: item.archiveDestination ?? '',
      carrierType: carrierTypeOptions.value[0]?.code || '',
      startArchPeriod: item.beginPeriod ?? '',
      endArchPeriod: item.endPeriod ?? '',
      docGenerationDate: item.documentDate ?? '',
      archCopies: 1,
      remark: item.remark ?? '',
      description: '',
      extValues: {}
    }
    const extFromArchive = (item.extValues && typeof item.extValues === 'object')
      ? (item.extValues as Record<string, unknown>)
      : {}
    extFields.value.forEach((f) => {
      const raw = extFromArchive[f.fieldCode]
      row.extValues[f.fieldCode] = raw == null ? '' : String(raw)
    })
    addedBusinessCodes.add(businessCode)
    detailRows.value.push(row)
    addedCount += 1
  })
  if (addedCount > 0) {
    detailRows.value = detailRows.value.filter((row) => !isRowBlank(row))
  }
  pickerSelection.value = []
  pickerVisible.value = false
  if (addedCount === 0) {
    ElMessage.warning('没有可新增的数据（已存在或无效）')
    return
  }
  ElMessage.success(`已添加${addedCount}条`)
}

function toLabelOptions(items: DictionaryItem[]): LabelOption[] {
  return items.filter((i) => i.enabledFlag === 'Y').map((i) => ({ code: i.itemCode, name: i.itemName }))
}

function resolveErrorMessage(error: unknown, fallback: string) {
  if (axios.isAxiosError(error)) {
    const data = error.response?.data
    if (data && typeof data === 'object') {
      const payload = data as { msg?: string; message?: string }
      if (payload.msg) return payload.msg
      if (payload.message) return payload.message
    }
  }
  if (error instanceof Error && error.message) return error.message
  return fallback
}

onMounted(async () => {
  headerForm.applicationDate = new Date().toISOString().slice(0, 10)
  const [tree, options, businessModules, applyMethods, expressTypes, handoverForms] = await Promise.all([
    fetchDocumentTypeTree(),
    fetchArchiveCreateOptions(),
    fetchDictionaryItems('BUSINESS_MOUDLE').catch(() => []),
    fetchDictionaryItems('TRANSFER_APPLY_METHOD').catch(() => []),
    fetchDictionaryItems('TRANSFER_EXPRESS_TYPE').catch(() => []),
    fetchDictionaryItems('HANDOVER_FORM').catch(() => [])
  ])
  documentTypeTree.value = tree
  companyOptions.value = (options as ArchiveCreateOptions).companyProjects ?? []
  carrierTypeOptions.value = (options as ArchiveCreateOptions).carrierTypes ?? []
  busiModuleOptions.value = toLabelOptions(businessModules as DictionaryItem[])
  const applyMethodDict = toLabelOptions(applyMethods as DictionaryItem[])
  const expressDict = toLabelOptions(expressTypes as DictionaryItem[])
  const handoverFormDict = toLabelOptions(handoverForms as DictionaryItem[])
  if (applyMethodDict.length) applyMethodOptions.value = applyMethodDict
  if (expressDict.length) expressTypeOptions.value = expressDict
  handoverFormOptions.value = handoverFormDict
  const editingIdRaw = route.query.applicationId
  const editingId = typeof editingIdRaw === 'string' ? Number(editingIdRaw) : NaN
  if (Number.isFinite(editingId) && editingId > 0) {
    try {
      await loadForEdit(editingId)
    } catch (e) {
      console.error(e)
      ElMessage.error(resolveErrorMessage(e, '加载申请数据失败'))
      addRow()
    }
  } else {
    addRow()
  }
})
</script>

<style scoped>
.transfer-page { display: grid; gap: 16px; }
.sub-card { margin-bottom: 16px; }
.sub-card__title { font-weight: 600; }
.row-between { display: flex; justify-content: space-between; align-items: center; }
.transfer-collapse { margin-bottom: 16px; }
.collapse-title { width: 100%; padding-right: 8px; }
.header-grid { display: grid; grid-template-columns: repeat(4, minmax(180px, 1fr)); gap: 10px; }
.span-2 { grid-column: span 2; }
.input-w180 { width: 180px; }
.footer-actions { display: flex; justify-content: flex-end; gap: 10px; }
.picker-form { width: 100%; }
.picker-input { width: 180px; }
.picker-query-item { margin-left: auto; }
</style>
