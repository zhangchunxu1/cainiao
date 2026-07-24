import { get, post, put, del } from '../utils/request'
import type { Announcement, PageResult } from '../types'

export const announcementApi = {
  getList: (params?: Record<string, any>) => {
    console.log('[Announcement] Get list:', params)
    return get<PageResult<Announcement>>('/announcements', params)
  },
  
  getById: (id: number) => {
    console.log('[Announcement] Get by id:', id)
    return get<Announcement>(`/announcements/${id}`)
  },
  
  add: (data: Record<string, any>) => {
    console.log('[Announcement] Add')
    return post<Announcement>('/announcements', data, true)
  },
  
  update: (id: number, data: Record<string, any>) => {
    console.log('[Announcement] Update:', id)
    return put<Announcement>(`/announcements/${id}`, data, true)
  },
  
  delete: (id: number) => {
    console.log('[Announcement] Delete:', id)
    return del<void>(`/announcements/${id}`)
  }
}

export default announcementApi
