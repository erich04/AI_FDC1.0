import http, { apiRequest } from '../http'
import type { ArchiveFlowRuleDetail, ArchiveFlowRuleOption, ArchiveFlowRuleSummary, AuditRecord } from '../../types'

export interface ArchiveFlowRuleQuery {
  keyword?: string
  companyProjectCode?: string
  busiModuleCode?: string
  documentOrganizationCode?: string
  enabledFlag?: string
}

export interface ArchiveFlowRuleCreateCommand {
  companyProjectCode: string
  busiModuleCode: string
  customRule?: string
  archiveDestination?: string
  documentOrganizationCode: string
  retentionPeriodYears: number
  externalDisplayFlag: string
  defaultFlag: string
  enabledFlag: string
}

export interface ArchiveFlowRuleUpdateCommand {
  busiModuleCode: string
  customRule?: string
  archiveDestination?: string
  documentOrganizationCode: string
  retentionPeriodYears: number
  externalDisplayFlag: string
  defaultFlag: string
  enabledFlag: string
}

export function fetchArchiveFlowRules(params: ArchiveFlowRuleQuery) {
  return apiRequest<ArchiveFlowRuleSummary[]>(http.get('/api/base-data/archive-flow-rules', { params }))
}

export function fetchArchiveFlowRuleDetail(id: number) {
  return apiRequest<ArchiveFlowRuleDetail>(http.get(`/api/base-data/archive-flow-rules/${id}`))
}

export function createArchiveFlowRule(data: ArchiveFlowRuleCreateCommand) {
  return apiRequest<ArchiveFlowRuleDetail>(http.post('/api/base-data/archive-flow-rules', data))
}

export function updateArchiveFlowRule(id: number, data: ArchiveFlowRuleUpdateCommand) {
  return apiRequest<ArchiveFlowRuleDetail>(http.put(`/api/base-data/archive-flow-rules/${id}`, data))
}

export function deleteArchiveFlowRule(id: number) {
  return apiRequest<void>(http.delete(`/api/base-data/archive-flow-rules/${id}`))
}

export function fetchArchiveFlowCompanyProjectOptions() {
  return apiRequest<ArchiveFlowRuleOption[]>(http.get('/api/base-data/archive-flow-rules/options/company-projects'))
}

export function fetchArchiveFlowBusinessModuleOptions() {
  return apiRequest<ArchiveFlowRuleOption[]>(http.get('/api/base-data/archive-flow-rules/options/business-modules'))
}

export function fetchArchiveFlowDocumentOrganizationOptions() {
  return apiRequest<ArchiveFlowRuleOption[]>(http.get('/api/base-data/archive-flow-rules/options/document-organizations'))
}

export function fetchArchiveFlowCityOptions() {
  return apiRequest<ArchiveFlowRuleOption[]>(http.get('/api/base-data/archive-flow-rules/options/cities'))
}

export function fetchModuleAudits(moduleCode: string) {
  return apiRequest<AuditRecord[]>(http.get(`/api/common/audits/modules/${moduleCode}`))
}
