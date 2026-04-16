import http, { apiRequest } from '../http'
import type { DocumentTypeConfig } from '../../types'

export interface DocumentTypeConfigQueryParams {
  docTypeCode?: string
  docTypeDescription?: string
  enableFlag?: 'Y' | 'N'
  tenantid?: number
}

export interface DocumentTypeConfigSaveCommand {
  docTypeCode: string
  docTypeDescription: string
  enableFlag: 'Y' | 'N'
  tenantid?: number
}

export function queryDocumentTypeConfigs(params: DocumentTypeConfigQueryParams) {
  return apiRequest<DocumentTypeConfig[]>(http.get('/api/base-data/document-type-configs', { params }))
}

export function createDocumentTypeConfig(data: DocumentTypeConfigSaveCommand) {
  return apiRequest<DocumentTypeConfig>(http.post('/api/base-data/document-type-configs', data))
}

export function updateDocumentTypeConfig(documentTypeId: number, data: DocumentTypeConfigSaveCommand) {
  return apiRequest<DocumentTypeConfig>(http.put(`/api/base-data/document-type-configs/${documentTypeId}`, data))
}

export async function exportDocumentTypeConfigsCsv(params: DocumentTypeConfigQueryParams) {
  const response = await http.get('/api/base-data/document-type-configs/export/csv', {
    params,
    responseType: 'blob'
  })
  return response.data as Blob
}

export async function exportDocumentTypeConfigsExcel(params: DocumentTypeConfigQueryParams) {
  const response = await http.get('/api/base-data/document-type-configs/export/excel', {
    params,
    responseType: 'blob'
  })
  return response.data as Blob
}

export function importDocumentTypeConfigsCsv(file: File, tenantid?: number) {
  const formData = new FormData()
  formData.append('file', file)
  if (typeof tenantid === 'number') {
    formData.append('tenantid', String(tenantid))
  }
  return apiRequest<number>(http.post('/api/base-data/document-type-configs/import/csv', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  }))
}

export function importDocumentTypeConfigsExcel(file: File, tenantid?: number) {
  const formData = new FormData()
  formData.append('file', file)
  if (typeof tenantid === 'number') {
    formData.append('tenantid', String(tenantid))
  }
  return apiRequest<number>(http.post('/api/base-data/document-type-configs/import/excel', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  }))
}
