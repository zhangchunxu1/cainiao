export interface LoginRequest {
  username: string
  password: string
}

export interface LoginResponse {
  token: string
  userId: string | number
  role: string
  realName: string
  department?: string
  username: string
}

export interface UserInfo {
  userId: string | number
  username: string
  realName: string
  role: string
  department?: string
  token: string
}

export interface ApiResponse<T = any> {
  success: boolean
  code: number
  message: string
  data: T
}

export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

export interface Attendance {
  id: number
  employeeId: number
  employeeName: string
  date: string
  checkInTime: string
  checkOutTime: string
  workHours: string
  status: string
  remark?: string
  createTime: string
}

export interface LeaveRequest {
  id: number
  employeeId: number
  employeeName: string
  department: string
  leaveType: string
  startDate: string
  endDate: string
  days: number
  reason: string
  status: string
  approver?: string
  approvalComment?: string
  approvalTime?: string
  createTime: string
}

export interface Announcement {
  id: number
  title: string
  content: string
  author?: string
  createTime: string
  updateTime?: string
}

export interface DailyReport {
  id: number
  employeeId: number
  employeeName: string
  department?: string
  reportDate: string
  content: string
  status: string
  reviewer?: string
  reviewComment?: string
  reviewTime?: string
  createTime: string
}

export interface Reimbursement {
  id: number
  employeeId: number
  employeeName: string
  department: string
  type: string
  amount: number
  reason: string
  status: string
  managerApprover?: string
  managerComment?: string
  managerApproveTime?: string
  financeApprover?: string
  financeComment?: string
  financeApproveTime?: string
  createTime: string
}

export interface SalarySlip {
  id: number
  employeeId: number
  employeeName: string
  department: string
  month: string
  basicSalary: number
  performance: number
  bonus: number
  allowance: number
  overtimePay: number
  socialInsurance: number
  housingFund: number
  personalTax: number
  otherDeductions: number
  netSalary: number
  createTime: string
}

export interface Employee {
  id: number
  name: string
  department: string
  position: string
  phone?: string
  email?: string
  entryDate?: string
  gender?: string
}
