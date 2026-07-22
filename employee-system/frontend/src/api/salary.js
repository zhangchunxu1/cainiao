import api from './employee'

export const salaryApi = {
  getSalarySlips: (params) => api.get('/salary-slips', { params }),
  getSalarySlip: (id) => api.get(`/salary-slips/${id}`),
  createSalarySlip: (data) => api.post('/salary-slips', data),
  updateSalarySlip: (id, data) => api.put(`/salary-slips/${id}`, data),
  deleteSalarySlip: (id) => api.delete(`/salary-slips/${id}`)
}