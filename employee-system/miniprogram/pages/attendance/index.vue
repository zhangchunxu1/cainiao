<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { onPullDownRefresh } from '@dcloudio/uni-app'
import { useAuthStore } from '../../store/auth'
import { attendanceApi } from '../../api/attendance'
import { formatDate, getMonthDays, getStatusClass, getStatusText } from '../../utils'
import type { Attendance } from '../../types'

const authStore = useAuthStore()
const attendanceList = ref<Attendance[]>([])
const currentMonth = ref(new Date())
const currentTime = ref('')
const loading = ref(false)

const year = computed(() => currentMonth.value.getFullYear())
const month = computed(() => currentMonth.value.getMonth() + 1)
const monthText = computed(() => `${year.value}年${month.value}月`)
const daysInMonth = computed(() => getMonthDays(year.value, month.value))
const weekdays = ['日', '一', '二', '三', '四', '五', '六']

const calendarDays = computed(() => {
  const firstDay = new Date(year.value, month.value - 1, 1).getDay()
  const days: Array<{ day: number | null; date: string; data?: Attendance }> = []
  for (let i = 0; i < firstDay; i++) days.push({ day: null, date: '' })
  for (let day = 1; day <= daysInMonth.value; day++) {
    const date = `${year.value}-${String(month.value).padStart(2, '0')}-${String(day).padStart(2, '0')}`
    days.push({ day, date, data: attendanceList.value.find(item => item.date === date) })
  }
  return days
})

const todayRecord = computed(() => {
  return attendanceList.value.find(item => item.date === formatDate(new Date()))
})

const monthStats = computed(() => {
  const records = attendanceList.value
  return {
    normal: records.filter(item => ['NORMAL', 'normal'].includes(item.status)).length,
    late: records.filter(item => ['LATE', 'late'].includes(item.status)).length,
    early: records.filter(item => ['EARLY', 'early'].includes(item.status)).length,
    absent: records.filter(item => ['ABSENT', 'absent'].includes(item.status)).length
  }
})

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
    const data = await attendanceApi.getList({
      current: 1,
      size: 100,
      month: `${year.value}-${String(month.value).padStart(2, '0')}`
    })
    attendanceList.value = data.records || []
  } catch (error) {
    console.error('[Attendance] Load error:', error)
  } finally {
    loading.value = false
    uni.stopPullDownRefresh()
  }
}

const shiftMonth = (offset: number) => {
  const next = new Date(currentMonth.value)
  next.setMonth(next.getMonth() + offset)
  currentMonth.value = next
  loadData()
}

const handleCheckIn = async () => {
  try {
    await attendanceApi.checkIn()
    uni.showToast({ title: '签到成功', icon: 'success' })
    loadData()
  } catch (error) {
    console.error('[Attendance] Check in error:', error)
  }
}

const handleCheckOut = async () => {
  try {
    await attendanceApi.checkOut()
    uni.showToast({ title: '签退成功', icon: 'success' })
    loadData()
  } catch (error) {
    console.error('[Attendance] Check out error:', error)
  }
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
    <view class="hero-panel">
      <view class="hero-kicker">实时考勤</view>
      <view class="hero-title">{{ currentTime }}</view>
      <view class="hero-subtitle">{{ formatDate(new Date()) }} · 今日打卡状态</view>
    </view>

    <view class="section-card today-card">
      <view class="section-header">
        <view class="section-title">今日记录</view>
        <view v-if="todayRecord?.status" class="status-pill" :class="getStatusClass(todayRecord.status)">
          {{ getStatusText(todayRecord.status) }}
        </view>
      </view>
      <view class="metric-grid">
        <view class="metric-item">
          <view class="metric-label">签到</view>
          <view class="metric-value">{{ todayRecord?.checkInTime || '--:--' }}</view>
        </view>
        <view class="metric-item">
          <view class="metric-label">签退</view>
          <view class="metric-value">{{ todayRecord?.checkOutTime || '--:--' }}</view>
        </view>
      </view>
      <view class="button-row action-row">
        <button class="primary-button" :disabled="!!todayRecord?.checkInTime" @click="handleCheckIn">签到</button>
        <button class="secondary-button" :disabled="!todayRecord?.checkInTime || !!todayRecord?.checkOutTime" @click="handleCheckOut">签退</button>
      </view>
    </view>

    <view class="section-card">
      <view class="section-header">
        <view class="section-title">{{ monthText }}统计</view>
        <view class="month-switch">
          <text @click="shiftMonth(-1)">上月</text>
          <text @click="shiftMonth(1)">下月</text>
        </view>
      </view>
      <view class="stats-row">
        <view class="stat-cell normal"><text>{{ monthStats.normal }}</text><view>正常</view></view>
        <view class="stat-cell late"><text>{{ monthStats.late }}</text><view>迟到</view></view>
        <view class="stat-cell early"><text>{{ monthStats.early }}</text><view>早退</view></view>
        <view class="stat-cell absent"><text>{{ monthStats.absent }}</text><view>缺勤</view></view>
      </view>
    </view>

    <view class="section-card">
      <view class="section-header">
        <view class="section-title">月度日历</view>
        <view class="muted-text">{{ loading ? '更新中' : '按日期查看' }}</view>
      </view>
      <view class="weekday-row">
        <view v-for="day in weekdays" :key="day">{{ day }}</view>
      </view>
      <view class="calendar-grid">
        <view v-for="(item, index) in calendarDays" :key="index" class="calendar-day" :class="{ empty: !item.day, today: item.date === formatDate(new Date()) }">
          <text>{{ item.day || '' }}</text>
          <view v-if="item.data" class="day-dot" :class="getStatusClass(item.data.status)"></view>
        </view>
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.today-card {
  margin-top: -18rpx;
}

.action-row {
  margin-top: 22rpx;
}

.month-switch {
  display: flex;
  gap: 22rpx;
  color: $color-primary;
  font-size: 25rpx;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12rpx;
}

.stat-cell {
  padding: 18rpx 8rpx;
  border-radius: 12rpx;
  background: #f7f9fc;
  text-align: center;

  text {
    display: block;
    font-size: 36rpx;
    font-weight: 800;
  }

  view {
    margin-top: 8rpx;
    color: $color-text-tertiary;
    font-size: 23rpx;
  }
}

.stat-cell.normal text { color: $color-success; }
.stat-cell.late text { color: $color-warning; }
.stat-cell.early text,
.stat-cell.absent text { color: $color-error; }

.weekday-row,
.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, minmax(0, 1fr));
}

.weekday-row view {
  padding: 14rpx 0;
  color: $color-text-tertiary;
  font-size: 24rpx;
  text-align: center;
}

.calendar-day {
  position: relative;
  min-height: 78rpx;
  padding-top: 14rpx;
  border-radius: 12rpx;
  text-align: center;

  text {
    color: $color-text-primary;
    font-size: 26rpx;
  }
}

.calendar-day.today {
  background: rgba(22, 119, 255, 0.1);
}

.calendar-day.empty {
  visibility: hidden;
}

.day-dot {
  width: 10rpx;
  height: 10rpx;
  margin: 8rpx auto 0;
  border-radius: 50%;
}

.day-dot.approved { background: $color-success; }
.day-dot.pending,
.day-dot.late { background: $color-warning; }
.day-dot.rejected { background: $color-error; }
</style>
