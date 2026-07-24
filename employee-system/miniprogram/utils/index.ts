import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn'

dayjs.locale('zh-cn')

export const formatDate = (date: string | Date, format: string = 'YYYY-MM-DD'): string => {
  if (!date) return ''
  return dayjs(date).format(format)
}

export const formatDateTime = (date: string | Date, format: string = 'YYYY-MM-DD HH:mm:ss'): string => {
  if (!date) return ''
  return dayjs(date).format(format)
}

export const formatTime = (date: string | Date, format: string = 'HH:mm:ss'): string => {
  if (!date) return ''
  return dayjs(date).format(format)
}

export const getToday = (): string => {
  return dayjs().format('YYYY-MM-DD')
}

export const getNowTime = (): string => {
  return dayjs().format('HH:mm:ss')
}

export const getWeekday = (date: string | Date): string => {
  const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  return weekdays[dayjs(date).day()]
}

export const getMonthDays = (year: number, month: number): number => {
  return dayjs(`${year}-${month}-01`).daysInMonth()
}

export const getStatusText = (status = ''): string => {
  const statusMap: Record<string, string> = {
    PENDING: '待审批',
    pending: '待审批',
    APPROVED: '已通过',
    approved: '已通过',
    REJECTED: '已拒绝',
    rejected: '已拒绝',
    NORMAL: '正常',
    normal: '正常',
    LATE: '迟到',
    late: '迟到',
    EARLY: '早退',
    early: '早退',
    ABSENT: '缺勤',
    absent: '缺勤',
    DRAFT: '草稿',
    draft: '草稿'
  }
  return statusMap[status] || status || '-'
}

export const getStatusClass = (status = ''): string => {
  const value = status.toLowerCase()
  if (value.includes('approved') || value.includes('normal')) return 'approved'
  if (value.includes('rejected') || value.includes('early') || value.includes('absent')) return 'rejected'
  if (value.includes('late')) return 'late'
  return 'pending'
}

export const leaveTypes = [
  { value: '事假', label: '事假' },
  { value: '病假', label: '病假' },
  { value: '年假', label: '年假' },
  { value: '婚假', label: '婚假' },
  { value: '产假', label: '产假' },
  { value: '陪产假', label: '陪产假' },
  { value: '丧假', label: '丧假' },
  { value: '调休', label: '调休' }
]

export const reimbursementTypes = [
  { value: '差旅费', label: '差旅费' },
  { value: '招待费', label: '招待费' },
  { value: '办公费', label: '办公费' },
  { value: '交通费', label: '交通费' },
  { value: '通讯费', label: '通讯费' },
  { value: '培训费', label: '培训费' },
  { value: '其他', label: '其他' }
]

export const formatAmount = (amount?: number): string => {
  return Number(amount || 0).toFixed(2)
}

export default {
  formatDate,
  formatDateTime,
  formatTime,
  getToday,
  getNowTime,
  getWeekday,
  getMonthDays,
  getStatusText,
  getStatusClass,
  leaveTypes,
  reimbursementTypes,
  formatAmount
}
