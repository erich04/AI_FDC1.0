import http, { apiRequest } from '../http'

export interface TransferApplyFieldConfigItem {
  fieldCode: string
  fieldName: string
  visibleFlag: 'Y' | 'N'
  sortOrder: number
}

export interface TransferApplyFieldConfigResponse {
  documentTypeCode: string
  fields: TransferApplyFieldConfigItem[]
}

export interface TransferApplyFieldConfigSaveCommand {
  tenantid: number
  fields: Array<{
    fieldCode: string
    visibleFlag: 'Y' | 'N'
  }>
}

export function getTransferApplyFieldConfig(documentTypeCode: string, tenantid?: number) {
  return apiRequest<TransferApplyFieldConfigResponse>(
    http.get(`/api/archive-management/transfer-apply-field-configs/${encodeURIComponent(documentTypeCode)}`, {
      params: tenantid ? { tenantid } : undefined
    })
  )
}

export function saveTransferApplyFieldConfig(documentTypeCode: string, command: TransferApplyFieldConfigSaveCommand) {
  return apiRequest<TransferApplyFieldConfigResponse>(
    http.post(`/api/archive-management/transfer-apply-field-configs/${encodeURIComponent(documentTypeCode)}`, command)
  )
}

export function getTransferApplyFieldVisibility(documentTypeCode: string, tenantid?: number) {
  return apiRequest<Record<string, boolean>>(
    http.get('/api/archive-management/transfer-applications/field-visibility', {
      params: {
        documentTypeCode,
        ...(tenantid ? { tenantid } : {})
      }
    })
  )
}
