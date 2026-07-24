import { get, post, del } from '../utils/request'
import type { Attendance, PageResult } from '../types'

export const attendanceApi = {
  getList: (params?: Record<string, any>) => {
    console.log('[Attendance] Get list:', params)
    return get<PageResult<Attendance>>('/attendance', params)
  },
  
  getToday: (params?: Record<string, any>) => {
    console.log('[Attendance] Get today attendance')
    return get<Attendance>('/attendance/today', params)
  },
  
  checkIn: (data?: Record<string, any>) => {
    console.log('[Attendance] Check in')
    return post<Attendance>('/attendance/checkin', data, true)
  },
  
  checkOut: (data?: Record<string, any>) => {
    console.log('[Attendance] Check out')
    return post<Attendance>('/attendance/checkout', data, true)
  },
  
  clearAll: () => {
    console.log('[Attendance] Clear all')
    return del<void>('/attendance/clear')
  }
}

export default attendanceApi
