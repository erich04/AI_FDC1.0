import http, { apiRequest } from '../http'
import type { ArchiveFlowRuleDetail, ArchiveFlowRuleOption, ArchiveFlowRuleSummary, AuditRecord, SecurityLevelOption } from '../../types'

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
  securityLevelCode: string
  externalDisplayFlag: string
  enabledFlag: string
}

export interface ArchiveFlowRuleUpdateCommand {
  busiModuleCode: string
  customRule?: string
  archiveDestination?: string
  documentOrganizationCode: string
  retentionPeriodYears: number
  securityLevelCode: string
  externalDisplayFlag: string
  enabledFlag: string
}

export function fetchArchiveFlowRules(params: ArchiveFlowRuleQuery) {
  return apiRequest<ArchiveFlowRuleSummary[]>(http.get('/api/base-data/archive-flow-rules', { params }))
}

export function fetchArchiveFlowRuleDetail(companyProjectCode: string) {
  return apiRequest<ArchiveFlowRuleDetail>(http.get(`/api/base-data/archive-flow-rules/${companyProjectCode}`))
}

export function createArchiveFlowRule(data: ArchiveFlowRuleCreateCommand) {
  return apiRequest<ArchiveFlowRuleDetail>(http.post('/api/base-data/archive-flow-rules', data))
}

export function updateArchiveFlowRule(companyProjectCode: string, data: ArchiveFlowRuleUpdateCommand) {
  return apiRequest<ArchiveFlowRuleDetail>(http.put(`/api/base-data/archive-flow-rules/${companyProjectCode}`, data))
}

export function deleteArchiveFlowRule(companyProjectCode: string) {
  return apiRequest<void>(http.delete(`/api/base-data/archive-flow-rules/${companyProjectCode}`))
}

export function fetchArchiveFlowCompanyProjectOptions() {
  return apiRequest<ArchiveFlowRuleOption[]>(http.get('/api/base-data/archive-flow-rules/options/company-projects'))
}

export function fetchArchiveFlowDocumentOrganizationOptions() {
  return apiRequest<ArchiveFlowRuleOption[]>(http.get('/api/base-data/archive-flow-rules/options/document-organizations'))
}

export function fetchArchiveFlowSecurityLevels() {
  return apiRequest<SecurityLevelOption[]>(http.get('/api/base-data/archive-flow-rules/options/security-levels'))
}

export function fetchModuleAudits(moduleCode: string) {
  return apiRequest<AuditRecord[]>(http.get(`/api/common/audits/modules/${moduleCode}`))
}
