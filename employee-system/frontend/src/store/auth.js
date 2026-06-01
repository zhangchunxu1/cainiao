import { defineStore } from 'pinia'
import { login as loginApi } from '../api/auth'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    username: localStorage.getItem('username') || '',
    userId: localStorage.getItem('userId') || '',
    role: localStorage.getItem('role') || '',
    realName: localStorage.getItem('realName') || ''
  }),
  
  getters: {
    isLoggedIn: (state) => !!state.token,
    isAdmin: (state) => state.role === 'admin'
  },
  
  actions: {
    async login(credentials) {
      try {
        const res = await loginApi(credentials)
        if (res.data.success && res.data.code === 200) {
          const data = res.data.data
          this.token = data.token
          this.username = credentials.username
          this.userId = data.userId
          this.role = data.role
          this.realName = data.realName
          
          localStorage.setItem('token', data.token)
          localStorage.setItem('username', credentials.username)
          localStorage.setItem('userId', data.userId)
          localStorage.setItem('role', data.role)
          localStorage.setItem('realName', data.realName)
          return true
        } else {
          throw new Error(res.data.message || '登录失败')
        }
      } catch (error) {
        throw error
      }
    },
    
    logout() {
      this.token = ''
      this.username = ''
      this.userId = ''
      this.role = ''
      this.realName = ''
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      localStorage.removeItem('userId')
      localStorage.removeItem('role')
      localStorage.removeItem('realName')
    }
  }
})