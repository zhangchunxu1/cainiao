import { defineStore } from 'pinia'
import { userApi } from '../api/user'

export const useUserStore = defineStore('userManage', {
  state: () => ({
    users: {
      records: [],
      total: 0,
      size: 10,
      current: 1,
      pages: 1
    },
    loading: false,
    error: null
  }),

  actions: {
    async fetchUsers(params = {}) {
      this.loading = true
      try {
        const res = await userApi.getUserList(params)
        if (res.data.success && res.data.code === 200) {
          this.users = res.data.data
        } else {
          throw new Error(res.data.message || '获取数据失败')
        }
        this.error = null
      } catch (error) {
        this.error = error.message || '获取用户列表失败'
      } finally {
        this.loading = false
      }
    },

    async createUser(userData) {
      this.loading = true
      try {
        const res = await userApi.addUser(userData)
        if (res.data.success && res.data.code === 200) {
          await this.fetchUsers({ page: 1, pageSize: this.users.size })
          return true
        }
        throw new Error(res.data.message || '添加失败')
      } catch (error) {
        throw error
      } finally {
        this.loading = false
      }
    },

    async editUser(id, userData) {
      this.loading = true
      try {
        const res = await userApi.updateUser(id, userData)
        if (res.data.success && res.data.code === 200) {
          await this.fetchUsers({ page: this.users.current, pageSize: this.users.size })
          return true
        }
        throw new Error(res.data.message || '更新失败')
      } catch (error) {
        throw error
      } finally {
        this.loading = false
      }
    },

    async removeUser(id) {
      try {
        const res = await userApi.deleteUser(id)
        if (res.data.success && res.data.code === 200) {
          await this.fetchUsers({
            page: this.users.records.length === 1 && this.users.current > 1 ? this.users.current - 1 : this.users.current,
            pageSize: this.users.size
          })
          return true
        }
        throw new Error(res.data.message || '删除失败')
      } catch (error) {
        throw error
      }
    }
  }
})
