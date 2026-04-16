<template>
  <div class="doc-detail">
    <section class="doc-detail__title">
      <div class="doc-detail__head-row">
        <h1 class="doc-detail__biz-code">{{ headlineTitle }}</h1>
        <div class="doc-detail__head-actions">
          <el-button :disabled="submitting" @click="saveDraft">保存草稿</el-button>
          <el-button type="primary" :loading="submitting" @click="submit">提交</el-button>
          <el-button :disabled="submitting" @click="cancel">取消</el-button>
        </div>
      </div>
    </section>

    <el-card shadow="never" class="doc-section">
      <template #header>
        <div class="doc-section__header">
          <div class="doc-section__title">
            <el-icon><Document /></el-icon>
            <span>文档基本信息</span>
          </div>
        </div>
      </template>
      <div v-if="loading"><el-skeleton :rows="12" animated /></div>
      <div v-else class="doc-info-grid">
        <div
          v-for="spec in createBasicSpecs"
          :key="spec.key"
          class="doc-info-item"
          :class="{ 'doc-info-item--full': spec.full }"
        >
          <div class="doc-info-item__label">{{ spec.label }}</div>
          <div class="doc-info-item__value">
            <el-select
              v-if="spec.key === 'documentTypeCode'"
              v-model="form.documentTypeCode"
              filterable
              clearable
              placeholder="请选择文档类型"
              class="doc-edit-control"
            >
              <el-option v-for="t in options.documentTypes" :key="t.code" :label="t.name" :value="t.code" />
            </el-select>
            <el-input
              v-else-if="spec.key === 'businessCode'"
              v-model="form.businessCode"
              clearable
              placeholder="请输入文档业务编码"
              class="doc-edit-control"
            />
            <el-select
              v-else-if="spec.key === 'companyProjectCode'"
              v-model="form.companyProjectCode"
              filterable
              clearable
              placeholder="请从公司配置中选择公司"
              class="doc-edit-control"
            >
              <el-option
                v-for="c in options.companyProjects"
                :key="c.code"
                :label="`${c.name}（${c.code}）`"
                :value="c.code"
              />
            </el-select>
            <el-select
              v-else-if="spec.key === 'archiveTypeCode'"
              v-model="form.archiveTypeCode"
              filterable
              clearable
              :disabled="!effectiveDocumentTypeCode.trim()"
              placeholder="请先选择文档类型，再选三级业务模块"
              class="doc-edit-control"
            >
              <el-option v-for="a in businessModuleOptions" :key="a.code" :label="a.name" :value="a.code" />
            </el-select>
            <el-date-picker
              v-else-if="spec.key === 'beginPeriod'"
              v-model="form.beginPeriod"
              type="month"
              value-format="YYYY-MM"
              placeholder="开始档期"
              class="doc-edit-control"
            />
            <el-date-picker
              v-else-if="spec.key === 'endPeriod'"
              v-model="form.endPeriod"
              type="month"
              value-format="YYYY-MM"
              placeholder="可选，不填则与开始档期相同"
              class="doc-edit-control"
            />
            <el-select
              v-else-if="spec.key === 'archiveDestination'"
              v-model="form.archiveDestination"
              filterable
              clearable
              placeholder="请选择归档地"
              class="doc-edit-control"
            >
              <el-option v-for="o in options.archiveDestinations" :key="o.code" :label="o.name" :value="o.code" />
            </el-select>
            <el-input
              v-else-if="spec.key === 'originPlace'"
              v-model="form.originPlace"
              clearable
              placeholder="产生地（可由责任人工作国家带出）"
              class="doc-edit-control"
            />
            <el-input
              v-else-if="spec.key === 'documentName'"
              v-model="form.documentName"
              clearable
              placeholder="请输入文档名称"
              class="doc-edit-control"
            />
            <el-date-picker
              v-else-if="spec.key === 'documentDate'"
              v-model="form.documentDate"
              type="datetime"
              value-format="YYYY-MM-DD HH:mm:ss"
              placeholder="文档生成日期"
              class="doc-edit-control"
            />
            <el-input
              v-else-if="spec.key === 'dutyPerson'"
              v-model="form.dutyPerson"
              clearable
              placeholder="登录名，失焦后带出部门与产生地"
              class="doc-edit-control"
              @blur="onDutyPersonBlur"
            />
            <el-input
              v-else-if="spec.key === 'dutyDepartment'"
              v-model="form.dutyDepartment"
              clearable
              placeholder="文档责任部门"
              class="doc-edit-control"
            />
            <el-select
              v-else-if="spec.key === 'carrierTypeCode'"
              v-model="form.carrierTypeCode"
              filterable
              clearable
              placeholder="载体类型"
              class="doc-edit-control"
            >
              <el-option v-for="o in options.carrierTypes" :key="o.code" :label="o.name" :value="o.code" />
            </el-select>
            <el-input
              v-else-if="spec.key === 'sourceSystem'"
              v-model="form.sourceSystem"
              clearable
              placeholder="系统来源"
              class="doc-edit-control"
            />
            <el-select
              v-else-if="spec.key === 'securityLevelCode'"
              v-model="form.securityLevelCode"
              filterable
              clearable
              placeholder="密级"
              class="doc-edit-control"
            >
              <el-option v-for="o in options.securityLevels" :key="o.code" :label="o.name" :value="o.code" />
            </el-select>
            <el-input
              v-else-if="spec.key === 'remark'"
              v-model="form.remark"
              type="textarea"
              :rows="4"
              placeholder="请输入描述"
              class="doc-edit-control doc-edit-control--block"
            />
          </div>
        </div>
      </div>
    </el-card>

    <el-card shadow="never" class="doc-section">
      <template #header>
        <div class="doc-section__header">
          <div class="doc-section__title">
            <el-icon><Collection /></el-icon>
            <span>扩展信息</span>
          </div>
          <el-button text @click="toggleSection('ext')">
            <el-icon><ArrowUp v-if="sectionOpen.ext" /><ArrowDown v-else /></el-icon>
          </el-button>
        </div>
      </template>
      <div v-show="sectionOpen.ext" class="doc-info-grid">
        <div class="doc-info-item doc-info-item--full">
          <div class="doc-info-item__value doc-muted-hint">国家、地区部、代表处、公司标签由公司信息自动带出，不可编辑。</div>
        </div>
        <div v-for="row in extCreateRows" :key="row.key" class="doc-info-item">
          <div class="doc-info-item__label">{{ row.label }}</div>
          <div class="doc-info-item__value">
            <template v-if="row.readonly">{{ row.display }}</template>
            <el-input v-else v-model="extForm[row.key]" clearable placeholder="请输入" class="doc-edit-control" />
          </div>
        </div>
        <el-empty v-if="!extCreateRows.length" description="请先选择文档类型" />
      </div>
    </el-card>

    <el-card shadow="never" class="doc-section">
      <template #header>
        <div class="doc-section__header">
          <div class="doc-section__title">
            <el-icon><Box /></el-icon>
            <span>归档信息</span>
          </div>
          <el-button text @click="toggleSection('archive')">
            <el-icon><ArrowUp v-if="sectionOpen.archive" /><ArrowDown v-else /></el-icon>
          </el-button>
        </div>
      </template>
      <div v-show="sectionOpen.archive" class="doc-info-grid">
        <div class="doc-info-item">
          <div class="doc-info-item__label">文档组织</div>
          <div class="doc-info-item__value">
            <el-input
              :model-value="documentOrganizationDisplay"
              readonly
              disabled
              class="doc-edit-control"
              :placeholder="documentOrganizationPlaceholder"
            />
            <div class="doc-flow-hint">由归档流向规则决定（公司、文档类型、业务模块、归档地匹配后自动带出）</div>
          </div>
        </div>
        <div class="doc-info-item">
          <div class="doc-info-item__label">是否可见</div>
          <div class="doc-info-item__value">
            <el-input v-model="extForm.visibility" clearable placeholder="默认：是" class="doc-edit-control" />
          </div>
        </div>
        <div class="doc-info-item">
          <div class="doc-info-item__label">条码模块</div>
          <div class="doc-info-item__value">
            <el-input v-model="extForm.barcodeModule" clearable placeholder="请输入" class="doc-edit-control" />
          </div>
        </div>
        <div class="doc-info-item">
          <div class="doc-info-item__label">保管状态</div>
          <div class="doc-info-item__value">
            <el-select
              v-model="form.custodyStatus"
              filterable
              clearable
              placeholder="请选择保管状态"
              class="doc-edit-control"
            >
              <el-option v-for="o in options.custodyStatuses" :key="o.code" :label="o.name" :value="o.code" />
            </el-select>
          </div>
        </div>
      </div>
    </el-card>

    <el-card shadow="never" class="doc-section">
      <template #header>
        <div class="doc-section__header">
          <div class="doc-section__title">
            <el-icon><Document /></el-icon>
            <span>本次操作</span>
          </div>
        </div>
      </template>
      <div class="doc-info-grid">
        <div class="doc-info-item doc-info-item--full">
          <div class="doc-info-item__label">备注</div>
          <div class="doc-info-item__value">
            <el-input
              v-model="operationRemark"
              type="textarea"
              :rows="2"
              placeholder="可选，将写入文档操作日志"
              class="doc-edit-control doc-edit-control--block"
            />
          </div>
        </div>
        <div class="doc-info-item doc-info-item--full">
          <div class="doc-info-item__label">补充说明附件</div>
          <div class="doc-info-item__value">
            <el-upload :http-request="handleAuditUpload" :show-file-list="false" multiple>
              <el-button type="primary" plain>上传文件</el-button>
            </el-upload>
            <div v-if="auditAttachments.length" class="audit-att-tags">
              <el-tag
                v-for="(a, i) in auditAttachments"
                :key="a.storageKey + i"
                closable
                class="audit-att-tag"
                @close="auditAttachments.splice(i, 1)"
              >
                {{ a.fileName || a.storageKey }}
              </el-tag>
            </div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ArrowDown, ArrowUp, Box, Collection, Document } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import type { UploadRequestOptions } from 'element-plus'
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { fetchLevel3Modules } from '../../api/modules/documentType'
import {
  createPendingDocument,
  fetchArchiveCreateOptions,
  fetchEffectiveDocumentTypeExtFields,
  getArchiveDetail,
  resolveArchiveDefaults,
  updatePendingDocument,
  uploadPendingAuditAttachment
} from '../../api/modules/archiveManagement'
import { fetchCompanyProjectCountries, fetchCompanyProjectDetail } from '../../api/modules/companyProject'
import { fetchUserDutyProfile } from '../../api/modules/security'
import type { ArchiveCreateOptions, CompanyProjectDetail, LabelValueOption } from '../../types'
import { EXT_DETAIL_FIELD_ORDER, hardCodedExtLabelMap, isHardCodedFieldVisible } from './extFieldDisplayConfig'
import { CURRENT_OPERATOR_USER_ID } from '../../constants/currentUser'

type CreateBasicKey =
  | 'documentTypeCode'
  | 'businessCode'
  | 'companyProjectCode'
  | 'archiveTypeCode'
  | 'beginPeriod'
  | 'endPeriod'
  | 'archiveDestination'
  | 'originPlace'
  | 'documentName'
  | 'documentDate'
  | 'dutyPerson'
  | 'dutyDepartment'
  | 'carrierTypeCode'
  | 'sourceSystem'
  | 'securityLevelCode'
  | 'remark'

const createBasicSpecs: { label: string; key: CreateBasicKey; full?: boolean }[] = [
  { label: '文档类型', key: 'documentTypeCode' },
  { label: '文档业务编码', key: 'businessCode' },
  { label: '公司', key: 'companyProjectCode' },
  { label: '业务模块', key: 'archiveTypeCode' },
  { label: '开始档期', key: 'beginPeriod' },
  { label: '结束档期', key: 'endPeriod' },
  { label: '归档地', key: 'archiveDestination' },
  { label: '产生地', key: 'originPlace' },
  { label: '文档名称', key: 'documentName', full: true },
  { label: '文档生成日期', key: 'documentDate' },
  { label: '归档责任人', key: 'dutyPerson' },
  { label: '文档责任部门', key: 'dutyDepartment' },
  { label: '载体类型', key: 'carrierTypeCode' },
  { label: '系统来源', key: 'sourceSystem' },
  { label: '密级', key: 'securityLevelCode' },
  { label: '描述', key: 'remark', full: true }
]

/** 扩展区不重复展示（在归档信息中编辑） */
const ARCHIVE_CARD_EXT_KEYS = new Set(['visibility', 'barcodeModule'])

const route = useRoute()
const router = useRouter()

const loading = ref(true)
const submitting = ref(false)
/** 从「我的草稿」继续编辑时避免 watch 清空已加载的业务模块 */
const bootstrappingDraft = ref(false)
const resumeArchiveId = ref<number | null>(null)
const loadedRetentionYears = ref(10)
const operationRemark = ref('')
const auditAttachments = ref<Array<{ fileId: number; fileName?: string; storageKey?: string; fileSize?: number }>>([])
const extFieldNameMap = ref<Record<string, string>>({})
const businessModuleOptions = ref<LabelValueOption[]>([])
const countryNameByCode = ref<Record<string, string>>({})

const COMPANY_SYNC_EXT_KEYS = ['country', 'repOffice', 'region', 'companyTag'] as const
const READONLY_EXT_KEYS = new Set(['country', 'repOffice', 'region', 'companyTag'])

const options = reactive<ArchiveCreateOptions>({
  companyProjects: [],
  documentTypes: [],
  archiveDestinations: [],
  documentOrganizations: [],
  securityLevels: [],
  carrierTypes: [],
  attachmentTypes: [],
  archiveTypes: [],
  aiModels: [],
  geoCountries: [],
  geoRepOffices: [],
  geoRegions: [],
  custodyStatuses: []
})

const form = reactive({
  documentTypeCode: (typeof route.query.documentTypeCode === 'string' ? route.query.documentTypeCode : '') || '',
  businessCode: '',
  companyProjectCode: '',
  archiveTypeCode: '',
  beginPeriod: '',
  endPeriod: '',
  archiveDestination: '',
  originPlace: '',
  documentName: '',
  documentDate: '',
  dutyPerson: '',
  dutyDepartment: '',
  carrierTypeCode: '',
  sourceSystem: '',
  securityLevelCode: '',
  remark: '',
  documentOrganizationCode: '',
  custodyStatus: ''
})

const extForm = reactive<Record<string, string>>({})

const sectionOpen = reactive({
  ext: true,
  archive: true
})

const effectiveDocumentTypeCode = computed(() => form.documentTypeCode.trim())

const selectedDocTypeName = computed(() => {
  const t = options.documentTypes.find((x) => x.code === effectiveDocumentTypeCode.value)
  return t?.name || ''
})

const headlineTitle = computed(() => {
  if (form.businessCode.trim()) return form.businessCode.trim()
  if (resumeArchiveId.value != null) return '继续完善应归档数据（草稿）'
  return '新建应归档数据'
})

/** 文档组织编码由归档流向解析，展示名称+编码 */
const documentOrganizationDisplay = computed(() => {
  const code = form.documentOrganizationCode.trim()
  if (!code) return ''
  const o = options.documentOrganizations.find((x) => x.code === code)
  return o ? `${o.name}（${code}）` : code
})

const documentOrganizationPlaceholder = computed(() => {
  const company = form.companyProjectCode.trim()
  const docType = form.documentTypeCode.trim()
  const module = form.archiveTypeCode.trim()
  if (!company || !docType) {
    return '请先选择公司与文档类型'
  }
  if (!module) {
    return '请先选择业务模块'
  }
  return '未匹配到归档流向，请调整归档地或联系管理员配置归档流向规则'
})

const shouldShowExtKey = (key: string) => {
  if (ARCHIVE_CARD_EXT_KEYS.has(key)) return false
  const docType = selectedDocTypeName.value
  return isHardCodedFieldVisible(key, docType) || key in extForm
}

const formatCountryExtValue = (raw: unknown) => {
  if (raw === null || raw === undefined) return '-'
  const s = String(raw).trim()
  if (!s) return '-'
  return countryNameByCode.value[s] || s
}

const formatExtCell = (raw: unknown) => {
  if (raw === null || raw === undefined) return '-'
  const s = String(raw).trim()
  return s || '-'
}

const extCreateRows = computed(() => {
  const rows: { key: string; label: string; readonly: boolean; display: string }[] = []
  const used = new Set<string>()
  const pushRow = (key: string) => {
    if (used.has(key)) return
    if (!shouldShowExtKey(key)) return
    used.add(key)
    const readonly = READONLY_EXT_KEYS.has(key)
    const raw = extForm[key]
    rows.push({
      key,
      label: extFieldNameMap.value[key] || hardCodedExtLabelMap[key] || key,
      readonly,
      display: readonly ? (key === 'country' ? formatCountryExtValue(raw) : formatExtCell(raw)) : ''
    })
  }
  for (const key of EXT_DETAIL_FIELD_ORDER) {
    pushRow(key)
  }
  for (const key of Object.keys(extForm).sort()) {
    pushRow(key)
  }
  return rows
})

const toggleSection = (key: 'ext' | 'archive') => {
  sectionOpen[key] = !sectionOpen[key]
}

const matchOptionCode = (opts: { code: string; name: string }[], raw: string | undefined) => {
  if (!raw) return ''
  const u = String(raw).trim()
  if (!u) return ''
  if (opts.some((o) => o.code === u)) return u
  const byName = opts.find((o) => o.name === u)
  return byName?.code ?? u
}

const applyCompanyDetailToExt = (detail: CompanyProjectDetail) => {
  const cc = detail.countryCode?.trim() || ''
  if (cc) {
    extForm.country = countryNameByCode.value[cc] || cc
  } else {
    extForm.country = ''
  }
  const rep = options.geoRepOffices.find((o) => o.code === cc)
  const reg = options.geoRegions.find((o) => o.code === cc)
  extForm.repOffice = rep?.name || ''
  extForm.region = reg?.name || detail.managementArea?.trim() || ''
  extForm.companyTag = detail.companyTag?.trim() || ''
}

const applySelectedCompanyExtFields = async () => {
  const code = form.companyProjectCode.trim()
  if (!code) return
  try {
    const detail = await fetchCompanyProjectDetail(code)
    applyCompanyDetailToExt(detail)
  } catch {
    /* 演示环境可能无该公司详情 */
  }
}

/** 密级、文档组织等与归档流向规则一致 */
const applyArchiveFlowDefaults = async () => {
  if (!form.companyProjectCode.trim() || !form.documentTypeCode.trim()) return
  try {
    const d = await resolveArchiveDefaults({
      companyProjectCode: form.companyProjectCode.trim(),
      documentTypeCode: form.documentTypeCode.trim(),
      customRule: form.archiveTypeCode.trim() || undefined,
      archiveDestination: form.archiveDestination.trim() || undefined
    })
    if (d?.securityLevelCode) {
      const next = matchOptionCode(options.securityLevels, d.securityLevelCode)
      if (next) form.securityLevelCode = next
    }
    const org = (d?.documentOrganizationCode || '').trim()
    if (org) {
      form.documentOrganizationCode = matchOptionCode(options.documentOrganizations, org) || org
    } else {
      form.documentOrganizationCode = ''
    }
  } catch {
    /* ignore */
  }
}

const onDutyPersonBlur = async () => {
  const name = form.dutyPerson.trim()
  if (!name) return
  try {
    const p = await fetchUserDutyProfile(name)
    if (!p) return
    if (p.dutyDepartment?.trim()) form.dutyDepartment = p.dutyDepartment.trim()
    const wcc = p.workCountryCode?.trim()
    if (wcc) {
      form.originPlace = countryNameByCode.value[wcc] || wcc
    }
  } catch {
    /* 用户不存在或未配置档案字段 */
  }
}

const onDocumentTypeChanged = async () => {
  form.archiveTypeCode = ''
  businessModuleOptions.value = []
  await syncExtFieldsForDocType()
  const code = form.documentTypeCode.trim()
  if (code) {
    try {
      businessModuleOptions.value = await fetchLevel3Modules(code)
    } catch {
      businessModuleOptions.value = []
      ElMessage.error('加载业务模块失败')
    }
  }
  await applySelectedCompanyExtFields()
}

const syncExtFieldsForDocType = async () => {
  const code = form.documentTypeCode.trim()
  const vis = extForm.visibility
  const bc = extForm.barcodeModule
  Object.keys(extForm).forEach((k) => delete extForm[k])
  extForm.visibility = vis || '是'
  extForm.barcodeModule = bc || ''
  extFieldNameMap.value = {}
  if (!code) return
  const fields = await fetchEffectiveDocumentTypeExtFields(code).catch(() => [])
  extFieldNameMap.value = Object.fromEntries((fields || []).map((item) => [item.fieldCode, item.fieldName || item.fieldCode]))
  const docTypeName = selectedDocTypeName.value
  for (const key of EXT_DETAIL_FIELD_ORDER) {
    if (!isHardCodedFieldVisible(key, docTypeName)) continue
    if (!(key in extForm)) extForm[key] = ''
  }
  for (const key of Object.keys(extFieldNameMap.value)) {
    if (!isHardCodedFieldVisible(key, docTypeName)) continue
    if (!(key in extForm)) extForm[key] = ''
  }
}

watch(
  () => form.documentTypeCode,
  () => {
    if (bootstrappingDraft.value) return
    void onDocumentTypeChanged()
  }
)

watch(
  () => form.companyProjectCode,
  async (next) => {
    if (!next?.trim()) {
      for (const k of COMPANY_SYNC_EXT_KEYS) {
        if (k in extForm) extForm[k] = ''
      }
      return
    }
    await applySelectedCompanyExtFields()
  }
)

watch(
  () =>
    [form.companyProjectCode, form.documentTypeCode, form.archiveTypeCode, form.archiveDestination] as const,
  () => {
    void applyArchiveFlowDefaults()
  }
)

const toFormDateTime = (value: unknown): string => {
  if (value === null || value === undefined || value === '') return ''
  const text = String(value).trim()
  if (!text) return ''
  const normalized = text.includes('T') ? text : text.replace(' ', 'T')
  const date = new Date(normalized)
  if (Number.isNaN(date.getTime())) return text.slice(0, 19).replace('T', ' ')
  const pad = (n: number) => String(n).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`
}

const loadDraftIntoForm = async (docId: number) => {
  bootstrappingDraft.value = true
  try {
    const record = await getArchiveDetail(docId)
    if (String(record.lifecycleStatus || '').toUpperCase() !== 'DRAFT') {
      ElMessage.warning('仅草稿可从「我的草稿」以创建页形式继续编辑')
      resumeArchiveId.value = null
      await router.replace({ path: '/archive-management/pending-archive/create', query: {} })
      await onDocumentTypeChanged()
      if (!form.documentTypeCode) {
        extForm.visibility = '是'
        extForm.barcodeModule = ''
      }
      await applyArchiveFlowDefaults()
      return
    }
    resumeArchiveId.value = docId
    loadedRetentionYears.value =
      record.retentionPeriodYears != null && record.retentionPeriodYears > 0 ? record.retentionPeriodYears : 10
    form.documentTypeCode = matchOptionCode(options.documentTypes, record.documentTypeCode)
    form.businessCode = (record.businessCode || '').trim()
    form.companyProjectCode = matchOptionCode(options.companyProjects, record.companyProjectCode)
    const dt = form.documentTypeCode.trim()
    businessModuleOptions.value = dt ? await fetchLevel3Modules(dt).catch(() => []) : []
    form.archiveTypeCode =
      matchOptionCode(businessModuleOptions.value, record.businessModuleTypeCode) ||
      (record.businessModuleTypeCode || '').trim()
    form.beginPeriod = record.beginPeriod || ''
    form.endPeriod = record.endPeriod || ''
    form.archiveDestination = matchOptionCode(options.archiveDestinations, record.archiveDestination)
    form.originPlace = (record.originPlace || '').trim()
    form.documentName = (record.documentName || '').trim()
    form.documentDate = toFormDateTime(record.documentDate)
    form.dutyPerson = (record.dutyPerson || '').trim()
    form.dutyDepartment = String(record.dutyDepartment ?? '').trim()
    form.carrierTypeCode = matchOptionCode(options.carrierTypes, record.carrierTypeCode)
    form.sourceSystem = (record.sourceSystem || '').trim()
    form.securityLevelCode = matchOptionCode(options.securityLevels, record.securityLevelCode || record.securityLevelName)
    form.remark = (record.remark || '').trim()
    form.documentOrganizationCode = (record.documentOrganizationCode || '').trim()
    form.custodyStatus = matchOptionCode(options.custodyStatuses, record.custodyStatus) || 'UNARCHIVED'
    Object.keys(extForm).forEach((k) => delete extForm[k])
    const ext = record.extValues || {}
    for (const [k, v] of Object.entries(ext)) {
      if (k === 'visibility') continue
      extForm[k] = v != null ? String(v) : ''
    }
    extForm.visibility = String(record.documentVisibility ?? ext.visibility ?? '是').trim() || '是'
    const fields = await fetchEffectiveDocumentTypeExtFields(form.documentTypeCode.trim()).catch(() => [])
    extFieldNameMap.value = Object.fromEntries(
      (fields || []).map((item) => [item.fieldCode, item.fieldName || item.fieldCode])
    )
    const docTypeName = selectedDocTypeName.value
    for (const key of EXT_DETAIL_FIELD_ORDER) {
      if (!isHardCodedFieldVisible(key, docTypeName)) continue
      if (!(key in extForm)) extForm[key] = ''
    }
    for (const key of Object.keys(extFieldNameMap.value)) {
      if (!isHardCodedFieldVisible(key, docTypeName)) continue
      if (!(key in extForm)) extForm[key] = ''
    }
    await applySelectedCompanyExtFields()
    await applyArchiveFlowDefaults()
  } catch (e: unknown) {
    ElMessage.error(e instanceof Error ? e.message : '加载草稿失败')
    resumeArchiveId.value = null
    await router.replace({ path: '/archive-management/pending-archive/create', query: {} })
    await onDocumentTypeChanged()
  } finally {
    bootstrappingDraft.value = false
  }
}

onMounted(async () => {
  loading.value = true
  try {
    const [data, countries] = await Promise.all([
      fetchArchiveCreateOptions(),
      fetchCompanyProjectCountries().catch(() => [] as { countryCode: string; countryName: string }[])
    ])
    Object.assign(options, data)
    countryNameByCode.value = Object.fromEntries(
      (countries || []).map((c) => [c.countryCode, c.countryName])
    )
    const rawResume =
      typeof route.query.resumeDraftId === 'string'
        ? route.query.resumeDraftId.trim()
        : Array.isArray(route.query.resumeDraftId)
          ? String(route.query.resumeDraftId[0] || '').trim()
          : ''
    if (rawResume && /^\d+$/.test(rawResume)) {
      await loadDraftIntoForm(Number(rawResume))
    } else {
      await onDocumentTypeChanged()
      if (!form.documentTypeCode) {
        extForm.visibility = '是'
        extForm.barcodeModule = ''
      }
      await applyArchiveFlowDefaults()
    }
  } finally {
    loading.value = false
  }
})

const collectExtValues = (): Record<string, string> => {
  const out: Record<string, string> = {}
  for (const [k, v] of Object.entries(extForm)) {
    out[k] = v != null ? String(v) : ''
  }
  return out
}

const handleAuditUpload = async (opt: UploadRequestOptions) => {
  try {
    const ref = await uploadPendingAuditAttachment(opt.file as File)
    if (ref.fileId != null && ref.fileId > 0) {
      auditAttachments.value.push({
        fileId: ref.fileId,
        fileName: ref.fileName,
        storageKey: ref.storageKey,
        fileSize: ref.fileSize
      })
    }
    ElMessage.success('已上传')
    opt.onSuccess?.({} as any)
  } catch (e: unknown) {
    opt.onError?.(e as any)
    ElMessage.error(e instanceof Error ? e.message : '上传失败')
  }
}

const resolveRetentionYears = async (): Promise<number> => {
  let retention = 10
  const company = form.companyProjectCode.trim()
  const docType = form.documentTypeCode.trim()
  const module = form.archiveTypeCode.trim()
  const dest = form.archiveDestination.trim()
  try {
    const d = await resolveArchiveDefaults({
      companyProjectCode: company,
      documentTypeCode: docType,
      customRule: module || undefined,
      archiveDestination: dest || undefined
    })
    if (d?.retentionPeriodYears != null && d.retentionPeriodYears > 0) {
      retention = d.retentionPeriodYears
    }
  } catch {
    /* ignore */
  }
  return retention
}

const saveDraft = async () => {
  submitting.value = true
  try {
    const retention =
      resumeArchiveId.value != null ? loadedRetentionYears.value : await resolveRetentionYears()
    const extVals = collectExtValues()
    const payload = {
      operatorUserId: CURRENT_OPERATOR_USER_ID,
      documentTypeCode: form.documentTypeCode.trim(),
      companyProjectCode: form.companyProjectCode.trim(),
      archiveTypeCode: form.archiveTypeCode.trim(),
      businessCode: form.businessCode.trim() || undefined,
      beginPeriod: form.beginPeriod || '',
      endPeriod: form.endPeriod.trim() || undefined,
      archiveDestination: form.archiveDestination.trim() || undefined,
      originPlace: form.originPlace.trim() || undefined,
      documentName: form.documentName,
      documentDate: form.documentDate || '',
      dutyPerson: form.dutyPerson,
      dutyDepartment: form.dutyDepartment.trim() || undefined,
      carrierTypeCode: form.carrierTypeCode.trim() || 'ELECTRONIC',
      sourceSystem: form.sourceSystem.trim() || undefined,
      securityLevelCode: form.securityLevelCode.trim() || 'INTERNAL',
      remark: form.remark.trim() || undefined,
      documentOrganizationCode: form.documentOrganizationCode.trim() || 'DEFAULT',
      retentionPeriodYears: retention,
      custodyStatus: (form.custodyStatus || '').trim() || 'UNARCHIVED',
      submitMode: 'DRAFT' as const,
      operationRemark: operationRemark.value.trim() || undefined,
      auditAttachments: auditAttachments.value.length ? auditAttachments.value : undefined,
      extValues: extVals
    }
    if (resumeArchiveId.value != null) {
      const updated = await updatePendingDocument(resumeArchiveId.value, payload)
      if (updated.retentionPeriodYears != null && updated.retentionPeriodYears > 0) {
        loadedRetentionYears.value = updated.retentionPeriodYears
      }
      ElMessage.success('草稿已保存')
      if (route.query.from === 'workspace-drafts') {
        await router.push('/workspace/my-drafts')
      } else {
        await router.push('/archive-management/pending-archive/query')
      }
      return
    }
    await createPendingDocument(payload)
    ElMessage.success('草稿已保存')
    await router.push('/archive-management/pending-archive/query')
  } catch (e: unknown) {
    const msg = e instanceof Error ? e.message : '保存失败'
    ElMessage.error(msg)
  } finally {
    submitting.value = false
  }
}

const submit = async () => {
  if (!form.documentTypeCode.trim()) {
    ElMessage.warning('请选择文档类型')
    return
  }
  if (!form.businessCode.trim()) {
    ElMessage.warning('请填写文档业务编码')
    return
  }
  if (!form.archiveTypeCode.trim()) {
    ElMessage.warning('请选择业务模块')
    return
  }
  if (!form.beginPeriod) {
    ElMessage.warning('请选择开始档期')
    return
  }
  if (!form.documentName.trim()) {
    ElMessage.warning('请填写文档名称')
    return
  }
  if (!form.documentDate) {
    ElMessage.warning('请填写文档生成日期')
    return
  }
  submitting.value = true
  try {
    const retention =
      resumeArchiveId.value != null ? loadedRetentionYears.value : await resolveRetentionYears()
    const extVals = collectExtValues()
    const payload = {
      operatorUserId: CURRENT_OPERATOR_USER_ID,
      documentTypeCode: form.documentTypeCode.trim(),
      companyProjectCode: form.companyProjectCode.trim(),
      archiveTypeCode: form.archiveTypeCode.trim(),
      businessCode: form.businessCode.trim() || undefined,
      beginPeriod: form.beginPeriod,
      endPeriod: form.endPeriod.trim() || undefined,
      archiveDestination: form.archiveDestination.trim() || undefined,
      originPlace: form.originPlace.trim() || undefined,
      documentName: form.documentName.trim(),
      documentDate: form.documentDate,
      dutyPerson: form.dutyPerson,
      dutyDepartment: form.dutyDepartment.trim() || undefined,
      carrierTypeCode: form.carrierTypeCode.trim() || 'ELECTRONIC',
      sourceSystem: form.sourceSystem.trim() || undefined,
      securityLevelCode: form.securityLevelCode.trim(),
      remark: form.remark.trim() || undefined,
      documentOrganizationCode: form.documentOrganizationCode.trim(),
      retentionPeriodYears: retention,
      custodyStatus: (form.custodyStatus || '').trim() || 'UNARCHIVED',
      submitMode: 'SUBMIT' as const,
      operationRemark: operationRemark.value.trim() || undefined,
      auditAttachments: auditAttachments.value.length ? auditAttachments.value : undefined,
      extValues: extVals
    }
    let createdId: number | null = null
    if (resumeArchiveId.value != null) {
      const updated = await updatePendingDocument(resumeArchiveId.value, payload)
      createdId = updated.archiveId ?? resumeArchiveId.value
      ElMessage.success('提交成功')
    } else {
      const created = await createPendingDocument(payload)
      createdId = created.archiveId
      ElMessage.success('创建成功')
    }
    if (createdId != null && Number.isFinite(Number(createdId)) && Number(createdId) > 0) {
      await router.push({
        path: `/archive-management/detail/${encodeURIComponent(String(createdId))}`,
        query: { from: 'pending', docId: String(createdId) }
      })
    } else {
      await router.push('/archive-management/pending-archive/query')
    }
  } catch (e: unknown) {
    const msg = e instanceof Error ? e.message : '提交失败'
    ElMessage.error(msg)
  } finally {
    submitting.value = false
  }
}

const cancel = () => {
  if (route.query.from === 'workspace-drafts') {
    void router.push('/workspace/my-drafts')
  } else {
    void router.push('/archive-management/pending-archive/query')
  }
}
</script>

<style scoped>
.doc-detail {
  display: grid;
  gap: 16px;
}
.doc-detail__title {
  display: flex;
  flex-direction: column;
  gap: 12px;
  width: 100%;
}
.doc-detail__head-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  width: 100%;
}
.doc-detail__biz-code {
  margin: 0;
  font-size: 30px;
  font-weight: 700;
  color: #0f172a;
  line-height: 1.3;
  word-break: break-all;
  flex: 1 1 auto;
  min-width: 0;
}
.doc-detail__head-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}
.doc-section {
  border: 1px solid #e2e8f0;
  border-radius: 12px;
}
.doc-section__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.doc-section__title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 700;
  color: #1e293b;
}
.doc-info-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 8px 20px;
}
.doc-info-item {
  display: flex;
  border-bottom: 1px solid #f1f5f9;
  padding: 8px 0;
}
.doc-info-item--full {
  grid-column: span 3;
}
.audit-att-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 8px;
}
.doc-info-item__label {
  width: 120px;
  color: #64748b;
  font-size: 14px;
  flex-shrink: 0;
}
.doc-info-item__value {
  color: #1f2937;
  font-size: 14px;
  word-break: break-all;
  min-width: 0;
  flex: 1;
}
.doc-edit-control {
  width: 100%;
}
.doc-edit-control--block {
  width: 100%;
}
.doc-muted-hint {
  color: #64748b;
  font-size: 12px;
  line-height: 1.4;
}
.doc-flow-hint {
  margin-top: 6px;
  font-size: 12px;
  color: #64748b;
  line-height: 1.4;
}
:deep(.doc-edit-control.el-input),
:deep(.doc-edit-control.el-select),
:deep(.doc-edit-control.el-date-editor) {
  width: 100%;
}
@media (max-width: 1200px) {
  .doc-info-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
  .doc-info-item--full {
    grid-column: span 2;
  }
}
@media (max-width: 768px) {
  .doc-detail__head-row {
    flex-wrap: wrap;
  }
  .doc-detail__head-actions {
    margin-left: auto;
  }
  .doc-info-grid {
    grid-template-columns: 1fr;
  }
  .doc-info-item--full {
    grid-column: span 1;
  }
}
</style>
