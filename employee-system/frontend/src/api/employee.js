import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 获取员工列表
export const getEmployees = (params) => {
  return api.get('/employees', { params })
}

// 获取单个员工信息
export const getEmployeeById = (id) => {
  return api.get(`/employees/${id}`)
}

// 添加员工
export const addEmployee = (data) => {
  return api.post('/employees', data)
}

// 更新员工信息
export const updateEmployee = (id, data) => {
  return api.put(`/employees/${id}`, data)
}

// 删除员工
export const deleteEmployee = (id) => {
  return api.delete(`/employees/${id}`)
}

// 请求拦截器
api.interceptors.request.use(
  config => {
    // 可以在这里添加认证token等
    return config
  },
  error => Promise.reject(error)
)

// 响应拦截器
api.interceptors.response.use(
  response => {
    // 处理SpringBoot后端返回的标准格式数据
    if (response.data && response.data.code === 200) {
      return response
    } else if (response.data && response.data.code !== 200) {
      console.error('业务错误:', response.data.message)
      return Promise.reject(new Error(response.data.message || '操作失败'))
    }
    return response
  },
  error => {
    if (error.response) {
      // 处理服务器响应错误
      console.error('响应错误:', error.response.data)
      const message = error.response.data?.message || '服务器错误'
      return Promise.reject(new Error(message))
    } else if (error.request) {
      // 处理请求错误
      console.error('请求错误:', error.request)
      return Promise.reject(new Error('网络请求失败，请检查网络连接'))
    } else {
      // 处理其他错误
      console.error('错误:', error.message)
      return Promise.reject(error)
    }
  }
)

export default api 