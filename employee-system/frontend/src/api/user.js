import api from './employee'

export const userApi = {
  getUserList: (params) => api.get('/users', { params }),
  getUserById: (id) => api.get(`/users/${id}`),
  addUser: (data) => api.post('/users', data),
  updateUser: (id, data) => api.put(`/users/${id}`, data),
  deleteUser: (id) => api.delete(`/users/${id}`),
  resetPassword: (id) => api.put(`/users/${id}/reset-password`)
}
