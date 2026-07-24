<script setup lang="ts">
import { ref } from 'vue'
import { useAuthStore } from '../../store/auth'
import { dailyReportApi } from '../../api/dailyReport'
import { getToday } from '../../utils'

const authStore = useAuthStore()
const reportDate = ref(getToday())
const content = ref('')
const submitting = ref(false)

const onDateChange = (e: any) => {
  reportDate.value = e.detail.value
}

const handleSubmit = async () => {
  if (!content.value.trim()) return uni.showToast({ title: '请填写日报内容', icon: 'none' })
  submitting.value = true
  try {
    await dailyReportApi.submit({
      employeeId: authStore.userId,
      employeeName: authStore.realName,
      department: authStore.department,
      reportDate: reportDate.value,
      content: content.value.trim()
    })
    uni.showToast({ title: '提交成功', icon: 'success' })
    setTimeout(() => uni.navigateBack(), 800)
  } catch (error: any) {
    uni.showToast({ title: error.message || '提交失败', icon: 'none' })
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <view class="page-shell form-page">
    <view class="hero-panel report-hero">
      <view class="hero-kicker">日报提交</view>
      <view class="hero-title">记录今天的工作</view>
      <view class="hero-subtitle">写清楚进展、问题和下一步计划</view>
    </view>

    <view class="section-card form-section">
      <view class="section-title">基础信息</view>
      <view class="field-card">
        <view class="field-row"><view class="field-label">提交人</view><view class="field-value">{{ authStore.realName || '-' }}</view></view>
        <view class="field-row"><view class="field-label">所属部门</view><view class="field-value">{{ authStore.department || '-' }}</view></view>
        <picker class="field-row" mode="date" :value="reportDate" @change="onDateChange">
          <view class="field-label">日报日期</view>
          <view class="field-value">{{ reportDate }}</view>
        </picker>
      </view>
    </view>

    <view class="section-card">
      <view class="section-header">
        <view class="section-title">工作内容</view>
        <view class="muted-text">{{ content.length }}/2000</view>
      </view>
      <textarea
        class="field-textarea report-textarea"
        v-model="content"
        maxlength="2000"
        placeholder="例如：1. 完成了哪些工作；2. 遇到哪些问题；3. 明天准备做什么。"
      />
    </view>

    <view class="safe-bottom-bar">
      <button class="primary-button report-button" :disabled="submitting" @click="handleSubmit">
        {{ submitting ? '正在提交...' : '提交日报' }}
      </button>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.form-page {
  padding-bottom: 160rpx;
}

.report-hero,
.report-button {
  background: linear-gradient(135deg, #ff7d00 0%, #ff9f40 100%);
}

.form-section {
  margin-top: -18rpx;
}

.report-textarea {
  min-height: 420rpx;
}
</style>
