import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { loginApi } from '../api/auth'
import type { UserInfo, LoginRequest } from '../types'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string>('')
  const userId = ref<string | number>('')
  const username = ref<string>('')
  const realName = ref<string>('')
  const role = ref<string>('')
  const department = ref<string>('')

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => role.value && role.value.toLowerCase() === 'admin')
  const isManager = computed(() => role.value && role.value.toLowerCase() === 'manager')

  const initFromStorage = () => {
    try {
      const storedToken = uni.getStorageSync('token')
      const storedUserInfo = uni.getStorageSync('userInfo')
      
      if (storedToken) {
        token.value = storedToken
      }
      
      if (storedUserInfo) {
        const userInfo: UserInfo = JSON.parse(storedUserInfo)
        userId.value = userInfo.userId
        username.value = userInfo.username
        realName.value = userInfo.realName
        role.value = userInfo.role
        department.value = userInfo.department || ''
      }
      
      console.log('[Auth] Init from storage, isLoggedIn:', isLoggedIn.value)
    } catch (error) {
      console.error('[Auth] Init from storage failed:', error)
    }
  }

  const login = async (credentials: LoginRequest): Promise<boolean> => {
    try {
      console.log('[Auth] Login attempt:', credentials.username)
      const data = await loginApi.login(credentials)
      
      token.value = data.token
      userId.value = data.userId
      username.value = credentials.username
      realName.value = data.realName
      role.value = data.role
      department.value = data.department || ''
      
      const userInfo: UserInfo = {
        userId: data.userId,
        username: credentials.username,
        realName: data.realName,
        role: data.role,
        department: data.department || '',
        token: data.token
      }
      
      uni.setStorageSync('token', data.token)
      uni.setStorageSync('userInfo', JSON.stringify(userInfo))
      
      console.log('[Auth] Login success:', { userId: data.userId, role: data.role })
      return true
    } catch (error) {
      console.error('[Auth] Login failed:', error)
      throw error
    }
  }

  const logout = () => {
    console.log('[Auth] Logout')
    token.value = ''
    userId.value = ''
    username.value = ''
    realName.value = ''
    role.value = ''
    department.value = ''
    
    uni.removeStorageSync('token')
    uni.removeStorageSync('userInfo')
  }

  const hasRole = (roles: string[]): boolean => {
    if (!role.value) return false
    return roles.includes(role.value)
  }

  return {
    token,
    userId,
    username,
    realName,
    role,
    department,
    isLoggedIn,
    isAdmin,
    isManager,
    initFromStorage,
    login,
    logout,
    hasRole
  }
})
