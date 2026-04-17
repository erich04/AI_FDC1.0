import http, { apiRequest } from '../http'
import type { BorrowOrder, BorrowRenewOrder, MyBorrowDocument, RenewableBorrowOrder } from '../../types'

export interface BorrowOrderDetailCreateCommand {
  businessCode?: string
  documentName?: string
  company: string
  documentType?: string
  description?: string
  demandType?: string
  needReturn?: boolean
  expectedReturnDate?: string
  lendingApprover?: string
  lendingRemark?: string
  handler?: string
  handlerRemark?: string
}

export interface CreateBorrowOrderCommand {
  orderNo?: string
  userName: string
  userDepartment?: string
  applicantName: string
  applyTime?: string
  purpose?: string
  reason?: string
  reasonAttachment?: string
  approvalComment?: string
  demandApprover?: string
  demandReviewer?: string
  demandAnalyst?: string
  ccUsers?: string[]
  status?: string
  workflowInstanceId?: string
  currentHandler?: string
  details: BorrowOrderDetailCreateCommand[]
}

export interface BorrowRenewDetailCreateCommand {
  sourceDetailId?: number
  businessCode?: string
  documentName?: string
  company?: string
  borrowType?: string
  borrowTime?: string
  currentExpireTime?: string
  renewExpireTime?: string
  renewReason?: string
}

export interface CreateBorrowRenewOrderCommand {
  renewOrderNo?: string
  sourceOrderNo: string
  userName: string
  userDepartment?: string
  applicantName: string
  applyTime?: string
  purpose?: string
  reason?: string
  reasonAttachment?: string
  reviewer?: string
  handler?: string
  ccUsers?: string[]
  status?: string
  workflowInstanceId?: string
  currentHandler?: string
  details: BorrowRenewDetailCreateCommand[]
}

export interface BorrowOrderQuery {
  orderNo?: string
  applicantName?: string
  userName?: string
  status?: string
  company?: string
  businessCode?: string
  documentName?: string
}

export interface MyBorrowDocumentQuery {
  applicantName?: string
  company?: string
  businessCode?: string
  documentName?: string
  documentType?: string
  orderNo?: string
  status?: string
}

export function fetchBorrowOrders(params?: BorrowOrderQuery) {
  return apiRequest<BorrowOrder[]>(http.get('/api/archive/borrow-orders', { params }))
}

export function fetchBorrowOrder(orderNo: string) {
  return apiRequest<BorrowOrder>(http.get(`/api/archive/borrow-orders/${orderNo}`))
}

export function createBorrowOrder(data: CreateBorrowOrderCommand) {
  return apiRequest<BorrowOrder>(http.post('/api/archive/borrow-orders', data))
}

export function updateBorrowOrder(orderNo: string, data: CreateBorrowOrderCommand) {
  return apiRequest<BorrowOrder>(http.put(`/api/archive/borrow-orders/${orderNo}`, data))
}

export function fetchRenewableBorrowOrders(applicantName?: string) {
  return apiRequest<RenewableBorrowOrder[]>(http.get('/api/archive/borrow-orders/renewable', { params: { applicantName } }))
}

export function fetchMyBorrowDocuments(params?: MyBorrowDocumentQuery) {
  return apiRequest<MyBorrowDocument[]>(http.get('/api/archive/borrow-orders/my-documents', { params }))
}

export function fetchBorrowRenewOrders(applicantName?: string) {
  return apiRequest<BorrowRenewOrder[]>(http.get('/api/archive/borrow-renew-orders', { params: { applicantName } }))
}

export function fetchBorrowRenewOrder(renewOrderNo: string) {
  return apiRequest<BorrowRenewOrder>(http.get(`/api/archive/borrow-renew-orders/${renewOrderNo}`))
}

export function createBorrowRenewOrder(data: CreateBorrowRenewOrderCommand) {
  return apiRequest<BorrowRenewOrder>(http.post('/api/archive/borrow-renew-orders', data))
}
