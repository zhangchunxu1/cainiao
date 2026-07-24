import { get, post, put, del } from '../utils/request'
import type { SalarySlip, PageResult } from '../types'

export const salaryApi = {
  getList: (params?: Record<string, any>) => {
    console.log('[Salary] Get list:', params)
    return get<PageResult<SalarySlip>>('/salary-slips', params)
  },
  
  getById: (id: number) => {
    console.log('[Salary] Get by id:', id)
    return get<SalarySlip>(`/salary-slips/${id}`)
  },
  
  create: (data: Record<string, any>) => {
    console.log('[Salary] Create')
    return post<SalarySlip>('/salary-slips', data, true)
  },
  
  update: (id: number, data: Record<string, any>) => {
    console.log('[Salary] Update:', id)
    return put<SalarySlip>(`/salary-slips/${id}`, data, true)
  },
  
  delete: (id: number) => {
    console.log('[Salary] Delete:', id)
    return del<void>(`/salary-slips/${id}`)
  }
}

export default salaryApi
