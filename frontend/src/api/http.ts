import axios from 'axios'

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
