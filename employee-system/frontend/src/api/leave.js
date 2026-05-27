import api from './employee'

export const leaveRequestApi = {
  getLeaveRequestList: (params) => api.get('/leaves', { params }),
  getLeaveRequestById: (id) => api.get(`/leaves/${id}`),
  submitLeaveRequest: (data) => api.post('/leaves', data),
  approveLeave: (id, data) => api.put(`/leaves/${id}/approve`, data),
  rejectLeave: (id, data) => api.put(`/leaves/${id}/reject`, data),
  deleteLeaveRequest: (id) => api.delete(`/leaves/${id}`)
}