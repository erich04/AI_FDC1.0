import axios, { AxiosHeaders } from 'axios'
import { CURRENT_OPERATOR_USER_ID } from '../constants/currentUser'

export interface ApiResponse<T> {
  code?: number
  msg?: string
  success?: boolean
  message?: string
  data: T
}

const http = axios.create({
  // Keep same-origin requests; API modules already prefix paths with /api.
  baseURL: '',
  timeout: 15000
})

http.interceptors.request.use((config) => {
  const headers = AxiosHeaders.from(config.headers ?? {})
  if (headers.get('X-User-Id') == null && headers.get('x-user-id') == null) {
    headers.set('X-User-Id', String(CURRENT_OPERATOR_USER_ID))
  }
  config.headers = headers
  return config
})

http.interceptors.response.use((response) => response, (error) => Promise.reject(error))

export async function apiRequest<T>(promise: Promise<{ data: ApiResponse<T> }>): Promise<T> {
  const response = await promise
  const payload = response.data
  const isSuccess = typeof payload.code === 'number' ? payload.code === 0 : payload.success === true
  if (!isSuccess) {
    throw new Error(payload.msg || payload.message || 'Request failed')
  }
  return payload.data
}

export default http
