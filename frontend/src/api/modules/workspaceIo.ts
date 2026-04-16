import http, { apiRequest } from '../http'
import type { WorkspaceImportQueryResultRow, WorkspaceIoJobPage, WorkspaceIoJobSummary } from '../../types'

export interface WorkspaceIoJobCreateCommand {
  jobType: string
  dataType: string
  jobName: string
  documentTypeCode?: string
  queryConfigJson?: string
  inputFileName?: string
  inputTotal?: number
  resultTotal?: number
  durationMs?: number
  jobStatus: string
  errorMessage?: string
  failedFileCsv?: string
  exportFileFormat?: string
  /** 服务端留存 7 天的导出文本（CSV；Excel 为带 BOM 的 CSV） */
  resultArtifactText?: string
}

export interface WorkspaceIoJobQueryCommand {
  jobType?: string
  dataType?: string
  keyword?: string
  inputFileName?: string
  jobStatus?: string
  inputTotal?: number
  resultTotal?: number
  createdStart?: string
  createdEnd?: string
  exportFileFormat?: string
  /** 为 true 时仅查询导入类任务（排除导出等），「我的导入」页必传 */
  importTasksOnly?: boolean
  page?: number
  pageSize?: number
}

export function createWorkspaceIoJob(data: WorkspaceIoJobCreateCommand) {
  return apiRequest<WorkspaceIoJobSummary>(http.post('/api/workspace/io-jobs', data))
}

export function queryWorkspaceIoJobs(data: WorkspaceIoJobQueryCommand) {
  return apiRequest<WorkspaceIoJobPage>(http.post('/api/workspace/io-jobs/query', data))
}

export function deleteWorkspaceIoJob(jobId: number) {
  return apiRequest<void>(http.delete(`/api/workspace/io-jobs/${jobId}`))
}

export async function downloadWorkspaceIoFailedFile(jobId: number): Promise<Blob> {
  const res = await http.get(`/api/workspace/io-jobs/${jobId}/failed-file`, { responseType: 'blob' })
  return res.data as Blob
}

export function getWorkspaceIoJob(jobId: number) {
  return apiRequest<WorkspaceIoJobSummary>(http.get(`/api/workspace/io-jobs/${jobId}`))
}

export async function downloadWorkspaceExportFile(jobId: number): Promise<Blob> {
  const res = await http.get(`/api/workspace/io-jobs/${jobId}/export-file`, { responseType: 'blob' })
  return res.data as Blob
}

export async function downloadWorkspaceImportResultFile(jobId: number): Promise<Blob> {
  const res = await http.get(`/api/workspace/io-jobs/${jobId}/import-result`, { responseType: 'blob' })
  return res.data as Blob
}

export function fetchWorkspaceImportQueryResults(jobId: number) {
  return apiRequest<WorkspaceImportQueryResultRow[]>(
    http.get(`/api/workspace/io-jobs/${jobId}/import-query-results`)
  )
}
