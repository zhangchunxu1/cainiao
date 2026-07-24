<script setup lang="ts">
import { computed, ref } from 'vue'
import { useAuthStore } from '../../store/auth'
import { leaveApi } from '../../api/leave'
import { leaveTypes } from '../../utils'

const authStore = useAuthStore()
const leaveType = ref('')
const startDate = ref('')
const endDate = ref('')
const reason = ref('')
const submitting = ref(false)
const typeIndex = ref(0)

const days = computed(() => {
  if (!startDate.value || !endDate.value) return 0
  const start = new Date(startDate.value).getTime()
  const end = new Date(endDate.value).getTime()
  return Math.max(0, Math.ceil((end - start) / 86400000) + 1)
})

const onTypeChange = (e: any) => {
  typeIndex.value = Number(e.detail.value)
  leaveType.value = leaveTypes[typeIndex.value].value
}

const onStartDateChange = (e: any) => {
  startDate.value = e.detail.value
  if (endDate.value && startDate.value > endDate.value) endDate.value = startDate.value
}

const onEndDateChange = (e: any) => {
  endDate.value = e.detail.value
}

const handleSubmit = async () => {
  if (!leaveType.value) return uni.showToast({ title: '请选择请假类型', icon: 'none' })
  if (!startDate.value || !endDate.value) return uni.showToast({ title: '请选择请假日期', icon: 'none' })
  if (!reason.value.trim()) return uni.showToast({ title: '请填写请假原因', icon: 'none' })

  submitting.value = true
  try {
    await leaveApi.submit({
      employeeId: authStore.userId,
      employeeName: authStore.realName,
      department: authStore.department,
      leaveType: leaveType.value,
      startDate: startDate.value,
      endDate: endDate.value,
      days: days.value,
      reason: reason.value.trim()
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
    <view class="hero-panel">
      <view class="hero-kicker">流程申请</view>
      <view class="hero-title">请假申请</view>
      <view class="hero-subtitle">填写请假类型、日期和原因后提交审批</view>
    </view>

    <view class="section-card form-section">
      <view class="section-header">
        <view class="section-title">申请信息</view>
        <view class="muted-text">共 {{ days }} 天</view>
      </view>

      <view class="field-card">
        <view class="field-row">
          <view class="field-label">申请人</view>
          <view class="field-value">{{ authStore.realName || '-' }}</view>
        </view>
        <view class="field-row">
          <view class="field-label">所属部门</view>
          <view class="field-value">{{ authStore.department || '-' }}</view>
        </view>
        <picker class="field-row" :range="leaveTypes" range-key="label" :value="typeIndex" @change="onTypeChange">
          <view class="field-label">请假类型 <text class="required-mark">*</text></view>
          <view class="field-value" :class="{ 'field-placeholder': !leaveType }">{{ leaveType || '请选择' }}</view>
        </picker>
        <picker class="field-row" mode="date" :value="startDate" @change="onStartDateChange">
          <view class="field-label">开始日期 <text class="required-mark">*</text></view>
          <view class="field-value" :class="{ 'field-placeholder': !startDate }">{{ startDate || '请选择' }}</view>
        </picker>
        <picker class="field-row" mode="date" :value="endDate" @change="onEndDateChange">
          <view class="field-label">结束日期 <text class="required-mark">*</text></view>
          <view class="field-value" :class="{ 'field-placeholder': !endDate }">{{ endDate || '请选择' }}</view>
        </picker>
      </view>
    </view>

    <view class="section-card">
      <view class="section-header">
        <view class="section-title">请假原因</view>
        <view class="muted-text">{{ reason.length }}/500</view>
      </view>
      <textarea class="field-textarea" v-model="reason" maxlength="500" placeholder="请说明请假原因，便于审批人判断" />
    </view>

    <view class="safe-bottom-bar">
      <button class="primary-button" :disabled="submitting" @click="handleSubmit">
        {{ submitting ? '正在提交...' : '提交申请' }}
      </button>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.form-page {
  padding-bottom: 160rpx;
}

.form-section {
  margin-top: -18rpx;
}
</style>
