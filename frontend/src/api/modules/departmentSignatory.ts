import http, { apiRequest } from '../http'
import type { DepartmentSignatory } from '../../types'

export interface DepartmentSignatoryCommand {
  firstLevelDepartment: string
  secondLevelDepartment?: string
  thirdLevelDepartment?: string
  fourthLevelDepartment?: string
  signatories: string[]
}

export interface DepartmentSignatoryQuery {
  departmentName?: string
  signatories?: string[]
}

export function fetchDepartmentSignatories(params?: DepartmentSignatoryQuery) {
  return apiRequest<DepartmentSignatory[]>(http.get('/api/base-data/department-signatories', { params }))
}

export function createDepartmentSignatory(data: DepartmentSignatoryCommand) {
  return apiRequest<DepartmentSignatory>(http.post('/api/base-data/department-signatories', data))
}

export function updateDepartmentSignatory(id: number, data: DepartmentSignatoryCommand) {
  return apiRequest<DepartmentSignatory>(http.put(`/api/base-data/department-signatories/${id}`, data))
}

export function deleteDepartmentSignatory(id: number) {
  return apiRequest<void>(http.delete(`/api/base-data/department-signatories/${id}`))
}
