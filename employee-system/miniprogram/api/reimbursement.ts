import { get, post, put, del } from '../utils/request'
import type { Reimbursement, PageResult } from '../types'

export const reimbursementApi = {
  getList: (params?: Record<string, any>) => {
    console.log('[Reimbursement] Get list:', params)
    return get<PageResult<Reimbursement>>('/reimbursements', params)
  },
  
  getById: (id: number) => {
    console.log('[Reimbursement] Get by id:', id)
    return get<Reimbursement>(`/reimbursements/${id}`)
  },
  
  create: (data: Record<string, any>) => {
    console.log('[Reimbursement] Create')
    return post<Reimbursement>('/reimbursements', data, true)
  },
  
  update: (id: number, data: Record<string, any>) => {
    console.log('[Reimbursement] Update:', id)
    return put<Reimbursement>(`/reimbursements/${id}`, data, true)
  },
  
  delete: (id: number) => {
    console.log('[Reimbursement] Delete:', id)
    return del<void>(`/reimbursements/${id}`)
  },
  
  managerApprove: (id: number, data: Record<string, any>) => {
    console.log('[Reimbursement] Manager approve:', id)
    return post<Reimbursement>(`/reimbursements/${id}/manager-approve`, data, true)
  },
  
  managerReject: (id: number, data: Record<string, any>) => {
    console.log('[Reimbursement] Manager reject:', id)
    return post<Reimbursement>(`/reimbursements/${id}/manager-reject`, data, true)
  },
  
  financeApprove: (id: number, data: Record<string, any>) => {
    console.log('[Reimbursement] Finance approve:', id)
    return post<Reimbursement>(`/reimbursements/${id}/finance-approve`, data, true)
  },
  
  financeReject: (id: number, data: Record<string, any>) => {
    console.log('[Reimbursement] Finance reject:', id)
    return post<Reimbursement>(`/reimbursements/${id}/finance-reject`, data, true)
  }
}

export default reimbursementApi
