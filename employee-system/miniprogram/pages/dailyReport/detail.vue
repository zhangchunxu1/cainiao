<script setup lang="ts">
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { dailyReportApi } from '../../api/dailyReport'
import { formatDate, getStatusClass, getStatusText } from '../../utils'
import type { DailyReport } from '../../types'

const detail = ref<DailyReport | null>(null)
const loading = ref(false)
const id = ref(0)
const statusClass = computed(() => getStatusClass(detail.value?.status || ''))

const loadDetail = async () => {
  if (!id.value) return
  loading.value = true
  try {
    detail.value = await dailyReportApi.getById(id.value)
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
    <view v-if="loading" class="empty-state">正在加载日报详情...</view>
    <view v-else-if="detail">
      <view class="hero-panel report-hero">
        <view class="hero-kicker">日报详情</view>
        <view class="hero-title">{{ detail.reportDate }}</view>
        <view class="hero-subtitle">{{ detail.employeeName }} · {{ detail.department || '-' }}</view>
      </view>

      <view class="section-card summary-card">
        <view class="section-header">
          <view class="section-title">状态</view>
          <view class="status-pill" :class="statusClass">{{ getStatusText(detail.status) }}</view>
        </view>
        <view class="muted-text">提交于 {{ formatDate(detail.createTime, 'YYYY-MM-DD HH:mm') }}</view>
      </view>

      <view class="section-card">
        <view class="section-title">工作内容</view>
        <view class="content-text">{{ detail.content || '-' }}</view>
      </view>

      <view class="section-card" v-if="detail.reviewer || detail.reviewComment || detail.reviewTime">
        <view class="section-title">审核信息</view>
        <view class="field-card compact">
          <view v-if="detail.reviewer" class="field-row"><view class="field-label">审核人</view><view class="field-value">{{ detail.reviewer }}</view></view>
          <view v-if="detail.reviewTime" class="field-row"><view class="field-label">审核时间</view><view class="field-value">{{ formatDate(detail.reviewTime, 'YYYY-MM-DD HH:mm') }}</view></view>
          <view v-if="detail.reviewComment" class="field-row"><view class="field-label">意见</view><view class="field-value">{{ detail.reviewComment }}</view></view>
        </view>
      </view>
    </view>
    <view v-else class="empty-state">暂无日报详情</view>
  </view>
</template>

<style lang="scss" scoped>
.report-hero {
  background: linear-gradient(135deg, #ff7d00 0%, #ff9f40 100%);
}

.summary-card {
  margin-top: -18rpx;
}

.content-text {
  margin-top: 18rpx;
  color: $color-text-primary;
  font-size: 29rpx;
  line-height: 1.8;
  white-space: pre-wrap;
}
</style>
