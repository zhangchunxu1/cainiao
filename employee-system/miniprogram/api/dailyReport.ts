import { get, post, put, del } from '../utils/request'
import type { DailyReport, PageResult } from '../types'

export const dailyReportApi = {
  getList: (params?: Record<string, any>) => {
    console.log('[DailyReport] Get list:', params)
    return get<PageResult<DailyReport>>('/daily-reports', params)
  },
  
  getById: (id: number) => {
    console.log('[DailyReport] Get by id:', id)
    return get<DailyReport>(`/daily-reports/${id}`)
  },
  
  submit: (data: Record<string, any>) => {
    console.log('[DailyReport] Submit')
    return post<DailyReport>('/daily-reports', data, true)
  },
  
  update: (id: number, data: Record<string, any>) => {
    console.log('[DailyReport] Update:', id)
    return put<DailyReport>(`/daily-reports/${id}`, data, true)
  },
  
  delete: (id: number) => {
    console.log('[DailyReport] Delete:', id)
    return del<void>(`/daily-reports/${id}`)
  },
  
  review: (id: number, data: Record<string, any>) => {
    console.log('[DailyReport] Review:', id)
    return put<DailyReport>(`/daily-reports/${id}/review`, data, true)
  }
}

export default dailyReportApi
