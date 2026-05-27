import api from './employee'

export const departmentApi = {
  getDepartmentList: (params) => api.get('/departments', { params }),
  getDepartmentById: (id) => api.get(`/departments/${id}`),
  addDepartment: (data) => api.post('/departments', data),
  updateDepartment: (id, data) => api.put(`/departments/${id}`, data),
  deleteDepartment: (id) => api.delete(`/departments/${id}`)
}