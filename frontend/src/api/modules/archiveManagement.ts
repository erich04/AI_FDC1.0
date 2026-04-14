import http, { apiRequest } from '../http'
import type {
  ArchiveAskResult,
  ArchiveAiModelSummary,
  ArchiveCreateOptions,
  ArchiveCreateSession,
  ArchiveDefaultResolve,
  ArchiveQueryResult,
  ArchiveRecordSummary,
  BindBatch,
  BindOptions,
  BindPreviewResult,
  DocumentTypeExtField,
  FourAttrInspectionConfig,
  FourAttrInspectionDetail,
  LabelValueOption,
  StorageBatch,
  StorageLedger,
  StorageOptions,
  StorageQueryResult
} from '../../types'

export interface DocumentTypeExtFieldCreateCommand {
  usageModule: string
  relatedModuleCode: string
  relatedField: string
  fieldName: string
  fieldType: 'TEXT' | 'DICT'
  dictCategoryCode?: string
  requiredFlag: 'Y' | 'N'
  enabledFlag: 'Y' | 'N'
  formSortOrder: number
  queryEnabledFlag: 'Y' | 'N'
  querySortOrder: number
}

export interface ArchiveCreateCommand {
  sessionCode?: string
  createMode?: 'AUTO' | 'MANUAL'
  busiModuleCode: string
  companyProjectCode: string
  beginPeriod: string
  endPeriod: string
  businessCode?: string
  documentName: string
  dutyPerson: string
  dutyDepartment: string
  documentDate: string
  securityLevelCode: string
  sourceSystem?: string
  archiveDestination?: string
  originPlace?: string
  carrierTypeCode: 'ELECTRONIC' | 'PAPER' | 'HYBRID'
  remark?: string
  aiArchiveSummary?: string
  documentOrganizationCode: string
  retentionPeriodYears: number
  archiveTypeCode: string
  countryCode?: string
  customRule?: string
  extValues?: Record<string, string>
  paperInfo?: {
    plannedCopyCount?: number
    actualCopyCount?: number
    remark?: string
  }
}

export interface ArchiveQueryCommand {
  keyword?: string
  busiModuleCode?: string
  companyProjectCode?: string
  archiveTypeCode?: string
  carrierTypeCode?: string
  securityLevelCode?: string
  beginPeriod?: string
  endPeriod?: string
  documentName?: string
  businessCode?: string
  dutyPerson?: string
  archiveDestination?: string
  sourceSystem?: string
  documentOrganizationCode?: string
  extFilters?: Record<string, string>
  excludeSubmittedTransferApplied?: boolean
}

export interface ArchiveAskCommand {
  question: string
  busiModuleCode?: string
  companyProjectCode?: string
}

export interface ArchiveTransferCommand {
  archiveIds: number[]
  assigneeId: string
  assigneeName?: string
  transferMethod: 'DIRECT' | 'MAIL'
  logisticsCompany?: string
  trackingNumber?: string
  remark?: string
  initiatorId?: string
  initiatorName?: string
}

export interface ArchiveTransferResponse {
  businessKey: string
  processInstanceId: string
  workflowInstanceId: number
  archiveCount: number
  archiveFilingCodes: string[]
}

export interface BindPreviewCommand {
  bindMode: 'BUSINESS_CODE' | 'PERIOD' | 'MANUAL'
  keyword?: string
  busiModuleCode?: string
  companyProjectCode?: string
  archiveIds?: number[]
}

export interface BindCreateCommand {
  bindMode: 'BUSINESS_CODE' | 'PERIOD' | 'MANUAL'
  bindRemark?: string
  volumes: Array<{
    volumeTitle?: string
    bindRuleKey?: string
    carrierTypeCode?: string
    remark?: string
    items: Array<{
      archiveId: number
      sortNo?: number
      primaryFlag?: 'Y' | 'N'
      bindReason?: string
    }>
  }>
}

export interface BindQueryCommand {
  bindMode?: string
  bindStatus?: string
  keyword?: string
}

export interface StorageQueryCommand {
  sourceBindBatchCode?: string
  keyword?: string
}

export interface StorageCreateCommand {
  sourceType: 'BIND_GUIDED' | 'DIRECT'
  sourceBindBatchCode?: string
  warehouseCode: string
  remark?: string
  items: Array<{
    itemType: 'VOLUME' | 'ARCHIVE'
    volumeId?: number
    archiveId?: number
    locationCode: string
  }>
}

export interface StorageLedgerQueryCommand {
  storageBatchCode?: string
  bindVolumeCode?: string
  archiveCode?: string
  warehouseCode?: string
  locationCode?: string
  resultStatus?: string
}

export function fetchDocumentTypeExtFields(busiModuleCode: string) {
  return apiRequest<DocumentTypeExtField[]>(http.get(`/api/base-data/document-types/${busiModuleCode}/ext-fields`))
}

export function fetchEffectiveDocumentTypeExtFields(busiModuleCode: string) {
  return apiRequest<DocumentTypeExtField[]>(http.get(`/api/base-data/document-types/${busiModuleCode}/ext-fields/effective`))
}

export function createDocumentTypeExtField(busiModuleCode: string, data: DocumentTypeExtFieldCreateCommand) {
  return apiRequest<DocumentTypeExtField>(http.post(`/api/base-data/document-types/${busiModuleCode}/ext-fields`, data))
}

export function updateDocumentTypeExtField(busiModuleCode: string, fieldCode: string, data: DocumentTypeExtFieldCreateCommand) {
  return apiRequest<DocumentTypeExtField>(http.put(`/api/base-data/document-types/${busiModuleCode}/ext-fields/${fieldCode}`, data))
}

export function deleteDocumentTypeExtField(busiModuleCode: string, fieldCode: string) {
  return apiRequest<void>(http.delete(`/api/base-data/document-types/${busiModuleCode}/ext-fields/${fieldCode}`))
}

export function fetchArchiveCreateOptions() {
  return apiRequest<ArchiveCreateOptions>(http.get('/api/archive-management/create/options'))
}

export function resolveArchiveDefaults(params: {
  companyProjectCode: string
  busiModuleCode: string
  customRule?: string
  archiveDestination?: string
}) {
  return apiRequest<ArchiveDefaultResolve>(http.get('/api/archive-management/create/defaults', { params }))
}

export function createArchiveSession(createMode: 'AUTO' | 'MANUAL') {
  return apiRequest<ArchiveCreateSession>(http.post('/api/archive-management/create/sessions', { createMode }))
}

export function fetchArchiveSession(sessionCode: string) {
  return apiRequest<ArchiveCreateSession>(http.get(`/api/archive-management/create/sessions/${sessionCode}`))
}

export async function uploadArchiveAttachment(params: {
  sessionCode: string
  attachmentRole: 'ELECTRONIC' | 'PAPER_SCAN'
  attachmentTypeCode?: string
  remark?: string
  file: File
}) {
  const formData = new FormData()
  formData.append('attachmentRole', params.attachmentRole)
  if (params.attachmentTypeCode) formData.append('attachmentTypeCode', params.attachmentTypeCode)
  if (params.remark) formData.append('remark', params.remark)
  formData.append('file', params.file)
  return apiRequest(http.post(`/api/archive-management/create/sessions/${params.sessionCode}/attachments`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })) as Promise<any>
}

export function updateArchiveAttachment(params: {
  sessionCode: string
  attachmentId: number
  attachmentTypeCode?: string
  remark?: string
  aiSummary?: string
}) {
  return apiRequest(http.put(`/api/archive-management/create/sessions/${params.sessionCode}/attachments/${params.attachmentId}`, {
    attachmentTypeCode: params.attachmentTypeCode,
    remark: params.remark,
    aiSummary: params.aiSummary
  })) as Promise<any>
}

export function createArchive(data: ArchiveCreateCommand) {
  return apiRequest<ArchiveRecordSummary>(http.post('/api/archive-management/create/archives', data))
}

export function queryArchives(data: ArchiveQueryCommand) {
  return apiRequest<ArchiveQueryResult>(http.post('/api/archive-management/create/query', data))
}

export function askArchiveQuestion(data: ArchiveAskCommand) {
  return apiRequest<ArchiveAskResult>(http.post('/api/archive-management/create/ask', data))
}

export function transferArchives(data: ArchiveTransferCommand) {
  return apiRequest<ArchiveTransferResponse>(http.post('/api/archive-management/archives/transfer', data))
}

export function fetchArchiveAiModels() {
  return apiRequest<ArchiveAiModelSummary[]>(http.get('/api/archive-management/ai-models'))
}

export function getArchiveDetail(archiveId: number) {
  return apiRequest<ArchiveRecordSummary>(http.get(`/api/archive-management/archives/${archiveId}`))
}

export function fetchBindOptions() {
  return apiRequest<BindOptions>(http.get('/api/archive-management/bind/options'))
}

export function previewBind(data: BindPreviewCommand) {
  return apiRequest<BindPreviewResult>(http.post('/api/archive-management/bind/preview', data))
}

export function createBindBatch(data: BindCreateCommand) {
  return apiRequest<BindBatch>(http.post('/api/archive-management/bind/batches', data))
}

export function getBindBatch(bindBatchCode: string) {
  return apiRequest<BindBatch>(http.get(`/api/archive-management/bind/batches/${bindBatchCode}`))
}

export function queryBindBatches(data: BindQueryCommand) {
  return apiRequest<BindBatch[]>(http.post('/api/archive-management/bind/query', data))
}

export function fetchStorageOptions() {
  return apiRequest<StorageOptions>(http.get('/api/archive-management/storage/options'))
}

export function queryStorage(data: StorageQueryCommand) {
  return apiRequest<StorageQueryResult>(http.post('/api/archive-management/storage/query', data))
}

export function createStorageBatch(data: StorageCreateCommand) {
  return apiRequest<StorageBatch>(http.post('/api/archive-management/storage/batches', data))
}

export function getStorageBatch(storageBatchCode: string) {
  return apiRequest<StorageBatch>(http.get(`/api/archive-management/storage/batches/${storageBatchCode}`))
}

export function queryStorageLedger(data: StorageLedgerQueryCommand) {
  return apiRequest<StorageLedger[]>(http.post('/api/archive-management/storage/ledger', data))
}

export function getStorageLedger(ledgerId: number) {
  return apiRequest<StorageLedger>(http.get(`/api/archive-management/storage/ledger/${ledgerId}`))
}

export interface FourAttrInspectionQueryParams {
  inspectionName?: string
  inspectionStage?: string
  enableFlag?: 'Y' | 'N'
  tenantid?: number
}

export interface FourAttrInspectionSaveCommand {
  inspectionName: string
  inspectionStage: string
  dataPackageSpec: string
  metadataSpec: string
  enableFlag: 'Y' | 'N'
  tenantid?: number
}

export interface FourAttrInspectionDetailBatchSaveCommand {
  inspectionId: number
  tenantid?: number
  details: FourAttrInspectionDetail[]
}

export function queryFourAttrInspections(params: FourAttrInspectionQueryParams) {
  return apiRequest<FourAttrInspectionConfig[]>(http.get('/api/security/four-properties/configs', { params }))
}

export function getFourAttrInspectionDetail(inspectionId: number, tenantid?: number) {
  return apiRequest<FourAttrInspectionConfig>(http.get(`/api/security/four-properties/configs/${inspectionId}`, {
    params: tenantid ? { tenantid } : undefined
  }))
}

export function createFourAttrInspection(data: FourAttrInspectionSaveCommand) {
  return apiRequest<FourAttrInspectionConfig>(http.post('/api/security/four-properties/configs', data))
}

export function updateFourAttrInspection(inspectionId: number, data: FourAttrInspectionSaveCommand) {
  return apiRequest<FourAttrInspectionConfig>(http.put(`/api/security/four-properties/configs/${inspectionId}`, data))
}

export function saveFourAttrInspectionDetails(inspectionId: number, data: FourAttrInspectionDetailBatchSaveCommand) {
  return apiRequest<FourAttrInspectionConfig>(http.put(`/api/security/four-properties/configs/${inspectionId}/details`, data))
}

export async function exportFourAttrInspections(params: FourAttrInspectionQueryParams) {
  const response = await http.get('/api/security/four-properties/configs/export', {
    params,
    responseType: 'blob'
  })
  return response.data as Blob
}

export function importFourAttrInspections(file: File, tenantid?: number) {
  const formData = new FormData()
  formData.append('file', file)
  if (typeof tenantid === 'number') {
    formData.append('tenantid', String(tenantid))
  }
  return apiRequest<number>(http.post('/api/security/four-properties/configs/import', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  }))
}
