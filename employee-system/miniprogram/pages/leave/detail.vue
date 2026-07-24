<script setup lang="ts">
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { leaveApi } from '../../api/leave'
import { formatDate, getStatusClass, getStatusText } from '../../utils'
import type { LeaveRequest } from '../../types'

const detail = ref<LeaveRequest | null>(null)
const loading = ref(false)
const id = ref(0)
const statusClass = computed(() => getStatusClass(detail.value?.status || ''))

const loadDetail = async () => {
  if (!id.value) return
  loading.value = true
  try {
    detail.value = await leaveApi.getById(id.value)
  } catch (error) {
    uni.showToast({ title: '加载失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

onLoad((options: any) => {
  id.value = Number(options?.id || 0)
  loadDetail()
})
</script>

<template>
  <view class="page-shell">
    <view v-if="loading" class="empty-state">正在加载请假详情...</view>
    <view v-else-if="detail">
      <view class="hero-panel">
        <view class="hero-kicker">请假详情</view>
        <view class="hero-title">{{ detail.leaveType }} · {{ detail.days }}天</view>
        <view class="hero-subtitle">{{ detail.startDate }} 至 {{ detail.endDate }}</view>
      </view>

      <view class="section-card summary-card">
        <view class="section-header">
          <view class="section-title">审批状态</view>
          <view class="status-pill" :class="statusClass">{{ getStatusText(detail.status) }}</view>
        </view>
        <view class="field-card">
          <view class="field-row"><view class="field-label">申请人</view><view class="field-value">{{ detail.employeeName }}</view></view>
          <view class="field-row"><view class="field-label">部门</view><view class="field-value">{{ detail.department || '-' }}</view></view>
          <view class="field-row"><view class="field-label">提交时间</view><view class="field-value">{{ formatDate(detail.createTime, 'YYYY-MM-DD HH:mm') }}</view></view>
        </view>
      </view>

      <view class="section-card">
        <view class="section-title">请假原因</view>
        <view class="content-text">{{ detail.reason || '-' }}</view>
      </view>

      <view class="section-card" v-if="detail.approver || detail.approvalComment || detail.approvalTime">
        <view class="section-title">审批信息</view>
        <view class="field-card compact">
          <view v-if="detail.approver" class="field-row"><view class="field-label">审批人</view><view class="field-value">{{ detail.approver }}</view></view>
          <view v-if="detail.approvalTime" class="field-row"><view class="field-label">审批时间</view><view class="field-value">{{ formatDate(detail.approvalTime, 'YYYY-MM-DD HH:mm') }}</view></view>
          <view v-if="detail.approvalComment" class="field-row"><view class="field-label">意见</view><view class="field-value">{{ detail.approvalComment }}</view></view>
        </view>
      </view>
    </view>
    <view v-else class="empty-state">暂无请假详情</view>
  </view>
</template>

<style lang="scss" scoped>
.summary-card {
  margin-top: -18rpx;
}

.content-text {
  margin-top: 18rpx;
  color: $color-text-primary;
  font-size: 29rpx;
  line-height: 1.75;
  white-space: pre-wrap;
}
</style>
