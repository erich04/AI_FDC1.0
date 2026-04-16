<template>
  <div class="workspace-page">
    <section class="filter-card">
      <div class="filter-grid">
        <div class="field">
          <label>文件名称</label>
          <el-input v-model="filters.inputFileName" placeholder="请输入文件名称" clearable />
        </div>
        <div class="field">
          <label>导出任务类型</label>
          <el-select v-model="filters.dataType" placeholder="全部" clearable>
            <el-option label="文档信息" value="DOCUMENT" />
            <el-option label="业务模块配置" value="DOCUMENT_TYPE" />
            <el-option label="文档组织配置" value="DOCUMENT_ORGANIZATION" />
            <el-option label="归档规则配置" value="ARCHIVE_FLOW_RULE" />
          </el-select>
        </div>
        <div class="field">
          <label>导出状态</label>
          <el-select v-model="filters.jobStatus" placeholder="全部" clearable>
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="成功" value="SUCCESS" />
            <el-option label="处理中" value="GENERATING" />
            <el-option label="失败" value="FAILED" />
            <el-option label="已过期" value="EXPIRED" />
          </el-select>
        </div>
        <div class="field">
          <label>导出格式</label>
          <el-select v-model="filters.exportFileFormat" placeholder="全部" clearable>
            <el-option label="CSV" value="CSV" />
            <el-option label="EXCEL" value="EXCEL" />
            <el-option label="PDF" value="PDF" />
          </el-select>
        </div>
        <div class="field">
          <label>导出时间</label>
          <el-date-picker
            v-model="filters.createdRange"
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
        <el-button type="primary" :loading="loading" @click="loadHistory">查询</el-button>
      </div>
    </section>
    <section class="table-card">
      <div class="table-wrap">
      <el-table :data="history.records" border stripe height="620" empty-text="暂无导出记录">
        <el-table-column label="操作" width="110">
          <template #default="{ row }">
            <div class="result-actions">
              <el-tooltip content="下载">
                <el-button circle text class="action-btn" :disabled="!row.exportDownloadable" @click="downloadExport(row)">
                  <el-icon><Download /></el-icon>
                </el-button>
              </el-tooltip>
              <el-tooltip content="删除">
                <el-button circle text class="danger-btn" @click="deleteJob(row)">
                  <el-icon><Delete /></el-icon>
                </el-button>
              </el-tooltip>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="导出任务ID" width="180">
          <template #default="{ row }">{{ exportTaskId(row.jobId) }}</template>
        </el-table-column>
        <el-table-column label="导出时间" width="180">
          <template #default="{ row }">{{ formatDateTime(row.creationDate) }}</template>
        </el-table-column>
        <el-table-column label="导出文件名" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">{{ exportFileDisplayName(row) }}</template>
        </el-table-column>
        <el-table-column label="导出任务类型" width="140" show-overflow-tooltip>
          <template #default="{ row }">{{ dataTypeLabel(row.dataType) }}</template>
        </el-table-column>
        <el-table-column prop="resultTotal" label="导出总条数" width="120" />
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.displayStatus || row.jobStatus)" effect="light">
              {{ exportStatusLabel(row.displayStatus || row.jobStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="失败原因" min-width="220" show-overflow-tooltip>
          <template #default="{ row }">
            <el-tooltip
              v-if="hasFailureReason(row)"
              :content="row.errorMessage"
              placement="top"
              effect="dark"
            >
              <span class="fail-reason-text">{{ row.errorMessage }}</span>
            </el-tooltip>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="文件大小" width="120">
          <template #default="{ row }">{{ fileSizeText(row) }}</template>
        </el-table-column>
      </el-table>
      </div>

      <div class="pagination">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="history.total"
          layout="total, sizes, prev, pager, next"
          :disabled="loading"
          @size-change="loadHistory"
          @current-change="loadHistory"
        />
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { Delete, Download } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { deleteWorkspaceIoJob, downloadWorkspaceExportFile, queryWorkspaceIoJobs } from '../../api/modules/workspaceIo'
import type { WorkspaceIoJobPage, WorkspaceIoJobSummary } from '../../types'

const loading = ref(false)
const page = ref(1)
const pageSize = ref(10)
const history = ref<WorkspaceIoJobPage>({ records: [], total: 0, pages: 0, page: 1, pageSize: 10 })
const filters = reactive({
  inputFileName: '',
  dataType: '',
  jobStatus: '',
  exportFileFormat: '',
  createdRange: null as [string, string] | null
})

const exportTaskId = (jobId: number) => `EXP-${String(jobId).padStart(8, '0')}`

const exportArtifactExtension = (row: WorkspaceIoJobSummary) =>
  row.exportFileFormat === 'PDF' ? 'pdf' : row.exportFileFormat === 'EXCEL' ? 'xlsx' : 'csv'

/** 历史任务 jobName 可能为中文说明；无常见扩展名时按 jobId+格式回退为下载文件名 */
const looksLikeExportFileName = (name: string) => /\.(csv|xlsx|xls|pdf)$/i.test(name.trim())

const exportFileDisplayName = (row: WorkspaceIoJobSummary) => {
  const n = (row.jobName || '').trim()
  if (looksLikeExportFileName(n)) return n
  return `workspace-export-${row.jobId}.${exportArtifactExtension(row)}`
}

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
const dataTypeLabel = (v: string) =>
  v === 'DOCUMENT' ? '文档信息'
  : v === 'DOCUMENT_TYPE' ? '业务模块配置'
  : v === 'DOCUMENT_ORGANIZATION' ? '文档组织配置'
  : v === 'ARCHIVE_FLOW_RULE' ? '归档规则配置'
  : (v || '-')
const exportStatusLabel = (v: string) => (v === 'COMPLETED' || v === 'SUCCESS' ? '已完成' : v === 'EXPIRED' ? '已过期' : v === 'FAILED' ? '失败' : v === 'GENERATING' ? '处理中' : v || '-')
const statusTagType = (v: string) => ((v === 'COMPLETED' || v === 'SUCCESS') ? 'success' : v === 'EXPIRED' ? 'info' : v === 'FAILED' ? 'danger' : 'warning')
const fileSizeText = (row: WorkspaceIoJobSummary) => (row.resultTotal && row.resultTotal > 0 ? `${Math.max(1, Math.round(row.resultTotal / 2))} KB` : '-')
const hasFailureReason = (row: WorkspaceIoJobSummary) => (row.displayStatus || row.jobStatus) === 'FAILED' && Boolean(row.errorMessage?.trim())

const loadHistory = async () => {
  loading.value = true
  try {
    history.value = await queryWorkspaceIoJobs({
      jobType: 'EXPORT_QUERY',
      inputFileName: filters.inputFileName || undefined,
      dataType: filters.dataType || undefined,
      jobStatus: filters.jobStatus || undefined,
      exportFileFormat: filters.exportFileFormat || undefined,
      createdStart: filters.createdRange?.[0] || undefined,
      createdEnd: filters.createdRange?.[1] || undefined,
      page: page.value,
      pageSize: pageSize.value
    })
  } finally {
    loading.value = false
  }
}

const resetFilters = () => {
  filters.inputFileName = ''
  filters.dataType = ''
  filters.jobStatus = ''
  filters.exportFileFormat = ''
  filters.createdRange = null
  page.value = 1
  void loadHistory()
}

const downloadExport = async (row: WorkspaceIoJobSummary) => {
  try {
    const blob = await downloadWorkspaceExportFile(row.jobId)
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = exportFileDisplayName(row)
    document.body.appendChild(a)
    a.click()
    a.remove()
    URL.revokeObjectURL(url)
  } catch (e: any) {
    ElMessage.error(e?.message || '下载失败')
  }
}

const deleteJob = async (row: WorkspaceIoJobSummary) => {
  try {
    await ElMessageBox.confirm('确认删除该导出记录吗？', '提示', { type: 'warning' })
    await deleteWorkspaceIoJob(row.jobId)
    ElMessage.success('已删除')
    await loadHistory()
  } catch (e: any) {
    if (e !== 'cancel') ElMessage.error(e?.message || '删除失败')
  }
}

onMounted(() => {
  void loadHistory()
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
.filter-card {
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
  display: flex;
  align-items: center;
  gap: 6px;
}
.table-wrap {
  width: 100%;
  max-width: 100%;
  overflow-x: auto;
}
.action-btn { color: #1173d4; }
.danger-btn { color: #ef4444; }
.fail-reason-text {
  display: inline-block;
  max-width: 100%;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
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
