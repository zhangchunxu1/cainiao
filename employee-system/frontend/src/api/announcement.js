import api from './employee'

export const announcementApi = {
  getAnnouncementList: (params) => api.get('/announcements', { params }),
  getAnnouncementById: (id) => api.get(`/announcements/${id}`),
  addAnnouncement: (data) => api.post('/announcements', data),
  updateAnnouncement: (id, data) => api.put(`/announcements/${id}`, data),
  deleteAnnouncement: (id) => api.delete(`/announcements/${id}`)
}