import { defineStore } from 'pinia'
import { login as loginApi } from '../api/auth'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    username: localStorage.getItem('username') || '',
    userId: localStorage.getItem('userId') || ''
  }),
  
  getters: {
    isLoggedIn: (state) => !!state.token
  },
  
  actions: {
    async login(credentials) {
      try {
        const res = await loginApi(credentials)
        if (res.data.success && res.data.code === 200) {
          const token = res.data.data.token
          this.token = token
          this.username = credentials.username
          
          localStorage.setItem('token', token)
          localStorage.setItem('username', credentials.username)
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
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      localStorage.removeItem('userId')
    }
  }
})