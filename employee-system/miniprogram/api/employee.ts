import { get, post, put, del } from '../utils/request'
import type { Employee, PageResult } from '../types'

export const employeeApi = {
  getList: (params?: Record<string, any>) => {
    console.log('[Employee] Get list:', params)
    return get<PageResult<Employee>>('/employees', params)
  },
  
  getById: (id: number) => {
    console.log('[Employee] Get by id:', id)
    return get<Employee>(`/employees/${id}`)
  },
  
  add: (data: Record<string, any>) => {
    console.log('[Employee] Add')
    return post<Employee>('/employees', data, true)
  },
  
  update: (id: number, data: Record<string, any>) => {
    console.log('[Employee] Update:', id)
    return put<Employee>(`/employees/${id}`, data, true)
  },
  
  delete: (id: number) => {
    console.log('[Employee] Delete:', id)
    return del<void>(`/employees/${id}`)
  },
  
  search: (params?: Record<string, any>) => {
    console.log('[Employee] Search:', params)
    return get<Employee[]>('/employees/search', params)
  }
}

export default employeeApi
