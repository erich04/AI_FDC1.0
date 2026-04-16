<template>
  <div class="doc-detail">
    <el-alert
      v-if="loadError"
      type="error"
      :closable="false"
      show-icon
      :title="loadError"
      class="doc-detail__load-alert"
    />
    <el-alert
      v-else-if="isDraftLifecycle && !loading"
      type="info"
      :closable="false"
      show-icon
      class="doc-detail__draft-notice"
      title="草稿占位值说明"
      description="列表中展示的子公司、文档类型、档期等可能来自系统自动占位（为满足建档必填），不代表您已确认的真实业务数据。提交保存前请逐项核对。"
    />
    <section class="doc-detail__title">
      <div class="doc-detail__head-row">
        <h1 class="doc-detail__biz-code">{{ headlineBusinessCode }}</h1>
        <div id="pending-archive-submit-actions" class="doc-detail__head-actions">
          <el-button :disabled="saving" @click="saveDraft">保存草稿</el-button>
          <el-button type="primary" :loading="saving" @click="save">提交保存</el-button>
          <el-button :disabled="saving" @click="cancel">取消</el-button>
        </div>
      </div>
      <div v-if="detail" class="doc-detail__title-tags">
        <el-tag effect="plain" type="primary">{{ carrierDisplayTag }}</el-tag>
        <el-tag effect="plain" type="success">{{ detail.archiveStatus || '草稿' }}</el-tag>
        <el-tag effect="plain" type="danger">{{ securityDisplayTag }}</el-tag>
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
      <div v-if="loading"><el-skeleton :rows="10" animated /></div>
      <div v-else class="doc-info-grid">
        <div
          v-for="row in basicRows"
          :key="row.label"
          class="doc-info-item"
          :class="{ 'doc-info-item--full': row.full }"
        >
          <div class="doc-info-item__label">{{ row.label }}</div>
          <div class="doc-info-item__value">
            <template v-if="row.readonly">{{ row.text || '-' }}</template>
            <el-input
              v-else-if="row.edit === 'businessCode'"
              v-model="form.businessCode"
              clearable
              placeholder="未填可留空，保存草稿后可在列表中继续补充"
              class="doc-edit-control"
            />
            <el-date-picker
              v-else-if="row.edit === 'endPeriod'"
              v-model="form.endPeriod"
              type="month"
              value-format="YYYY-MM"
              placeholder="可选，不填与开始档期一致"
              class="doc-edit-control"
            />
            <el-select
              v-else-if="row.edit === 'archiveDestination'"
              v-model="form.archiveDestination"
              filterable
              clearable
              placeholder="请选择归档地"
              class="doc-edit-control"
            >
              <el-option v-for="o in options.archiveDestinations" :key="o.code" :label="o.name" :value="o.code" />
            </el-select>
            <el-input v-else-if="row.edit === 'documentName'" v-model="form.documentName" clearable class="doc-edit-control" />
            <el-date-picker
              v-else-if="row.edit === 'documentDate'"
              v-model="form.documentDate"
              type="datetime"
              value-format="YYYY-MM-DD HH:mm:ss"
              placeholder="选择日期时间"
              class="doc-edit-control"
            />
            <el-input v-else-if="row.edit === 'dutyPerson'" v-model="form.dutyPerson" clearable class="doc-edit-control" />
            <el-select
              v-else-if="row.edit === 'carrierType'"
              v-model="form.carrierTypeCode"
              filterable
              clearable
              placeholder="请选择载体类型"
              class="doc-edit-control"
            >
              <el-option v-for="o in options.carrierTypes" :key="o.code" :label="o.name" :value="o.code" />
            </el-select>
            <el-input v-else-if="row.edit === 'sourceSystem'" v-model="form.sourceSystem" clearable class="doc-edit-control" />
            <el-select
              v-else-if="row.edit === 'securityLevel'"
              v-model="form.securityLevelCode"
              filterable
              clearable
              placeholder="请选择密级"
              class="doc-edit-control"
            >
              <el-option v-for="o in options.securityLevels" :key="o.code" :label="o.name" :value="o.code" />
            </el-select>
            <el-input
              v-else-if="row.edit === 'remark'"
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
        <div v-for="row in extEditRows" :key="row.key" class="doc-info-item">
          <div class="doc-info-item__label">{{ row.label }}</div>
          <div class="doc-info-item__value">
            <template v-if="row.readonly">{{ row.display }}</template>
            <el-input v-else v-model="extForm[row.key]" clearable placeholder="请输入" class="doc-edit-control" />
          </div>
        </div>
        <el-empty v-if="!extEditRows.length" description="暂无扩展信息" />
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
        <div v-for="item in archiveInfoItems" :key="item.label" class="doc-info-item">
          <div class="doc-info-item__label">{{ item.label }}</div>
          <div class="doc-info-item__value">
            {{ item.value || '-' }}
            <div v-if="item.label === '文档组织'" class="doc-flow-hint">随归档地变更按归档流向规则重新匹配</div>
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
import { computed, nextTick, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { fetchCompanyProjectCountries, fetchCompanyProjectDetail } from '../../api/modules/companyProject'
import {
  fetchArchiveCreateOptions,
  fetchEffectiveDocumentTypeExtFields,
  getArchiveDetail,
  resolveArchiveDefaults,
  updatePendingDocument,
  uploadPendingAuditAttachment
} from '../../api/modules/archiveManagement'
import type { ArchiveCreateOptions, ArchiveRecordSummary, CompanyProjectDetail } from '../../types'
import { getCountryLabel } from '../base-data/companyProjectShared'
import { EXT_DETAIL_FIELD_ORDER, hardCodedExtLabelMap, isHardCodedFieldVisible } from './extFieldDisplayConfig'
import { CURRENT_OPERATOR_USER_ID } from '../../constants/currentUser'

type BasicEditKey =
  | 'businessCode'
  | 'endPeriod'
  | 'archiveDestination'
  | 'documentName'
  | 'documentDate'
  | 'dutyPerson'
  | 'carrierType'
  | 'sourceSystem'
  | 'securityLevel'
  | 'remark'

interface BasicRowReadonly {
  label: string
  readonly: true
  text: string
  full?: boolean
}

interface BasicRowEdit {
  label: string
  edit: BasicEditKey
  full?: boolean
}

type BasicRow = BasicRowReadonly | BasicRowEdit

const route = useRoute()
const router = useRouter()

const loading = ref(true)
const saving = ref(false)
const dirty = ref(false)
const operationRemark = ref('')
const auditAttachments = ref<Array<{ fileId: number; fileName?: string; storageKey?: string; fileSize?: number }>>([])
const loadError = ref('')
const detail = ref<ArchiveRecordSummary | null>(null)
/** 保存用：随归档地与归档流向规则解析，不由库内旧值手工决定 */
const flowDocumentOrganizationCode = ref('')
const extFieldNameMap = ref<Record<string, string>>({})
const countryNameByCode = ref<Record<string, string>>({})

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
  businessCode: '',
  endPeriod: '',
  archiveDestination: '',
  documentName: '',
  documentDate: '',
  dutyPerson: '',
  carrierTypeCode: '',
  sourceSystem: '',
  securityLevelCode: '',
  remark: ''
})

/** 扩展字段表单副本（国家/代表处/地区部/公司标签只读，其余可改） */
const extForm = reactive<Record<string, string>>({})

const READONLY_EXT_KEYS = new Set(['country', 'repOffice', 'region', 'companyTag'])

const sectionOpen = reactive({
  ext: true,
  archive: true
})

const docIdParam = computed(() => {
  const raw = route.params.docId
  return decodeURIComponent(String(Array.isArray(raw) ? raw[0] : raw || '').trim())
})

const archiveNumericId = computed((): number | null => {
  const s = docIdParam.value
  if (!/^\d+$/.test(s)) return null
  const n = Number(s)
  return Number.isSafeInteger(n) && n > 0 ? n : null
})

const isDraftLifecycle = computed(() => String(detail.value?.lifecycleStatus || '').toUpperCase() === 'DRAFT')

const headlineBusinessCode = computed(() => {
  const bc = (detail.value?.businessCode || '').trim()
  if (bc) return bc
  if (isDraftLifecycle.value) return '未填写文档业务编码'
  return docIdParam.value || '-'
})

const carrierDisplayTag = computed(() => {
  const o = options.carrierTypes.find((c) => c.code === form.carrierTypeCode)
  if (o) return o.name
  if (!detail.value) return '载体类型'
  return detail.value.carrierTypeCode
})

const securityDisplayTag = computed(() => {
  const d = detail.value
  if (d?.securityLevelName) return d.securityLevelName
  const o = options.securityLevels.find((c) => c.code === form.securityLevelCode)
  if (o) return o.name
  return d?.securityLevelCode || '内部'
})

const selectedDocTypeName = computed(() => detail.value?.documentTypeName || '')

const formatDateTime = (value: unknown) => {
  if (value === null || value === undefined || value === '') return '-'
  const text = String(value).trim()
  if (!text) return '-'
  const normalized = text.includes('T') ? text : text.replace(' ', 'T')
  const date = new Date(normalized)
  if (Number.isNaN(date.getTime())) return text
  const pad = (n: number) => String(n).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`
}

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

const matchOptionCode = (opts: { code: string; name: string }[], raw: string | undefined) => {
  if (!raw) return ''
  const u = String(raw).trim()
  if (!u) return ''
  if (opts.some((o) => o.code === u)) return u
  const byName = opts.find((o) => o.name === u)
  return byName?.code ?? u
}

const syncDocumentOrgFromFlow = async () => {
  if (!detail.value) {
    flowDocumentOrganizationCode.value = ''
    return
  }
  const d = detail.value
  const company = (d.companyProjectCode || '').trim()
  const docType = (d.documentTypeCode || '').trim()
  if (!company || !docType) {
    flowDocumentOrganizationCode.value = (d.documentOrganizationCode || '').trim()
    return
  }
  try {
    const res = await resolveArchiveDefaults({
      companyProjectCode: company,
      documentTypeCode: docType,
      customRule: (d.businessModuleTypeCode || '').trim() || undefined,
      archiveDestination: form.archiveDestination.trim() || undefined
    })
    const code = (res?.documentOrganizationCode || '').trim()
    if (code) {
      flowDocumentOrganizationCode.value = matchOptionCode(options.documentOrganizations, code) || code
    } else {
      flowDocumentOrganizationCode.value = ''
    }
  } catch {
    flowDocumentOrganizationCode.value = (d.documentOrganizationCode || '').trim()
  }
}

const applyDetailToForm = (d: ArchiveRecordSummary) => {
  form.businessCode = (d.businessCode || '').trim()
  form.endPeriod = d.endPeriod || ''
  form.archiveDestination = matchOptionCode(options.archiveDestinations, d.archiveDestination)
  form.documentName = d.documentName || ''
  form.documentDate = toFormDateTime(d.documentDate)
  form.dutyPerson = d.dutyPerson || ''
  form.carrierTypeCode = matchOptionCode(options.carrierTypes, d.carrierTypeCode)
  form.sourceSystem = d.sourceSystem || ''
  form.securityLevelCode = matchOptionCode(options.securityLevels, d.securityLevelCode || d.securityLevelName)
  form.remark = d.remark || ''
}

const initExtForm = (d: ArchiveRecordSummary) => {
  Object.keys(extForm).forEach((k) => delete extForm[k])
  const ext = d.extValues || {}
  const docType = d.documentTypeName || ''
  for (const [k, v] of Object.entries(ext)) {
    if (k === 'visibility') continue
    extForm[k] = v != null ? String(v) : ''
  }
  extForm.visibility = String(d.documentVisibility ?? ext.visibility ?? '是').trim() || '是'
  for (const key of EXT_DETAIL_FIELD_ORDER) {
    if (!isHardCodedFieldVisible(key, docType)) continue
    if (!(key in extForm)) extForm[key] = ''
  }
}

const applyCompanyDetailToExt = (company: CompanyProjectDetail) => {
  const countryCode = company.countryCode?.trim() || ''
  extForm.country = countryCode
  const rep = options.geoRepOffices.find((o) => o.code === countryCode)
  const reg = options.geoRegions.find((o) => o.code === countryCode)
  extForm.repOffice = rep?.name || ''
  extForm.region = reg?.name || company.managementArea?.trim() || ''
  extForm.companyTag = company.companyTag?.trim() || ''
}

const syncCompanyReadonlyExtFields = async (companyProjectCode: string) => {
  const code = companyProjectCode.trim()
  if (!code) {
    for (const key of READONLY_EXT_KEYS) {
      extForm[key] = ''
    }
    return
  }
  try {
    const company = await fetchCompanyProjectDetail(code)
    applyCompanyDetailToExt(company)
  } catch {
    /* ignore */
  }
}

const ensureExtFormKeysFromConfig = () => {
  const docType = selectedDocTypeName.value
  for (const key of Object.keys(extFieldNameMap.value)) {
    if (!isHardCodedFieldVisible(key, docType)) continue
    if (!(key in extForm)) extForm[key] = ''
  }
}

const shouldShowExtKey = (key: string) => {
  if (key === 'visibility') return false
  const docType = selectedDocTypeName.value
  const fromApi = Boolean(detail.value?.extValues && key in (detail.value.extValues as object))
  return isHardCodedFieldVisible(key, docType) || fromApi
}

const basicRows = computed((): BasicRow[] => {
  if (!detail.value) return []
  const d = detail.value
  const lifecycle = String(d.lifecycleStatus || d.archiveStatus || '').trim()
  const draft = String(d.lifecycleStatus || '').toUpperCase() === 'DRAFT'
  return [
    { label: '文档类型', readonly: true, text: d.documentTypeName || d.documentTypeCode || '-' },
    draft
      ? { label: '文档业务编码', edit: 'businessCode' as const }
      : { label: '文档业务编码', readonly: true, text: d.businessCode || '-' },
    { label: '公司', readonly: true, text: d.companyProjectName || d.companyProjectCode || '-' },
    { label: '业务模块', readonly: true, text: d.archiveTypeCode || '-' },
    { label: '开始档期', readonly: true, text: d.beginPeriod || '-' },
    { label: '结束档期', edit: 'endPeriod' },
    { label: '归档地', edit: 'archiveDestination' },
    { label: '产生地', readonly: true, text: d.originPlace || '-' },
    { label: '文档名称', edit: 'documentName', full: true },
    { label: '文档生成日期', edit: 'documentDate' },
    { label: '归档责任人', edit: 'dutyPerson' },
    { label: '文档责任部门', readonly: true, text: d.dutyDepartment || '-' },
    { label: '载体类型', edit: 'carrierType' },
    { label: '系统来源', edit: 'sourceSystem' },
    { label: '密级', edit: 'securityLevel' },
    { label: '文档生命周期状态', readonly: true, text: lifecycle || '-' },
    { label: '创建时间', readonly: true, text: formatDateTime(d.lastUpdateDate) },
    { label: '创建人', readonly: true, text: d.dutyPerson || '-' },
    { label: '描述', edit: 'remark', full: true }
  ]
})

const formatCountryExtValue = (raw: unknown) => {
  if (raw === null || raw === undefined) return '-'
  let s = String(raw).trim()
  if (!s) return '-'
  if (s.startsWith('[')) {
    try {
      const parsed = JSON.parse(s) as unknown
      if (Array.isArray(parsed)) {
        s = parsed.map((x) => String(x).trim()).filter(Boolean).join(',')
      } else if (parsed !== null && typeof parsed === 'object' && 'countryCode' in (parsed as object)) {
        s = String((parsed as { countryCode?: string }).countryCode ?? '').trim()
      }
    } catch {
      /* ignore */
    }
  }
  const map = countryNameByCode.value
  const resolveOne = (code: string) => {
    const c = code.trim()
    if (!c) return ''
    return map[c] || getCountryLabel(c)
  }
  const parts = s.split(/[,，]/).map((p) => p.trim()).filter(Boolean)
  if (!parts.length) return '-'
  const labels = parts.map(resolveOne).filter(Boolean)
  return labels.length ? labels.join('、') : '-'
}

const formatExtCell = (v: unknown) => {
  if (v === null || v === undefined) return '-'
  const s = String(v).trim()
  return s === '' ? '-' : s
}

const extEditRows = computed(() => {
  if (!detail.value) return []
  const rows: { key: string; label: string; readonly: boolean; display: string }[] = []
  const used = new Set<string>()

  const pushRow = (key: string) => {
    if (used.has(key)) return
    if (!shouldShowExtKey(key)) return
    used.add(key)
    const label = extFieldNameMap.value[key] || hardCodedExtLabelMap[key] || key
    const readonly = READONLY_EXT_KEYS.has(key)
    const raw = extForm[key]
    const display = readonly ? (key === 'country' ? formatCountryExtValue(raw) : formatExtCell(raw)) : ''
    rows.push({ key, label, readonly, display })
  }

  for (const key of EXT_DETAIL_FIELD_ORDER) {
    pushRow(key)
  }
  for (const key of Object.keys(extForm).sort()) {
    pushRow(key)
  }
  return rows
})

const documentOrgFlowDisplay = computed(() => {
  const code = flowDocumentOrganizationCode.value.trim()
  if (!code) return '-'
  const o = options.documentOrganizations.find((x) => x.code === code)
  return o ? `${o.name}（${code}）` : code
})

const archiveInfoItems = computed(() => {
  if (!detail.value) return []
  const d = detail.value
  const cell = (v: unknown) => {
    if (v === null || v === undefined) return '-'
    const s = String(v).trim()
    return s === '' ? '-' : s
  }
  const vis = String(extForm.visibility ?? d.documentVisibility ?? d.extValues?.visibility ?? '是').trim() || '是'
  const barcode = extForm.barcodeModule ?? d.extValues?.barcodeModule
  return [
    { label: '文档组织', value: documentOrgFlowDisplay.value },
    { label: '是否可见', value: cell(vis) },
    { label: '条码模块', value: cell(barcode) },
    { label: '保管状态', value: cell(d.custodyStatus || d.archiveStatus) }
  ]
})

const toggleSection = (key: 'ext' | 'archive') => {
  sectionOpen[key] = !sectionOpen[key]
}

const load = async () => {
  loading.value = true
  loadError.value = ''
  operationRemark.value = ''
  auditAttachments.value = []
  flowDocumentOrganizationCode.value = ''
  const id = archiveNumericId.value
  if (id == null) {
    loadError.value = '无效的文档 ID'
    loading.value = false
    detail.value = null
    return
  }
  try {
    const [opts, record, countries] = await Promise.all([
      fetchArchiveCreateOptions(),
      getArchiveDetail(id),
      fetchCompanyProjectCountries().catch(() => [])
    ])
    Object.assign(options, opts)
    detail.value = record
    countryNameByCode.value = Object.fromEntries((countries || []).map((c) => [c.countryCode, c.countryName]))
    applyDetailToForm(record)
    initExtForm(record)
    await syncCompanyReadonlyExtFields(record.companyProjectCode || '')
    if (record.documentTypeCode) {
      const fields = await fetchEffectiveDocumentTypeExtFields(record.documentTypeCode).catch(() => [])
      extFieldNameMap.value = Object.fromEntries((fields || []).map((item) => [item.fieldCode, item.fieldName || item.fieldCode]))
      ensureExtFormKeysFromConfig()
    }
    await syncDocumentOrgFromFlow()
  } catch (e: any) {
    const msg = e?.message || '加载失败'
    loadError.value = msg
    ElMessage.error(msg)
    detail.value = null
  } finally {
    loading.value = false
    dirty.value = false
    void nextTick(() => {
      if (route.query.focus === 'submit' && !loadError.value) {
        document.getElementById('pending-archive-submit-actions')?.scrollIntoView({ behavior: 'smooth', block: 'center' })
      }
    })
  }
}

watch(
  () => ({ ...form, ...extForm }),
  () => {
    if (!loading.value && detail.value) {
      dirty.value = true
    }
  },
  { deep: true }
)

const collectExtValuesForSave = (): Record<string, string> => {
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

const saveDraft = async (silent = false) => {
  if (!detail.value || archiveNumericId.value == null) return
  const d = detail.value
  saving.value = true
  try {
    const updated = await updatePendingDocument(archiveNumericId.value, {
      operatorUserId: CURRENT_OPERATOR_USER_ID,
      documentTypeCode: d.documentTypeCode || '',
      companyProjectCode: d.companyProjectCode || '',
      archiveTypeCode: d.businessModuleTypeCode || '',
      businessCode: form.businessCode.trim(),
      beginPeriod: d.beginPeriod || '',
      endPeriod: form.endPeriod.trim() || undefined,
      archiveDestination: form.archiveDestination.trim() || undefined,
      originPlace: d.originPlace || undefined,
      documentName: form.documentName.trim(),
      documentDate: form.documentDate || '',
      dutyPerson: form.dutyPerson.trim() || d.dutyPerson,
      dutyDepartment: d.dutyDepartment,
      carrierTypeCode: form.carrierTypeCode.trim() || 'ELECTRONIC',
      sourceSystem: form.sourceSystem.trim() || undefined,
      securityLevelCode: form.securityLevelCode.trim() || 'INTERNAL',
      remark: form.remark.trim() || undefined,
      documentOrganizationCode: flowDocumentOrganizationCode.value.trim() || 'DEFAULT',
      retentionPeriodYears: d.retentionPeriodYears,
      submitMode: 'DRAFT',
      operationRemark: operationRemark.value.trim() || undefined,
      auditAttachments: auditAttachments.value.length ? auditAttachments.value : undefined,
      extValues: collectExtValuesForSave()
    })
    dirty.value = false
    if (!silent) ElMessage.success('草稿已保存')
    await load()
  } catch (e: unknown) {
    const msg = e instanceof Error ? e.message : '保存失败'
    ElMessage.error(msg)
  } finally {
    saving.value = false
  }
}

const save = async () => {
  if (!detail.value || archiveNumericId.value == null) return
  const d = detail.value
  if (!(d.documentTypeCode || '').trim()) {
    ElMessage.warning('文档类型缺失')
    return
  }
  if (!form.businessCode.trim()) {
    ElMessage.warning('请填写文档业务编码')
    return
  }
  if (!(d.businessModuleTypeCode || '').trim()) {
    ElMessage.warning('业务模块缺失')
    return
  }
  if (!(d.beginPeriod || '').trim()) {
    ElMessage.warning('开始档期缺失')
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
  saving.value = true
  try {
    const updated = await updatePendingDocument(archiveNumericId.value, {
      operatorUserId: CURRENT_OPERATOR_USER_ID,
      documentTypeCode: d.documentTypeCode || '',
      companyProjectCode: d.companyProjectCode || '',
      archiveTypeCode: d.businessModuleTypeCode || '',
      businessCode: form.businessCode.trim(),
      beginPeriod: d.beginPeriod || '',
      endPeriod: form.endPeriod.trim() || undefined,
      archiveDestination: form.archiveDestination.trim() || undefined,
      originPlace: d.originPlace || undefined,
      documentName: form.documentName.trim(),
      documentDate: form.documentDate,
      dutyPerson: form.dutyPerson.trim() || d.dutyPerson,
      dutyDepartment: d.dutyDepartment,
      carrierTypeCode: form.carrierTypeCode.trim() || 'ELECTRONIC',
      sourceSystem: form.sourceSystem.trim() || undefined,
      securityLevelCode: form.securityLevelCode.trim(),
      remark: form.remark.trim() || undefined,
      documentOrganizationCode: flowDocumentOrganizationCode.value.trim(),
      retentionPeriodYears: d.retentionPeriodYears,
      submitMode: 'SUBMIT',
      operationRemark: operationRemark.value.trim() || undefined,
      auditAttachments: auditAttachments.value.length ? auditAttachments.value : undefined,
      extValues: collectExtValuesForSave()
    })
    dirty.value = false
    ElMessage.success('保存成功')
    const updatedId = updated.archiveId ?? archiveNumericId.value
    if (updatedId != null && Number.isFinite(Number(updatedId)) && Number(updatedId) > 0) {
      await router.push({
        path: `/archive-management/detail/${encodeURIComponent(String(updatedId))}`,
        query: { from: 'pending', docId: String(updatedId) }
      })
    } else {
      await load()
    }
  } catch (e: unknown) {
    const msg = e instanceof Error ? e.message : '保存失败'
    ElMessage.error(msg)
  } finally {
    saving.value = false
  }
}

const cancel = () => {
  router.push('/archive-management/pending-archive/query')
}

watch(
  () => docIdParam.value,
  () => {
    void load()
  },
  { immediate: true }
)

watch(
  () => form.archiveDestination,
  () => {
    if (detail.value) void syncDocumentOrgFromFlow()
  }
)
</script>

<style scoped>
.doc-detail {
  display: grid;
  gap: 16px;
}
.doc-detail__load-alert {
  margin-bottom: 0;
}
.doc-detail__draft-notice {
  margin-bottom: 0;
}
.doc-detail__draft-notice :deep(.el-alert__description) {
  margin-top: 6px;
  line-height: 1.55;
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
.doc-detail__title-tags {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
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
/* 与文档详情一致：值区占满栅格列，不设 max-width */
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
.audit-att-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 8px;
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
