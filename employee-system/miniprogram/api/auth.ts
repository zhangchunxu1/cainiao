import { post } from '../utils/request'
import type { LoginRequest, LoginResponse } from '../types'

export const loginApi = {
  login: (data: LoginRequest) => {
    console.log('[Auth] Login request:', { username: data.username })
    return post<LoginResponse>('/auth/login', data)
  }
}

export default loginApi
