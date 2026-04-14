<template>
  <div class="transfer-query-page">
    <el-card shadow="never">
      <el-form :model="searchForm" label-width="108px" class="filter-form" @submit.prevent>
        <div class="filter-grid">
          <el-form-item label="文档业务编码">
            <el-input v-model="searchForm.docBusiNo" class="input-w180" placeholder="模糊查询" clearable />
          </el-form-item>
          <el-form-item label="公司">
            <el-select v-model="searchForm.companyProjectCode" class="input-w180" clearable filterable placeholder="请选择">
              <el-option v-for="o in companyOptions" :key="o.code" :label="o.name" :value="o.code" />
            </el-select>
          </el-form-item>
          <el-form-item label="业务模块">
            <el-select v-model="searchForm.busiModuleCode" class="input-w180" clearable filterable placeholder="请选择">
              <el-option v-for="o in busiModuleOptions" :key="o.code" :label="o.name" :value="o.code" />
            </el-select>
          </el-form-item>
          <el-form-item label="档期" class="span-range">
            <el-date-picker
              v-model="searchForm.archPeriodRange"
              type="daterange"
              value-format="YYYY-MM-DD"
              range-separator="至"
              start-placeholder="开始"
              end-placeholder="结束"
              unlink-panels
              class="input-range"
            />
          </el-form-item>
          <el-form-item label="申请人">
            <el-select v-model="searchForm.applicant" class="input-w180" clearable filterable placeholder="用户 ID">
              <el-option v-for="u in userOptions" :key="u.id" :label="u.name" :value="u.id" />
            </el-select>
          </el-form-item>

          <el-form-item label="申请单号">
            <el-input v-model="searchForm.applicationNumber" class="input-w180" placeholder="模糊查询" clearable />
          </el-form-item>
          <el-form-item label="申请日期" class="span-range">
            <el-date-picker
              v-model="searchForm.applicationDateRange"
              type="daterange"
              value-format="YYYY-MM-DD"
              range-separator="至"
              start-placeholder="开始"
              end-placeholder="结束"
              unlink-panels
              class="input-range"
            />
          </el-form-item>
          <el-form-item label="申请状态">
            <el-select v-model="searchForm.applicationStatus" class="input-w180" clearable placeholder="请选择">
              <el-option v-for="o in statusOptions" :key="o.code" :label="o.name" :value="o.code" />
            </el-select>
          </el-form-item>
          <el-form-item label="载体类型">
            <el-select v-model="searchForm.carrierType" class="input-w180" clearable placeholder="请选择">
              <el-option v-for="o in carrierTypeOptions" :key="o.code" :label="o.name" :value="o.code" />
            </el-select>
          </el-form-item>
          <el-form-item label="差异原因">
            <el-select v-model="searchForm.diffReasonCode" class="input-w180" clearable placeholder="请选择">
              <el-option v-for="o in diffReasonOptions" :key="o.code" :label="o.name" :value="o.code" />
            </el-select>
          </el-form-item>
        </div>

        <div v-show="filterExpanded" class="filter-grid filter-grid--extra">
          <el-form-item label="移交方式">
            <el-select v-model="searchForm.applyMethod" class="input-w180" clearable placeholder="请选择">
              <el-option v-for="o in applyMethodOptions" :key="o.code" :label="o.name" :value="o.code" />
            </el-select>
          </el-form-item>
          <el-form-item label="邮寄方式">
            <el-select v-model="searchForm.expressType" class="input-w180" clearable placeholder="请选择">
              <el-option v-for="o in expressTypeOptions" :key="o.code" :label="o.name" :value="o.code" />
            </el-select>
          </el-form-item>
          <el-form-item label="邮寄单号">
            <el-input v-model="searchForm.expressNumber" class="input-w180" placeholder="模糊查询" clearable />
          </el-form-item>
          <el-form-item label="接收人">
            <el-select v-model="searchForm.documentRecipient" class="input-w180" clearable filterable placeholder="用户 ID">
              <el-option v-for="u in userOptions" :key="u.id" :label="u.name" :value="u.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="册号">
            <el-input v-model="searchForm.catalogVolumeNo" class="input-w180" placeholder="模糊查询" clearable />
          </el-form-item>
        </div>

        <div class="filter-toolbar">
          <el-button link type="primary" @click="filterExpanded = !filterExpanded">
            {{ filterExpanded ? '收起更多条件' : '展开更多条件' }}
          </el-button>
          <div class="filter-toolbar__actions">
            <el-button @click="reset">重置</el-button>
            <el-button type="primary" :loading="loading" @click="runQuery">查询</el-button>
          </div>
        </div>
      </el-form>

      <div class="table-section">
        <div class="table-toolbar">
          <div class="table-toolbar__left">
            <el-button type="primary" @click="goToCreate">发起移交</el-button>
            <el-button @click="hintTodo('导出')">导出</el-button>
          </div>
        </div>

        <div v-if="loading" class="loading-container">
          <el-skeleton :rows="8" animated />
        </div>
        <div v-else class="transfer-list">
          <el-table :data="tableRecords" border class="transfer-table">
            <el-table-column label="序号" width="64" align="center">
              <template #default="{ $index }">
                {{ (pagination.page - 1) * pagination.pageSize + $index + 1 }}
              </template>
            </el-table-column>
            <el-table-column prop="applicationNumber" label="申请单号" min-width="160" show-overflow-tooltip />
            <el-table-column prop="busiModuleName" label="业务模块" min-width="140" show-overflow-tooltip />
            <el-table-column prop="applicantName" label="申请人" width="120" show-overflow-tooltip />
            <el-table-column label="申请日期" width="170">
              <template #default="{ row }">{{ formatDateTime(row.applicationDate) }}</template>
            </el-table-column>
            <el-table-column label="申请状态" width="110">
              <template #default="{ row }">
                <el-tag v-if="row.applicationStatus" size="small" :type="statusTagType(row.applicationStatus)" effect="light">
                  {{ labelOf(statusOptions, row.applicationStatus) }}
                </el-tag>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="documentRecipientName" label="接收人" width="120" show-overflow-tooltip />
            <el-table-column label="邮寄方式" width="120">
              <template #default="{ row }">{{ labelOf(expressTypeOptions, row.expressType) }}</template>
            </el-table-column>
            <el-table-column prop="expressNumber" label="邮寄单号" min-width="140" show-overflow-tooltip />
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" @click="openView(row.applicationId)">查看</el-button>
                <el-button
                  v-if="row.applicationStatus === 'DRAFT' || row.applicationStatus === 'REJECTED'"
                  link
                  type="primary"
                  @click="goEdit(row.applicationId)"
                >
                  编辑
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination">
            <el-pagination
              v-model:current-page="pagination.page"
              v-model:page-size="pagination.pageSize"
              :page-sizes="[20, 50, 100, 200]"
              layout="total, sizes, prev, pager, next, jumper"
              :total="pagination.total"
              @size-change="handleSizeChange"
              @current-change="handlePageChange"
            />
          </div>
        </div>
      </div>

      <el-dialog v-model="detailVisible" title="移交申请详情" width="920px" destroy-on-close>
        <div v-if="detailLoading" class="loading-container"><el-skeleton :rows="6" animated /></div>
        <div v-else-if="detailData" class="transfer-detail">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="申请单号">{{ detailData.applicationNumber }}</el-descriptions-item>
            <el-descriptions-item label="申请状态">{{ labelOf(statusOptions, detailData.applicationStatus) }}</el-descriptions-item>
            <el-descriptions-item label="申请人">{{ formatUser(detailData.applicant) }}</el-descriptions-item>
            <el-descriptions-item label="申请日期">{{ formatDateTime(detailData.applicationDate) }}</el-descriptions-item>
            <el-descriptions-item label="业务模块">{{ detailData.busiModuleCode || '-' }}</el-descriptions-item>
            <el-descriptions-item label="移交方式">{{ labelOf(applyMethodOptions, detailData.applyMethod) }}</el-descriptions-item>
            <el-descriptions-item label="载体类型">{{ labelOf(carrierTypeOptions, detailData.carrierType) }}</el-descriptions-item>
            <el-descriptions-item label="邮寄方式">{{ labelOf(expressTypeOptions, detailData.expressType) }}</el-descriptions-item>
            <el-descriptions-item label="邮寄单号">{{ detailData.expressNumber || '-' }}</el-descriptions-item>
            <el-descriptions-item label="接收人">{{ formatUser(detailData.documentRecipient) }}</el-descriptions-item>
            <el-descriptions-item label="差异原因">{{ labelOf(diffReasonOptions, detailData.diffReasonCode) }}</el-descriptions-item>
            <el-descriptions-item label="说明" :span="2">{{ detailData.applicationDescription || '-' }}</el-descriptions-item>
          </el-descriptions>

          <div class="documents-section">
            <h3>申请明细</h3>
            <el-table :data="detailData.details || []" border size="small">
              <el-table-column type="index" label="#" width="50" />
              <el-table-column prop="docBusiNo" label="文档业务编码" min-width="120" show-overflow-tooltip />
              <el-table-column prop="docName" label="文档名称" min-width="160" show-overflow-tooltip />
              <el-table-column label="公司/项目" min-width="120" show-overflow-tooltip>
                <template #default="{ row }">{{ labelOf(companyOptions, row.companyProjectCode) }}</template>
              </el-table-column>
              <el-table-column label="业务模块" width="120">
                <template #default="{ row }">{{ labelOf(busiModuleOptions, row.busiModuleCode) }}</template>
              </el-table-column>
              <el-table-column label="档期起" width="120">
                <template #default="{ row }">{{ row.startArchPeriod || '-' }}</template>
              </el-table-column>
              <el-table-column label="档期止" width="120">
                <template #default="{ row }">{{ row.endArchPeriod || '-' }}</template>
              </el-table-column>
              <el-table-column prop="catalogVolumeNo" label="册号" width="100" show-overflow-tooltip />
            </el-table>
          </div>
        </div>
        <template #footer>
          <el-button @click="detailVisible = false">关闭</el-button>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import axios from 'axios'
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { fetchArchiveCreateOptions } from '../../api/modules/archiveManagement'
import { fetchDictionaryItems } from '../../api/modules/dictionary'
import {
  getTransferApplication,
  searchTransferApplicationRecords,
  type TransferApplicationDetailPayload,
  type TransferApplicationRecordQuery,
  type TransferApplicationRecordRow
} from '../../api/modules/transferApplications'
import type { ArchiveCreateOptions, DictionaryItem } from '../../types'

interface LabelOption {
  code: string
  name: string
}

const DEFAULT_TENANT_ID = 1

const router = useRouter()
const loading = ref(false)
const filterExpanded = ref(false)
const archiveOptions = ref<ArchiveCreateOptions | null>(null)

const tableRecords = ref<TransferApplicationRecordRow[]>([])
const pagination = reactive({
  page: 1,
  pageSize: 20,
  total: 0
})

const searchForm = reactive<TransferApplicationRecordQuery>({
  docBusiNo: '',
  companyProjectCode: '',
  busiModuleCode: '',
  archPeriodRange: undefined,
  applicant: undefined,
  applicationNumber: '',
  applicationDateRange: undefined,
  applicationStatus: '',
  carrierType: '',
  diffReasonCode: '',
  applyMethod: '',
  expressType: '',
  expressNumber: '',
  documentRecipient: undefined,
  catalogVolumeNo: ''
})

const userOptions = ref([
  { id: 1, name: '张三' },
  { id: 2, name: '李四' },
  { id: 3, name: '王五' }
])

const companyOptions = ref<LabelOption[]>([])
const carrierTypeOptions = ref<LabelOption[]>([])
const busiModuleOptions = ref<LabelOption[]>([])
const statusOptions = ref<LabelOption[]>([])
const diffReasonOptions = ref<LabelOption[]>([])
const applyMethodOptions = ref<LabelOption[]>([])
const expressTypeOptions = ref<LabelOption[]>([])

const detailVisible = ref(false)
const detailLoading = ref(false)
const detailData = ref<TransferApplicationDetailPayload | null>(null)

function toLabelOptions(items: DictionaryItem[]): LabelOption[] {
  return items
    .filter((i) => i.enabledFlag === 'Y')
    .map((i) => ({ code: i.itemCode, name: i.itemName }))
}

async function tryDict(categoryCode: string): Promise<LabelOption[]> {
  try {
    const items = await fetchDictionaryItems(categoryCode)
    return toLabelOptions(items)
  } catch {
    return []
  }
}

function mergeOptions(primary: LabelOption[], fallback: LabelOption[]): LabelOption[] {
  if (primary.length) return primary
  return fallback
}

const FALLBACK_STATUS: LabelOption[] = [
  { code: 'DRAFT', name: '草稿' },
  { code: 'SUBMITTED', name: '已提交' },
  { code: 'RUNNING', name: '处理中' },
  { code: 'APPROVED', name: '已通过' },
  { code: 'REJECTED', name: '已驳回' }
]

const FALLBACK_APPLY: LabelOption[] = [
  { code: 'DIRECT', name: '直接移交' },
  { code: 'MAIL', name: '邮寄' }
]

const FALLBACK_EXPRESS: LabelOption[] = [
  { code: 'SF', name: '顺丰' },
  { code: 'EMS', name: 'EMS' },
  { code: 'OTHER', name: '其他' }
]

const FALLBACK_DIFF: LabelOption[] = [
  { code: 'NONE', name: '无差异' },
  { code: 'QTY', name: '数量差异' },
  { code: 'CONTENT', name: '内容差异' }
]

function labelOf(options: LabelOption[], code?: string | null) {
  if (!code) return '-'
  return options.find((o) => o.code === code)?.name ?? code
}

function formatUser(id?: number | null) {
  if (id == null) return '-'
  return userOptions.value.find((u) => u.id === id)?.name ?? `用户-${id}`
}

function formatDateTime(value?: string | null) {
  if (!value) return '-'
  const d = new Date(value)
  if (Number.isNaN(d.getTime())) return value
  const p = (n: number) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())} ${p(d.getHours())}:${p(d.getMinutes())}:${p(d.getSeconds())}`
}

function statusTagType(status: string) {
  switch (status) {
    case 'RUNNING':
      return 'warning'
    case 'APPROVED':
      return 'success'
    case 'REJECTED':
      return 'danger'
    case 'DRAFT':
      return 'info'
    default:
      return 'info'
  }
}

function buildFilterPayload(): TransferApplicationRecordQuery {
  const f: TransferApplicationRecordQuery = {}
  if (searchForm.docBusiNo) f.docBusiNo = searchForm.docBusiNo.trim()
  if (searchForm.companyProjectCode) f.companyProjectCode = searchForm.companyProjectCode
  if (searchForm.busiModuleCode) f.busiModuleCode = searchForm.busiModuleCode
  if (searchForm.archPeriodRange?.length === 2) f.archPeriodRange = [...searchForm.archPeriodRange]
  if (searchForm.applicant != null) f.applicant = searchForm.applicant
  if (searchForm.applicationNumber) f.applicationNumber = searchForm.applicationNumber.trim()
  if (searchForm.applicationDateRange?.length === 2) f.applicationDateRange = [...searchForm.applicationDateRange]
  if (searchForm.applicationStatus) f.applicationStatus = searchForm.applicationStatus
  if (searchForm.carrierType) f.carrierType = searchForm.carrierType
  if (searchForm.diffReasonCode) f.diffReasonCode = searchForm.diffReasonCode
  if (searchForm.applyMethod) f.applyMethod = searchForm.applyMethod
  if (searchForm.expressType) f.expressType = searchForm.expressType
  if (searchForm.expressNumber) f.expressNumber = searchForm.expressNumber.trim()
  if (searchForm.documentRecipient != null) f.documentRecipient = searchForm.documentRecipient
  if (searchForm.catalogVolumeNo) f.catalogVolumeNo = searchForm.catalogVolumeNo.trim()
  return f
}

async function runQuery() {
  loading.value = true
  try {
    const res = await searchTransferApplicationRecords({
      filter: buildFilterPayload(),
      page: pagination.page,
      pageSize: pagination.pageSize,
      tenantid: DEFAULT_TENANT_ID
    })
    tableRecords.value = res.records
    pagination.total = res.total
  } catch (e) {
    console.error(e)
    let msg = '查询失败，请稍后重试'
    if (axios.isAxiosError(e) && e.response?.data && typeof e.response.data === 'object' && 'message' in e.response.data) {
      msg = String((e.response.data as { message?: string }).message || msg)
    } else if (e instanceof Error && e.message) {
      msg = e.message
    }
    ElMessage.error(msg)
  } finally {
    loading.value = false
  }
}

function reset() {
  searchForm.docBusiNo = ''
  searchForm.companyProjectCode = ''
  searchForm.busiModuleCode = ''
  searchForm.archPeriodRange = undefined
  searchForm.applicant = undefined
  searchForm.applicationNumber = ''
  searchForm.applicationDateRange = undefined
  searchForm.applicationStatus = ''
  searchForm.carrierType = ''
  searchForm.diffReasonCode = ''
  searchForm.applyMethod = ''
  searchForm.expressType = ''
  searchForm.expressNumber = ''
  searchForm.documentRecipient = undefined
  searchForm.catalogVolumeNo = ''
  pagination.page = 1
  runQuery()
}

function handleSizeChange() {
  pagination.page = 1
  runQuery()
}

function handlePageChange() {
  runQuery()
}

function goToCreate() {
  router.push('/archive-management/transfer')
}

function goEdit(applicationId: number) {
  router.push({ path: '/archive-management/transfer', query: { applicationId: String(applicationId) } })
}

async function openView(applicationId: number) {
  detailVisible.value = true
  detailLoading.value = true
  detailData.value = null
  try {
    detailData.value = await getTransferApplication(applicationId)
  } catch (e) {
    console.error(e)
    ElMessage.error('加载详情失败')
    detailVisible.value = false
  } finally {
    detailLoading.value = false
  }
}

function hintTodo(action: string) {
  ElMessage.info(`${action}功能开发中`)
}

onMounted(async () => {
  try {
    const options = await fetchArchiveCreateOptions()
    archiveOptions.value = options
    companyOptions.value = (options.companyProjects ?? []).map((c) => ({ code: c.code, name: c.name }))
    carrierTypeOptions.value = (options.carrierTypes ?? []).map((c) => ({ code: c.code, name: c.name }))
  } catch {
    ElMessage.warning('加载基础选项失败，部分下拉为空')
  }

  const [bm, st, diff, am, ex] = await Promise.all([
    tryDict('FUNCTION_MODULE'),
    tryDict('TRANSFER_APPLICATION_STATUS'),
    tryDict('TRANSFER_DIFF_REASON'),
    tryDict('TRANSFER_APPLY_METHOD'),
    tryDict('TRANSFER_EXPRESS_TYPE')
  ])
  busiModuleOptions.value = bm
  statusOptions.value = mergeOptions(st, FALLBACK_STATUS)
  diffReasonOptions.value = mergeOptions(diff, FALLBACK_DIFF)
  applyMethodOptions.value = mergeOptions(am, FALLBACK_APPLY)
  expressTypeOptions.value = mergeOptions(ex, FALLBACK_EXPRESS)

  await runQuery()
})
</script>

<style scoped>
.transfer-query-page {
  display: grid;
  gap: 16px;
}

.filter-form {
  margin-bottom: 8px;
}

.filter-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 10px;
  align-items: end;
}

.filter-grid--extra {
  margin-top: 4px;
}

.filter-form :deep(.el-form-item) {
  margin-bottom: 10px;
}

.input-w180 {
  width: 180px;
}

.input-range {
  width: 240px;
}

.span-range :deep(.el-form-item__content) {
  flex-wrap: nowrap;
}

.filter-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 4px;
  margin-bottom: 12px;
}

.filter-toolbar__actions {
  display: flex;
  gap: 10px;
}

.table-section {
  display: grid;
  gap: 12px;
}

.table-toolbar {
  display: flex;
  align-items: center;
}

.table-toolbar__left {
  display: flex;
  gap: 10px;
}

.loading-container {
  padding: 16px 0;
}

.transfer-list {
  display: grid;
  gap: 12px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
}

.transfer-detail {
  display: grid;
  gap: 16px;
}

.documents-section h3 {
  margin: 0;
  font-size: 15px;
  color: #24324a;
}

@media (max-width: 1400px) {
  .filter-grid {
    grid-template-columns: repeat(4, minmax(0, 1fr));
  }
}

@media (max-width: 1200px) {
  .filter-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}
</style>
