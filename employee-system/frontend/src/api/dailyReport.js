import api from './employee'

export const dailyReportApi = {
  getDailyReportList: (params) => api.get('/daily-reports', { params }),
  getDailyReportById: (id) => api.get(`/daily-reports/${id}`),
  submitDailyReport: (data) => api.post('/daily-reports', data),
  updateDailyReport: (id, data) => api.put(`/daily-reports/${id}`, data),
  deleteDailyReport: (id) => api.delete(`/daily-reports/${id}`),
  batchDeleteDailyReports: (ids) => api.delete('/daily-reports/batch', { data: ids }),
  reviewDailyReport: (id, data) => api.put(`/daily-reports/${id}/review`, data)
}