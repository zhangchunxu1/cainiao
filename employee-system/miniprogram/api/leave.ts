import { get, post, put, del } from '../utils/request'
import type { LeaveRequest, PageResult } from '../types'

export const leaveApi = {
  getList: (params?: Record<string, any>) => {
    console.log('[Leave] Get list:', params)
    return get<PageResult<LeaveRequest>>('/leaves', params)
  },
  
  getById: (id: number) => {
    console.log('[Leave] Get by id:', id)
    return get<LeaveRequest>(`/leaves/${id}`)
  },
  
  submit: (data: Record<string, any>) => {
    console.log('[Leave] Submit request')
    return post<LeaveRequest>('/leaves', data, true)
  },
  
  approve: (id: number, data: Record<string, any>) => {
    console.log('[Leave] Approve:', id)
    return put<LeaveRequest>(`/leaves/${id}/approve`, data, true)
  },
  
  reject: (id: number, data: Record<string, any>) => {
    console.log('[Leave] Reject:', id)
    return put<LeaveRequest>(`/leaves/${id}/reject`, data, true)
  },
  
  delete: (id: number) => {
    console.log('[Leave] Delete:', id)
    return del<void>(`/leaves/${id}`)
  }
}

export default leaveApi
