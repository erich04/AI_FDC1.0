<template>
  <div class="workspace-page">
    <section class="filter-card">
      <div class="filter-grid">
        <div class="field">
          <label>草稿业务类型</label>
          <el-select v-model="filters.draftBizType" clearable placeholder="全部">
            <el-option label="应归档数据创建" value="PENDING_ARCHIVE_CREATE" />
            <el-option label="应归档数据编辑" value="PENDING_ARCHIVE_EDIT" />
            <el-option label="移交电子流" value="TRANSFER" />
            <el-option label="借阅电子流（预留）" value="BORROW" />
          </el-select>
        </div>
        <div class="field">
          <label>业务编号</label>
          <el-input v-model="filters.businessNo" clearable placeholder="业务编码/申请单号" />
        </div>
        <div class="field">
          <label>草稿标题</label>
          <el-input v-model="filters.titleKeyword" clearable placeholder="草稿名称/文档名称" />
        </div>
        <div class="field">
          <label>更新时间</label>
          <el-date-picker
            v-model="filters.updatedRange"
            type="datetimerange"
            value-format="YYYY-MM-DDTHH:mm:ss"
            range-separator="-"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
          />
        </div>
      </div>
      <div class="fdc-query-action-buttons">
        <el-button @click="resetFilters">重置</el-button>
        <el-button type="primary" :loading="loading" @click="loadDrafts">查询</el-button>
      </div>
    </section>

    <section class="table-card">
      <div class="table-wrap">
        <el-table :data="pagedRows" border stripe height="620" empty-text="暂无草稿记录">
          <el-table-column label="操作" width="80">
            <template #default="{ row }">
              <div class="result-actions">
                <el-tooltip
                  content="进入编辑页继续维护。未填字段在保存草稿时可能由系统自动写入占位值，打开后请留意页顶说明；保存草稿会更新列表中的最后更新时间。"
                  placement="top"
                >
                  <el-button circle text class="action-btn" @click="openDraft(row)">
                    <el-icon><Edit /></el-icon>
                  </el-button>
                </el-tooltip>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="草稿ID" width="180">
            <template #default="{ row }">{{ draftDisplayId(row) }}</template>
          </el-table-column>
          <el-table-column prop="bizTypeName" label="业务类型" width="140" />
          <el-table-column prop="businessNo" label="业务编号" min-width="170" show-overflow-tooltip />
          <el-table-column prop="title" label="草稿标题" min-width="220" show-overflow-tooltip />
          <el-table-column prop="statusName" label="状态" width="100" />
          <el-table-column label="创建时间" width="180">
            <template #default="{ row }">{{ formatDateTime(row.creationDate) }}</template>
          </el-table-column>
          <el-table-column label="更新时间" width="180">
            <template #default="{ row }">{{ formatDateTime(row.updatedAt) }}</template>
          </el-table-column>
        </el-table>
      </div>
      <div class="pagination">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="filteredRows.length"
          layout="total, sizes, prev, pager, next"
          :disabled="loading"
        />
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { Edit } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { queryPendingDocuments, type PendingDocumentRowResponse } from '../../api/modules/archiveManagement'
import {
  searchTransferApplicationRecords,
  type TransferApplicationRecordPageResult,
  type TransferApplicationRecordRow
} from '../../api/modules/transferApplications'
import { CURRENT_OPERATOR_USER_ID } from '../../constants/currentUser'

type DraftBizType = 'PENDING_ARCHIVE_CREATE' | 'PENDING_ARCHIVE_EDIT' | 'TRANSFER' | 'BORROW'

interface WorkspaceDraftRow {
  draftId: string
  bizType: DraftBizType
  bizTypeName: string
  businessNo: string
  title: string
  statusName: string
  creationDate?: string
  updatedAt?: string
  raw: PendingDocumentRowResponse | TransferApplicationRecordRow
}

const router = useRouter()
const loading = ref(false)
const page = ref(1)
const pageSize = ref(10)
const rows = ref<WorkspaceDraftRow[]>([])

const filters = reactive({
  draftBizType: '' as '' | DraftBizType,
  businessNo: '',
  titleKeyword: '',
  updatedRange: null as [string, string] | null
})

const formatDateTime = (value?: string | null) => {
  if (!value) return '-'
  const date = new Date(value.includes('T') ? value : value.replace(' ', 'T'))
  if (Number.isNaN(date.getTime())) return value
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  const h = String(date.getHours()).padStart(2, '0')
  const mi = String(date.getMinutes()).padStart(2, '0')
  const s = String(date.getSeconds()).padStart(2, '0')
  return `${y}-${m}-${d} ${h}:${mi}:${s}`
}

const toTime = (value?: string) => {
  if (!value) return 0
  const d = new Date(value.includes('T') ? value : value.replace(' ', 'T'))
  return Number.isNaN(d.getTime()) ? 0 : d.getTime()
}

const filteredRows = computed(() => {
  const bizNo = filters.businessNo.trim().toLowerCase()
  const title = filters.titleKeyword.trim().toLowerCase()
  const start = filters.updatedRange?.[0] ? toTime(filters.updatedRange[0]) : 0
  const end = filters.updatedRange?.[1] ? toTime(filters.updatedRange[1]) : 0
  return rows.value
    .filter((row) => {
      if (!filters.draftBizType) return true
      if (filters.draftBizType === 'PENDING_ARCHIVE_CREATE' || filters.draftBizType === 'PENDING_ARCHIVE_EDIT') {
        return row.bizType === 'PENDING_ARCHIVE_CREATE'
      }
      return row.bizType === filters.draftBizType
    })
    .filter((row) => !bizNo || (row.businessNo || '').toLowerCase().includes(bizNo))
    .filter((row) => !title || (row.title || '').toLowerCase().includes(title))
    .filter((row) => {
      if (!start && !end) return true
      const t = toTime(row.updatedAt || row.creationDate)
      return (!start || t >= start) && (!end || t <= end)
    })
    .sort((a, b) => toTime(b.updatedAt || b.creationDate) - toTime(a.updatedAt || a.creationDate))
})

const pagedRows = computed(() => {
  const begin = (page.value - 1) * pageSize.value
  return filteredRows.value.slice(begin, begin + pageSize.value)
})

const draftDisplayId = (row: WorkspaceDraftRow) =>
  row.bizType === 'PENDING_ARCHIVE_CREATE' || row.bizType === 'PENDING_ARCHIVE_EDIT'
    ? `PAD-${row.draftId.padStart(8, '0')}`
    : row.bizType === 'TRANSFER'
      ? `TRD-${row.draftId.padStart(8, '0')}`
      : `BRD-${row.draftId.padStart(8, '0')}`

const isPendingArchiveType = (type: DraftBizType) =>
  type === 'PENDING_ARCHIVE_CREATE' || type === 'PENDING_ARCHIVE_EDIT'

const loadPendingDrafts = async () => {
  const list = await queryPendingDocuments({
    businessCode: filters.businessNo.trim() || undefined,
    createdByUserId: CURRENT_OPERATOR_USER_ID
  })
  return list
    .filter((item) => String(item.docStatus || '').includes('草稿'))
    .map((item): WorkspaceDraftRow => {
      const docBizRaw = (item.businessCode || '').trim()
      const docBizNo = docBizRaw || '未填'
      const docName = item.documentName || '-'
      return {
        draftId: String(item.docId || ''),
        bizType: 'PENDING_ARCHIVE_CREATE',
        bizTypeName: '应归档数据创建',
        businessNo: `草稿_${docBizNo}`,
        title: `草稿_${docName}`,
        statusName: item.docStatus || '草稿',
        creationDate: item.creationTime,
        updatedAt: item.updatedAt || item.creationTime,
        raw: item
      }
    })
}

const loadTransferDrafts = async () => {
  const pageResult: TransferApplicationRecordPageResult = await searchTransferApplicationRecords({
    filter: {
      applicationStatus: 'DRAFT',
      applicant: CURRENT_OPERATOR_USER_ID,
      applicationNumber: filters.businessNo.trim() || undefined
    },
    page: 1,
    pageSize: 200
  })
  return (pageResult.records || []).map((item): WorkspaceDraftRow => ({
    draftId: String(item.applicationId || ''),
    bizType: 'TRANSFER',
    bizTypeName: '移交电子流',
    businessNo: item.applicationNumber || '-',
    title: item.documentTypeName ? `${item.documentTypeName}移交申请草稿` : '移交申请草稿',
    statusName: '草稿',
    creationDate: item.applicationDate || undefined,
    updatedAt: item.applicationDate || undefined,
    raw: item
  }))
}

const loadDrafts = async () => {
  loading.value = true
  try {
    const needPending =
      !filters.draftBizType ||
      filters.draftBizType === 'PENDING_ARCHIVE_CREATE' ||
      filters.draftBizType === 'PENDING_ARCHIVE_EDIT'
    const needTransfer = !filters.draftBizType || filters.draftBizType === 'TRANSFER'
    const tasks: Array<Promise<WorkspaceDraftRow[]>> = []
    if (needPending) tasks.push(loadPendingDrafts())
    if (needTransfer) tasks.push(loadTransferDrafts())
    const result = (await Promise.all(tasks)).flat()
    rows.value = result
    page.value = 1
    if (filters.draftBizType === 'BORROW') {
      rows.value = []
      ElMessage.info('借阅电子流草稿功能预留中，后续接口接入后会自动展示。')
    }
  } catch (e: any) {
    rows.value = []
    ElMessage.error(e?.message || '加载草稿失败')
  } finally {
    loading.value = false
  }
}

const openDraft = (row: WorkspaceDraftRow) => {
  if (isPendingArchiveType(row.bizType)) {
    router.push({
      path: '/archive-management/pending-archive/create',
      query: { resumeDraftId: row.draftId, from: 'workspace-drafts' }
    })
    return
  }
  if (row.bizType === 'TRANSFER') {
    router.push({
      path: '/archive-management/transfer',
      query: { applicationId: row.draftId, from: 'workspace-drafts' }
    })
  }
}

const resetFilters = () => {
  filters.draftBizType = ''
  filters.businessNo = ''
  filters.titleKeyword = ''
  filters.updatedRange = null
  page.value = 1
  void loadDrafts()
}

onMounted(() => {
  void loadDrafts()
})
</script>

<style scoped>
.workspace-page {
  min-height: 100%;
  display: grid;
  gap: 16px;
  width: 100%;
  max-width: 100%;
  overflow-x: hidden;
  box-sizing: border-box;
}
.filter-card,
.table-card {
  background: #fff;
  border: 1px solid #dbe0e6;
  border-radius: 12px;
  padding: 18px;
  box-shadow: 0 6px 22px rgba(15, 23, 42, 0.04);
  width: 100%;
  max-width: 100%;
  box-sizing: border-box;
  overflow: hidden;
}
.filter-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}
.field {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.field label {
  font-size: 13px;
  color: #64748b;
}
.result-actions {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  white-space: nowrap;
}
.action-btn { color: #1173d4; }
.table-wrap {
  width: 100%;
  max-width: 100%;
  overflow-x: auto;
}
.pagination {
  display: flex;
  justify-content: flex-end;
  padding-top: 14px;
}
:deep(.el-table th.el-table__cell) {
  background: #f8fafc;
  color: #64748b;
  font-weight: 700;
}
:deep(.el-table td.el-table__cell) {
  color: #334155;
}
:deep(.field .el-date-editor),
:deep(.field .el-select),
:deep(.field .el-input) {
  width: 100%;
}
:deep(.field .el-date-editor.el-range-editor) {
  width: 100% !important;
  min-width: 0 !important;
}
:deep(.fdc-query-action-buttons .el-button--primary) {
  --el-button-bg-color: #1173d4;
  --el-button-border-color: #1173d4;
  --el-button-hover-bg-color: #2c84de;
  --el-button-hover-border-color: #2c84de;
  --el-button-active-bg-color: #0f66bd;
  --el-button-active-border-color: #0f66bd;
}
:deep(.fdc-query-action-buttons) {
  border-top: none;
  padding-top: 0;
  margin-top: 14px;
}
:deep(.fdc-query-action-buttons .el-button:not(.el-button--primary)) {
  --el-button-bg-color: #ffffff;
  --el-button-border-color: #c4c8cf;
  --el-button-text-color: #6b7280;
  --el-button-hover-bg-color: #ffffff;
  --el-button-hover-border-color: #b8bdc6;
  --el-button-hover-text-color: #4b5563;
  --el-button-active-bg-color: #f9fafb;
  --el-button-active-border-color: #aeb4bf;
  --el-button-active-text-color: #374151;
}
:deep(.el-table) {
  width: 100%;
}
@media (max-width: 1200px) {
  .filter-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
@media (max-width: 768px) {
  .filter-grid {
    grid-template-columns: 1fr;
  }
}
</style>
