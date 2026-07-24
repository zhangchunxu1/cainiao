<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { onPullDownRefresh } from '@dcloudio/uni-app'
import { useAuthStore } from '../../store/auth'
import { leaveApi } from '../../api/leave'
import { reimbursementApi } from '../../api/reimbursement'
import { dailyReportApi } from '../../api/dailyReport'
import { formatAmount, formatDate, getStatusClass, getStatusText } from '../../utils'
import type { DailyReport, LeaveRequest, Reimbursement } from '../../types'

const authStore = useAuthStore()
const activeTab = ref(0)
const loading = ref(false)
const leaveList = ref<LeaveRequest[]>([])
const reimbursementList = ref<Reimbursement[]>([])
const dailyReportList = ref<DailyReport[]>([])

const tabs = ['请假', '报销', '日报']
const currentList = computed(() => {
  if (activeTab.value === 0) return leaveList.value
  if (activeTab.value === 1) return reimbursementList.value
  return dailyReportList.value
})

const ensureLogin = () => {
  if (authStore.isLoggedIn) return true
  uni.reLaunch({ url: '/pages/login/index' })
  return false
}

const loadData = async () => {
  if (!ensureLogin()) return
  loading.value = true
  try {
    const [leaves, reimbursements, reports] = await Promise.all([
      leaveApi.getList({ current: 1, size: 20 }),
      reimbursementApi.getList({ current: 1, size: 20 }),
      dailyReportApi.getList({ current: 1, size: 20 })
    ])
    leaveList.value = leaves.records || []
    reimbursementList.value = reimbursements.records || []
    dailyReportList.value = reports.records || []
  } catch (error) {
    console.error('[Apply] Load data error:', error)
  } finally {
    loading.value = false
    uni.stopPullDownRefresh()
  }
}

const goCreate = (type: number) => {
  const paths = ['/pages/leave/apply', '/pages/reimbursement/apply', '/pages/dailyReport/submit']
  uni.navigateTo({ url: paths[type] })
}

const goDetail = (type: number, id: number) => {
  const paths = [
    `/pages/leave/detail?id=${id}`,
    `/pages/reimbursement/detail?id=${id}`,
    `/pages/dailyReport/detail?id=${id}`
  ]
  uni.navigateTo({ url: paths[type] })
}

onMounted(loadData)
onPullDownRefresh(loadData)
</script>

<template>
  <view class="page-shell with-tabbar">
    <view class="hero-panel">
      <view class="hero-kicker">流程中心</view>
      <view class="hero-title">申请与审批记录</view>
      <view class="hero-subtitle">查看请假、报销、日报的提交状态</view>
    </view>

    <view class="section-card quick-card">
      <view class="section-header">
        <view class="section-title">发起申请</view>
      </view>
      <view class="quick-actions">
        <view class="quick-action" @click="goCreate(0)"><text>假</text><view>请假申请</view></view>
        <view class="quick-action" @click="goCreate(1)"><text>报</text><view>费用报销</view></view>
        <view class="quick-action" @click="goCreate(2)"><text>日</text><view>提交日报</view></view>
      </view>
    </view>

    <view class="section-card">
      <view class="tabs">
        <view v-for="(tab, index) in tabs" :key="tab" class="tab" :class="{ active: activeTab === index }" @click="activeTab = index">
          {{ tab }}
        </view>
      </view>

      <view v-if="currentList.length">
        <view v-for="item in leaveList" v-show="activeTab === 0" :key="`leave-${item.id}`" class="record-row" @click="goDetail(0, item.id)">
          <view>
            <view class="list-title">{{ item.leaveType }} · {{ item.days }}天</view>
            <view class="list-meta">{{ item.startDate }} 至 {{ item.endDate }}</view>
          </view>
          <view class="status-pill" :class="getStatusClass(item.status)">{{ getStatusText(item.status) }}</view>
        </view>

        <view v-for="item in reimbursementList" v-show="activeTab === 1" :key="`reim-${item.id}`" class="record-row" @click="goDetail(1, item.id)">
          <view>
            <view class="list-title">{{ item.type }} · ¥{{ formatAmount(item.amount) }}</view>
            <view class="list-meta">{{ formatDate(item.createTime) }} · {{ item.reason }}</view>
          </view>
          <view class="status-pill" :class="getStatusClass(item.status)">{{ getStatusText(item.status) }}</view>
        </view>

        <view v-for="item in dailyReportList" v-show="activeTab === 2" :key="`report-${item.id}`" class="record-row" @click="goDetail(2, item.id)">
          <view>
            <view class="list-title">{{ item.reportDate }} 日报</view>
            <view class="list-meta">{{ item.content }}</view>
          </view>
          <view class="status-pill" :class="getStatusClass(item.status)">{{ getStatusText(item.status) }}</view>
        </view>
      </view>

      <view v-else class="empty-state">{{ loading ? '正在加载记录...' : `暂无${tabs[activeTab]}记录` }}</view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.quick-card {
  margin-top: -18rpx;
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14rpx;
}

.quick-action {
  padding: 22rpx 8rpx;
  border-radius: 12rpx;
  background: #f7f9fc;
  text-align: center;

  text {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 54rpx;
    height: 54rpx;
    margin: 0 auto 12rpx;
    border-radius: 14rpx;
    background: rgba(22, 119, 255, 0.12);
    color: $color-primary;
    font-weight: 800;
  }

  view {
    color: $color-text-primary;
    font-size: 25rpx;
    font-weight: 700;
  }
}

.tabs {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  padding: 8rpx;
  border-radius: 12rpx;
  background: #f2f5f9;
  margin-bottom: 12rpx;
}

.tab {
  height: 64rpx;
  line-height: 64rpx;
  border-radius: 10rpx;
  color: $color-text-secondary;
  font-size: 27rpx;
  font-weight: 700;
  text-align: center;
}

.tab.active {
  background: #fff;
  color: $color-primary;
  box-shadow: 0 4rpx 14rpx rgba(22, 119, 255, 0.1);
}

.record-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  padding: 24rpx 0;
  border-bottom: 1rpx solid $color-border-light;
}

.record-row:last-child {
  border-bottom: 0;
}
</style>
