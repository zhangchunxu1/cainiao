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
  (config) => {
    const authStore = useAuthStore()
    if (authStore.token) {
      config.headers.Authorization = `Bearer ${authStore.token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

api.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    if (error.response && error.response.status === 401) {
      const authStore = useAuthStore()
      authStore.logout()
      router.push('/login')
      message.error('登录已过期，请重新登录')
    } else {
      message.error(error.response?.data?.message || error.message || '请求失败')
    }
    return Promise.reject(error)
  }
)

export const getReimbursements = (params) => api.get('/reimbursements', { params })
export const getReimbursementById = (id) => api.get(`/reimbursements/${id}`)
export const createReimbursement = (data) => api.post('/reimbursements', data)
export const updateReimbursement = (id, data) => api.put(`/reimbursements/${id}`, data)
export const deleteReimbursement = (id) => api.delete(`/reimbursements/${id}`)
export const batchDeleteReimbursements = (ids) => api.delete('/reimbursements/batch', { data: ids })
export const managerApprove = (id, data) => api.post(`/reimbursements/${id}/manager-approve`, data)
export const managerReject = (id, data) => api.post(`/reimbursements/${id}/manager-reject`, data)
export const financeApprove = (id, data) => api.post(`/reimbursements/${id}/finance-approve`, data)
export const financeReject = (id, data) => api.post(`/reimbursements/${id}/finance-reject`, data)

export const reimbursementApi = {
  getReimbursements,
  getReimbursementById,
  createReimbursement,
  updateReimbursement,
  deleteReimbursement,
  batchDeleteReimbursements,
  managerApprove,
  managerReject,
  financeApprove,
  financeReject
}