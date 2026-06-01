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
  error => {
    return Promise.reject(error)
  }
)

api.interceptors.response.use(
  response => {
    return response
  },
  error => {
    if (error.response && error.response.status === 401) {
      const authStore = useAuthStore()
      authStore.logout()
      message.error('登录已过期，请重新登录')
      router.push('/login')
    } else if (error.response && error.response.status === 403) {
      message.error('没有权限访问')
    }
    return Promise.reject(error)
  }
)

export const login = (data) => {
  return api.post('/auth/login', data)
}

export default api
