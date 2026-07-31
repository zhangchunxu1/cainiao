import { defineStore } from 'pinia'
import { getEmployees, getEmployeeById, addEmployee, updateEmployee, deleteEmployee, batchDeleteEmployees } from '../api/employee'

export const useEmployeeStore = defineStore('employee', {
  state: () => ({
    employees: {
      records: [],
      total: 0,
      size: 10,
      current: 1,
      pages: 1
    },
    currentEmployee: null,
    loading: false,
    error: null
  }),
  
  actions: {
    async fetchEmployees(params = {}) {
      this.loading = true
      try {
        const requestParams = {
          page: params.page || this.employees.current,
          pageSize: params.pageSize || this.employees.size,
          keyword: params.keyword
        }
        const res = await getEmployees(requestParams)
        if (res.data.success && res.data.code === 200) {
          const data = res.data.data
          this.employees = {
            records: data.records || [],
            total: data.total || 0,
            size: this.employees.size,
            current: data.current || this.employees.current,
            pages: data.pages || 1
          }
        } else {
          throw new Error(res.data.message || '获取数据失败')
        }
        this.error = null
      } catch (error) {
        this.error = error.message || '获取员工列表失败'
        console.error('获取员工列表失败:', error)
      } finally {
        this.loading = false
      }
    },
    
    async fetchEmployeeById(id) {
      this.loading = true
      try {
        const res = await getEmployeeById(id)
        if (res.data.success && res.data.code === 200) {
          this.currentEmployee = res.data.data
        } else {
          throw new Error(res.data.message || '获取数据失败')
        }
        this.error = null
        return this.currentEmployee
      } catch (error) {
        this.error = error.message || '获取员工详情失败'
        console.error('获取员工详情失败:', error)
        return null
      } finally {
        this.loading = false
      }
    },
    
    async createEmployee(employeeData) {
      this.loading = true
      try {
        const res = await addEmployee(employeeData)
        if (res.data.success && res.data.code === 200) {
          // 添加成功后重新获取列表
          await this.fetchEmployees({
            current: 1,
            size: this.employees.size
          })
          return true
        } else {
          throw new Error(res.data.message || '添加失败')
        }
      } catch (error) {
        this.error = error.message || '添加员工失败'
        console.error('添加员工失败:', error)
        return false
      } finally {
        this.loading = false
      }
    },
    
    async editEmployee(id, employeeData) {
      this.loading = true
      try {
        const res = await updateEmployee(id, employeeData)
        if (res.data.success && res.data.code === 200) {
          // 更新成功后重新获取列表
          await this.fetchEmployees({
            current: this.employees.current,
            size: this.employees.size
          })
          return true
        } else {
          throw new Error(res.data.message || '更新失败')
        }
      } catch (error) {
        this.error = error.message || '更新员工信息失败'
        console.error('更新员工信息失败:', error)
        return false
      } finally {
        this.loading = false
      }
    },
    
    async removeEmployee(id) {
      this.loading = true
      try {
        const res = await deleteEmployee(id)
        if (res.data.success && res.data.code === 200) {
          // 删除成功后重新获取列表
          await this.fetchEmployees({
            current: this.employees.records.length === 1 && this.employees.current > 1 
              ? this.employees.current - 1 
              : this.employees.current,
            size: this.employees.size
          })
          return true
        } else {
          throw new Error(res.data.message || '删除失败')
        }
      } catch (error) {
        this.error = error.message || '删除员工失败'
        console.error('删除员工失败:', error)
        return false
      } finally {
        this.loading = false
      }
    },
    
    async batchRemoveEmployees(ids) {
      this.loading = true
      try {
        const res = await batchDeleteEmployees(ids)
        if (res.data.success && res.data.code === 200) {
          await this.fetchEmployees({
            current: this.employees.current,
            size: this.employees.size
          })
          return res.data.data
        } else {
          throw new Error(res.data.message || '批量删除失败')
        }
      } catch (error) {
        this.error = error.message || '批量删除员工失败'
        console.error('批量删除员工失败:', error)
        throw error
      } finally {
        this.loading = false
      }
    }
  }
}) 