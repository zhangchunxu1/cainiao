import api from './employee'

export const contractApi = {
  getContractList(params) {
    return api.get('/contracts', { params })
  },

  getContractById(id) {
    return api.get(`/contracts/${id}`)
  },

  addContract(data) {
    return api.post('/contracts', data)
  },

  updateContract(id, data) {
    return api.put(`/contracts/${id}`, data)
  },

  deleteContract(id) {
    return api.delete(`/contracts/${id}`)
  }
}