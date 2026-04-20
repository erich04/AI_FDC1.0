import http, { apiRequest } from '../http'
import type { BusinessModuleExtField, BusinessModuleNode } from '../../types'

/** 与「业务模块配置」查询区树形下拉相同的节点结构（编码 ｜ 名称） */
export type ModuleQueryTreeNode = BusinessModuleNode & {
  queryLabel: string
  children?: ModuleQueryTreeNode[]
}

export function buildModuleQueryTree(nodes: BusinessModuleNode[]): ModuleQueryTreeNode[] {
  return nodes.map((node) => ({
    ...node,
    queryLabel: `${node.moduleCode} ｜ ${node.moduleName}`,
    children: buildModuleQueryTree(node.children || [])
  }))
}

export interface BusinessModuleCommand {
  moduleCode: string
  moduleName: string
  parentCode?: string
  enabledFlag: 'Y' | 'N'
  sortOrder?: number
  securityLevel?: '公开' | '秘密' | '机密'
  integrationType?: '全部集成' | '部分集成' | '不集成'
  description?: string
  remark?: string
}

export interface BusinessModuleUpdateCommand {
  moduleName: string
  parentCode?: string
  enabledFlag: 'Y' | 'N'
  sortOrder?: number
  securityLevel?: '公开' | '秘密' | '机密'
  integrationType?: '全部集成' | '部分集成' | '不集成'
  description?: string
  remark?: string
}

export interface BusinessModuleExtFieldCommand {
  fieldCode: string
  fieldScope: 'BASIC' | 'ATTACHMENT'
  applicationFunctions?: ('应收' | '移交')[]
  extAttribute?: 'ATTR1' | 'ATTR2' | 'ATTR3' | 'ATTR4' | 'ATTR5' | 'ATTR6'
  fieldName: string
  englishFieldName?: string
  dataType: 'TEXT' | 'NUMBER' | 'DATE' | 'DATETIME' | 'DICT' | 'BOOLEAN'
  queryFlag: 'Y' | 'N'
  requiredFlag: 'Y' | 'N'
  enabledFlag: 'Y' | 'N'
  sortOrder: number
}

export interface BusinessModuleParentOption {
  code: string
  description?: string
  sourceType: 'DOCUMENT_TYPE' | 'BUSINESS_MODULE'
}

export function fetchBusinessModuleTree() {
  return apiRequest<BusinessModuleNode[]>(http.get('/api/base-data/business-modules/tree'))
}

export function fetchBusinessModuleParentOptions() {
  return apiRequest<BusinessModuleParentOption[]>(http.get('/api/base-data/business-modules/parent-options'))
}

export function createBusinessModule(data: BusinessModuleCommand) {
  return apiRequest<BusinessModuleNode>(http.post('/api/base-data/business-modules', data))
}

export function updateBusinessModule(moduleCode: string, data: BusinessModuleUpdateCommand) {
  return apiRequest<BusinessModuleNode>(http.put(`/api/base-data/business-modules/${moduleCode}`, data))
}

export function deleteBusinessModule(moduleCode: string) {
  return apiRequest<void>(http.delete(`/api/base-data/business-modules/${moduleCode}`))
}

export function fetchBusinessModuleExtFields(moduleCode: string, fieldScope?: 'BASIC' | 'ATTACHMENT') {
  return apiRequest<BusinessModuleExtField[]>(http.get(`/api/base-data/business-modules/${moduleCode}/ext-fields`, { params: { fieldScope } }))
}

export function createBusinessModuleExtField(moduleCode: string, data: BusinessModuleExtFieldCommand) {
  return apiRequest<BusinessModuleExtField>(http.post(`/api/base-data/business-modules/${moduleCode}/ext-fields`, data))
}

export function updateBusinessModuleExtField(moduleCode: string, fieldCode: string, data: BusinessModuleExtFieldCommand) {
  return apiRequest<BusinessModuleExtField>(http.put(`/api/base-data/business-modules/${moduleCode}/ext-fields/${fieldCode}`, data))
}

export function deleteBusinessModuleExtField(moduleCode: string, fieldCode: string) {
  return apiRequest<void>(http.delete(`/api/base-data/business-modules/${moduleCode}/ext-fields/${fieldCode}`))
}
