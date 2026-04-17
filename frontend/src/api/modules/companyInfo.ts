import http, { apiRequest } from '../http'
import type { CompanyInfo, CompanyTag } from '../../types'

export interface CompanyInfoCommand {
  companyCode: string
  companyName: string
  region?: string
  representativeOffice?: string
  country?: string
  description?: string
  tags?: string[]
  enabledFlag: 'Y' | 'N'
}

export interface CompanyInfoUpdateCommand {
  companyName: string
  region?: string
  representativeOffice?: string
  country?: string
  description?: string
  tags?: string[]
  enabledFlag: 'Y' | 'N'
}

export interface CompanyInfoQuery {
  companyCodes?: string[]
  companyName?: string
  region?: string
  representativeOffice?: string
  country?: string
  enabledFlag?: '' | 'Y' | 'N'
  tags?: string[]
}

export interface CompanyTagCommand {
  tagValue: string
  enabledFlag: 'Y' | 'N'
}

export function fetchCompanyInfos(params?: CompanyInfoQuery) {
  return apiRequest<CompanyInfo[]>(http.get('/api/base-data/company-infos', { params }))
}

export function createCompanyInfo(data: CompanyInfoCommand) {
  return apiRequest<CompanyInfo>(http.post('/api/base-data/company-infos', data))
}

export function updateCompanyInfo(companyCode: string, data: CompanyInfoUpdateCommand) {
  return apiRequest<CompanyInfo>(http.put(`/api/base-data/company-infos/${companyCode}`, data))
}

export function deleteCompanyInfo(companyCode: string) {
  return apiRequest<void>(http.delete(`/api/base-data/company-infos/${companyCode}`))
}

export function fetchCompanyTags(enabledOnly = false) {
  return apiRequest<CompanyTag[]>(http.get('/api/base-data/company-infos/tags', { params: { enabledOnly } }))
}

export function createCompanyTag(data: CompanyTagCommand) {
  return apiRequest<CompanyTag>(http.post('/api/base-data/company-infos/tags', data))
}

export function updateCompanyTag(tagId: number, data: CompanyTagCommand) {
  return apiRequest<CompanyTag>(http.put(`/api/base-data/company-infos/tags/${tagId}`, data))
}

export function deleteCompanyTag(tagId: number) {
  return apiRequest<void>(http.delete(`/api/base-data/company-infos/tags/${tagId}`))
}
