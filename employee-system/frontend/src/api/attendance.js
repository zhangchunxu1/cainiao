import api from './employee'

export const attendanceApi = {
  getAttendanceList: (params) => api.get('/attendance', { params }),
  checkIn: (data) => api.post('/attendance/checkin', data),
  checkOut: (data) => api.post('/attendance/checkout', data)
}