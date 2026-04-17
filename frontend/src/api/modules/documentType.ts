import http, { apiRequest } from '../http'
import type { AuditRecord, BusinessModuleNode, DocumentTypePermissionPreview, DocumentTypeTreeNode } from '../../types'
import type { LabelValueOption } from '../../types'

export interface DocumentTypeCreateCommand {
  typeCode: string
  typeName: string
  description?: string
  enabledFlag: string
  parentCode?: string
}

export interface DocumentTypeUpdateCommand {
  typeName: string
  description?: string
  enabledFlag: string
  parentCode?: string
}

function mapNode(node: BusinessModuleNode): DocumentTypeTreeNode {
  return {
    id: node.id,
    typeCode: node.moduleCode,
    typeName: node.moduleName,
    description: node.description,
    enabledFlag: node.enabledFlag,
    parentCode: node.parentCode,
    levelNum: node.levelNum,
    ancestorPath: node.ancestorPath,
    sortOrder: node.sortOrder,
    deleteFlag: 'N',
    createdBy: 0,
    creationDate: '',
    lastUpdatedBy: node.lastUpdatedBy ?? 0,
    lastUpdateDate: node.lastUpdateDate ?? '',
    children: (node.children || []).map(mapNode)
  }
}

function flattenNodes(nodes: DocumentTypeTreeNode[]): DocumentTypeTreeNode[] {
  const result: DocumentTypeTreeNode[] = []
  const walk = (items: DocumentTypeTreeNode[]) => {
    for (const item of items) {
      result.push(item)
      if (item.children?.length) walk(item.children)
    }
  }
  walk(nodes)
  return result
}

export async function fetchDocumentTypeTree() {
  const tree = await apiRequest<BusinessModuleNode[]>(http.get('/api/base-data/business-modules/tree'))
  return tree.map(mapNode)
}

export async function fetchDocumentTypeDetail(typeCode: string) {
  const tree = await fetchDocumentTypeTree()
  const matched = flattenNodes(tree).find(item => item.typeCode === typeCode)
  if (!matched) {
    throw new Error(`未找到业务模块：${typeCode}`)
  }
  return matched
}

export async function createDocumentType(data: DocumentTypeCreateCommand) {
  const created = await apiRequest<BusinessModuleNode>(http.post('/api/base-data/business-modules', {
    moduleCode: data.typeCode,
    moduleName: data.typeName,
    parentCode: data.parentCode,
    enabledFlag: data.enabledFlag,
    description: data.description
  }))
  return mapNode(created)
}

export async function updateDocumentType(typeCode: string, data: DocumentTypeUpdateCommand) {
  const updated = await apiRequest<BusinessModuleNode>(http.put(`/api/base-data/business-modules/${typeCode}`, {
    moduleName: data.typeName,
    parentCode: data.parentCode,
    enabledFlag: data.enabledFlag,
    description: data.description
  }))
  return mapNode(updated)
}

export function deleteDocumentType(typeCode: string) {
  return apiRequest<void>(http.delete(`/api/base-data/business-modules/${typeCode}`))
}

export function fetchDocumentTypePermissionPreview() {
  return apiRequest<DocumentTypePermissionPreview>(http.get('/api/base-data/archive-flow-rules/permissions/preview'))
}

export async function fetchLevel3Modules(documentTypeCode: string) {
  const tree = await fetchDocumentTypeTree()
  const nodes = flattenNodes(tree)
  return nodes
    .filter(item => item.enabledFlag === 'Y' && item.levelNum === 3)
    .filter(item => {
      if (item.typeCode === documentTypeCode) return false
      const chain = [item.ancestorPath, item.typeCode].filter(Boolean).join('/')
      return chain.split('/').includes(documentTypeCode)
    })
    .map(item => ({ code: item.typeCode, name: item.typeName } as LabelValueOption))
}

export function fetchModuleAudits(moduleCode: string) {
  return apiRequest<AuditRecord[]>(http.get(`/api/common/audits/modules/${moduleCode}`))
}
