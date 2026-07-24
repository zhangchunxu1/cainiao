<script setup lang="ts">
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { reimbursementApi } from '../../api/reimbursement'
import { formatAmount, formatDate, getStatusClass, getStatusText } from '../../utils'
import type { Reimbursement } from '../../types'

const detail = ref<Reimbursement | null>(null)
const loading = ref(false)
const id = ref(0)
const statusClass = computed(() => getStatusClass(detail.value?.status || ''))

const loadDetail = async () => {
  if (!id.value) return
  loading.value = true
  try {
    detail.value = await reimbursementApi.getById(id.value)
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
    <view v-if="loading" class="empty-state">正在加载报销详情...</view>
    <view v-else-if="detail">
      <view class="hero-panel expense-hero">
        <view class="hero-kicker">报销详情</view>
        <view class="hero-title">¥{{ formatAmount(detail.amount) }}</view>
        <view class="hero-subtitle">{{ detail.type }} · {{ detail.employeeName }}</view>
      </view>

      <view class="section-card summary-card">
        <view class="section-header">
          <view class="section-title">审批状态</view>
          <view class="status-pill" :class="statusClass">{{ getStatusText(detail.status) }}</view>
        </view>
        <view class="field-card">
          <view class="field-row"><view class="field-label">部门</view><view class="field-value">{{ detail.department || '-' }}</view></view>
          <view class="field-row"><view class="field-label">提交时间</view><view class="field-value">{{ formatDate(detail.createTime, 'YYYY-MM-DD HH:mm') }}</view></view>
        </view>
      </view>

      <view class="section-card">
        <view class="section-title">报销事由</view>
        <view class="content-text">{{ detail.reason || '-' }}</view>
      </view>

      <view class="section-card" v-if="detail.managerApprover || detail.financeApprover">
        <view class="section-title">审批记录</view>
        <view class="field-card compact">
          <view v-if="detail.managerApprover" class="field-row"><view class="field-label">经理审批</view><view class="field-value">{{ detail.managerApprover }}</view></view>
          <view v-if="detail.managerComment" class="field-row"><view class="field-label">经理意见</view><view class="field-value">{{ detail.managerComment }}</view></view>
          <view v-if="detail.financeApprover" class="field-row"><view class="field-label">财务审批</view><view class="field-value">{{ detail.financeApprover }}</view></view>
          <view v-if="detail.financeComment" class="field-row"><view class="field-label">财务意见</view><view class="field-value">{{ detail.financeComment }}</view></view>
        </view>
      </view>
    </view>
    <view v-else class="empty-state">暂无报销详情</view>
  </view>
</template>

<style lang="scss" scoped>
.expense-hero {
  background: linear-gradient(135deg, #00b42a 0%, #23c343 100%);
}

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
