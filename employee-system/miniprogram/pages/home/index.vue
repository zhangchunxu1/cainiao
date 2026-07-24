<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { onPullDownRefresh } from '@dcloudio/uni-app'
import { useAuthStore } from '../../store/auth'
import { attendanceApi } from '../../api/attendance'
import { announcementApi } from '../../api/announcement'
import { leaveApi } from '../../api/leave'
import { formatDate, getStatusText, getWeekday } from '../../utils'
import type { Announcement, Attendance, LeaveRequest } from '../../types'

const authStore = useAuthStore()
const todayAttendance = ref<Attendance | null>(null)
const announcements = ref<Announcement[]>([])
const pendingLeaves = ref<LeaveRequest[]>([])
const currentTime = ref('')
const loading = ref(false)

const todayDate = computed(() => formatDate(new Date()))
const weekday = computed(() => getWeekday(new Date()))
const firstName = computed(() => authStore.realName?.charAt(0) || '员')
const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 9) return '早上好'
  if (hour < 12) return '上午好'
  if (hour < 14) return '中午好'
  if (hour < 18) return '下午好'
  return '晚上好'
})

const quickActions = [
  { mark: '假', title: '请假申请', desc: '提交休假流程', path: '/pages/leave/apply', type: 'navigate' },
  { mark: '报', title: '费用报销', desc: '录入报销事项', path: '/pages/reimbursement/apply', type: 'navigate' },
  { mark: '日', title: '提交日报', desc: '记录今日工作', path: '/pages/dailyReport/submit', type: 'navigate' },
  { mark: '勤', title: '考勤打卡', desc: '查看月度考勤', path: '/pages/attendance/index', type: 'tab' }
]

const ensureLogin = () => {
  if (authStore.isLoggedIn) return true
  uni.reLaunch({ url: '/pages/login/index' })
  return false
}

const updateTime = () => {
  currentTime.value = new Date().toLocaleTimeString('zh-CN', { hour12: false })
}

const loadData = async () => {
  if (!ensureLogin()) return
  loading.value = true
  try {
    const [attendance, announcementPage, leavePage] = await Promise.all([
      attendanceApi.getToday(),
      announcementApi.getList({ current: 1, size: 5 }),
      leaveApi.getList({ current: 1, size: 3, status: 'PENDING' })
    ])
    todayAttendance.value = attendance
    announcements.value = announcementPage.records || []
    pendingLeaves.value = leavePage.records || []
  } catch (error) {
    console.error('[Home] Load data error:', error)
  } finally {
    loading.value = false
    uni.stopPullDownRefresh()
  }
}

const handleCheckIn = async () => {
  try {
    todayAttendance.value = await attendanceApi.checkIn()
    uni.showToast({ title: '签到成功', icon: 'success' })
  } catch (error) {
    console.error('[Home] Check in error:', error)
  }
}

const handleCheckOut = async () => {
  try {
    todayAttendance.value = await attendanceApi.checkOut()
    uni.showToast({ title: '签退成功', icon: 'success' })
  } catch (error) {
    console.error('[Home] Check out error:', error)
  }
}

const goToAction = (item: typeof quickActions[number]) => {
  if (item.type === 'tab') {
    uni.switchTab({ url: item.path })
    return
  }
  uni.navigateTo({ url: item.path })
}

const goToAnnouncement = (id: number) => {
  if (!id) return
  uni.navigateTo({ url: `/pages/announcement/detail?id=${id}` })
}

onMounted(() => {
  updateTime()
  setInterval(updateTime, 1000)
  loadData()
})

onPullDownRefresh(loadData)
</script>

<template>
  <view class="page-shell with-tabbar">
    <view class="hero-panel home-hero">
      <view class="profile-row">
        <view class="avatar">{{ firstName }}</view>
        <view class="profile-text">
          <view class="hero-kicker">{{ todayDate }} {{ weekday }}</view>
          <view class="hero-title">{{ greeting }}，{{ authStore.realName || '员工' }}</view>
          <view class="hero-subtitle">{{ authStore.department || '员工管理系统' }}</view>
        </view>
      </view>
    </view>

    <view class="section-card attendance-card">
      <view class="section-header">
        <view>
          <view class="section-title">今日考勤</view>
          <view class="muted-text">当前时间 {{ currentTime }}</view>
        </view>
        <view v-if="todayAttendance?.status" class="status-pill" :class="todayAttendance.status.toLowerCase()">
          {{ getStatusText(todayAttendance.status) }}
        </view>
      </view>

      <view class="metric-grid">
        <view class="metric-item">
          <view class="metric-label">上班签到</view>
          <view class="metric-value">{{ todayAttendance?.checkInTime || '--:--' }}</view>
        </view>
        <view class="metric-item">
          <view class="metric-label">下班签退</view>
          <view class="metric-value">{{ todayAttendance?.checkOutTime || '--:--' }}</view>
        </view>
      </view>

      <view class="button-row attendance-actions">
        <button class="primary-button" :disabled="!!todayAttendance?.checkInTime" @click="handleCheckIn">签到</button>
        <button class="secondary-button" :disabled="!todayAttendance?.checkInTime || !!todayAttendance?.checkOutTime" @click="handleCheckOut">签退</button>
      </view>
    </view>

    <view class="section-card">
      <view class="section-header">
        <view class="section-title">快捷入口</view>
      </view>
      <view class="quick-grid">
        <view v-for="item in quickActions" :key="item.title" class="quick-item" @click="goToAction(item)">
          <view class="quick-mark">{{ item.mark }}</view>
          <view class="quick-title">{{ item.title }}</view>
          <view class="quick-desc">{{ item.desc }}</view>
        </view>
      </view>
    </view>

    <view class="section-card">
      <view class="section-header">
        <view class="section-title">公告通知</view>
        <view class="section-action">最近 {{ announcements.length }} 条</view>
      </view>
      <view v-if="announcements.length">
        <view v-for="item in announcements" :key="item.id" class="list-row" @click="goToAnnouncement(item.id)">
          <view class="list-title">{{ item.title }}</view>
          <view class="list-meta">{{ item.author || '系统公告' }} · {{ formatDate(item.createTime) }}</view>
        </view>
      </view>
      <view v-else class="empty-state">{{ loading ? '正在加载公告...' : '暂无公告' }}</view>
    </view>

    <view class="section-card" v-if="pendingLeaves.length">
      <view class="section-header">
        <view class="section-title">待处理请假</view>
        <view class="status-pill pending">{{ pendingLeaves.length }}</view>
      </view>
      <view v-for="item in pendingLeaves" :key="item.id" class="list-row" @click="uni.navigateTo({ url: `/pages/leave/detail?id=${item.id}` })">
        <view class="list-title">{{ item.employeeName }} · {{ item.leaveType }}</view>
        <view class="list-meta">{{ item.startDate }} 至 {{ item.endDate }}，共 {{ item.days }} 天</view>
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.home-hero {
  border-radius: 0 0 24rpx 24rpx;
}

.profile-row {
  display: flex;
  align-items: center;
  gap: 22rpx;
}

.avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 92rpx;
  height: 92rpx;
  flex-shrink: 0;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
  font-size: 40rpx;
  font-weight: 800;
}

.profile-text {
  min-width: 0;
}

.attendance-card {
  margin-top: -18rpx;
}

.attendance-actions {
  margin-top: 22rpx;
}

.quick-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16rpx;
}

.quick-item {
  min-height: 154rpx;
  padding: 22rpx;
  border-radius: 14rpx;
  background: #f7f9fc;
}

.quick-mark {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 52rpx;
  height: 52rpx;
  margin-bottom: 14rpx;
  border-radius: 12rpx;
  background: rgba(22, 119, 255, 0.12);
  color: $color-primary;
  font-weight: 800;
}

.quick-title {
  color: $color-text-primary;
  font-size: 28rpx;
  font-weight: 800;
}

.quick-desc {
  margin-top: 6rpx;
  color: $color-text-tertiary;
  font-size: 23rpx;
}
</style>
