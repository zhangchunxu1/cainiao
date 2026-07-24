const DEVTOOLS_BASE_URL = 'http://127.0.0.1:8080'
const DEVICE_BASE_URL = 'http://192.168.31.139:8080'

const getBaseUrl = () => {
  const systemInfo = uni.getSystemInfoSync()
  return systemInfo.platform === 'devtools' ? DEVTOOLS_BASE_URL : DEVICE_BASE_URL
}

export interface RequestOptions {
  url: string
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE'
  data?: any
  header?: Record<string, string>
  showLoading?: boolean
}

export interface ApiResult<T = any> {
  success: boolean
  code: number
  message: string
  data: T
}

const request = <T = any>(options: RequestOptions): Promise<T> => {
  return new Promise((resolve, reject) => {
    const token = uni.getStorageSync('token')
    
    const header: Record<string, string> = {
      'Content-Type': 'application/json',
      ...options.header
    }
    
    if (token) {
      header.Authorization = `Bearer ${token}`
    }
    
    if (options.showLoading) {
      uni.showLoading({ title: '加载中...' })
    }
    
    const requestUrl = getBaseUrl() + options.url
    console.log('[Request] URL:', requestUrl)
    
    uni.request({
      url: requestUrl,
      method: options.method || 'GET',
      data: options.data,
      header,
      success: (res) => {
        if (options.showLoading) {
          uni.hideLoading()
        }
        
        const result = res.data as ApiResult
        
        if (res.statusCode === 200) {
          if (result.success && result.code === 200) {
            resolve(result.data as T)
          } else {
            if (res.statusCode === 401 || result.code === 401) {
              handleUnauthorized()
            }
            uni.showToast({
              title: result.message || '请求失败',
              icon: 'none'
            })
            reject(new Error(result.message || '请求失败'))
          }
        } else if (res.statusCode === 401) {
          handleUnauthorized()
          reject(new Error('登录已过期'))
        } else if (res.statusCode === 403) {
          uni.showToast({
            title: '没有权限访问',
            icon: 'none'
          })
          reject(new Error('没有权限'))
        } else {
          uni.showToast({
            title: result.message || `请求失败(${res.statusCode})`,
            icon: 'none'
          })
          reject(new Error(result.message || '请求失败'))
        }
      },
      fail: (err) => {
        if (options.showLoading) {
          uni.hideLoading()
        }
        console.error('[Request] Failed:', err)
        uni.showToast({
          title: '网络连接失败',
          icon: 'none'
        })
        reject(err)
      }
    })
  })
}

const handleUnauthorized = () => {
  uni.removeStorageSync('token')
  uni.removeStorageSync('userInfo')
  uni.showToast({
    title: '登录已过期，请重新登录',
    icon: 'none'
  })
  setTimeout(() => {
    uni.reLaunch({ url: '/pages/login/index' })
  }, 1500)
}

export const get = <T = any>(url: string, data?: any, showLoading = false): Promise<T> => {
  return request<T>({ url, method: 'GET', data, showLoading })
}

export const post = <T = any>(url: string, data?: any, showLoading = false): Promise<T> => {
  return request<T>({ url, method: 'POST', data, showLoading })
}

export const put = <T = any>(url: string, data?: any, showLoading = false): Promise<T> => {
  return request<T>({ url, method: 'PUT', data, showLoading })
}

export const del = <T = any>(url: string, data?: any, showLoading = false): Promise<T> => {
  return request<T>({ url, method: 'DELETE', data, showLoading })
}

export default request
