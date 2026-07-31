import axios from 'axios'
import { message } from 'ant-design-vue'
import router from '../router'
import { useAuthStore } from '../store/auth'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

api.interceptors.response.use(
  response => {
    return response
  },
  error => {
    if (error.response) {
      const status = error.response.status
      if (status === 401) {
        const authStore = useAuthStore()
        authStore.logout()
        message.error('登录已过期，请重新登录')
        router.push('/login')
        return Promise.reject(new Error('登录过期'))
      } else if (status === 403) {
        message.error('没有权限访问此功能')
        return Promise.reject(new Error('没有权限'))
      } else {
        const msg = error.response.data?.message || '服务器错误'
        return Promise.reject(new Error(msg))
      }
    } else if (error.request) {
      return Promise.reject(new Error('网络连接失败，请检查网络'))
    }
    return Promise.reject(error)
  }
)

export const getEmployees = (params) => api.get('/employees', { params })
export const getEmployeeById = (id) => api.get(`/employees/${id}`)
export const addEmployee = (data) => api.post('/employees', data)
export const updateEmployee = (id, data) => api.put(`/employees/${id}`, data)
export const deleteEmployee = (id) => api.delete(`/employees/${id}`)
export const batchDeleteEmployees = (ids) => api.delete('/employees/batch', { data: ids })
export const searchEmployees = (params) => api.get('/employees/search', { params })

export default api
