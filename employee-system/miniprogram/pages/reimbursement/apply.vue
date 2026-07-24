<script setup lang="ts">
import { ref } from 'vue'
import { useAuthStore } from '../../store/auth'
import { reimbursementApi } from '../../api/reimbursement'
import { reimbursementTypes } from '../../utils'

const authStore = useAuthStore()
const type = ref('')
const typeIndex = ref(0)
const amount = ref('')
const reason = ref('')
const submitting = ref(false)

const onTypeChange = (e: any) => {
  typeIndex.value = Number(e.detail.value)
  type.value = reimbursementTypes[typeIndex.value].value
}

const handleSubmit = async () => {
  if (!type.value) return uni.showToast({ title: '请选择报销类型', icon: 'none' })
  if (!amount.value || Number(amount.value) <= 0) return uni.showToast({ title: '请输入正确金额', icon: 'none' })
  if (!reason.value.trim()) return uni.showToast({ title: '请填写报销事由', icon: 'none' })

  submitting.value = true
  try {
    await reimbursementApi.create({
      type: type.value,
      amount: Number(amount.value),
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
    <view class="hero-panel expense-hero">
      <view class="hero-kicker">费用流程</view>
      <view class="hero-title">报销申请</view>
      <view class="hero-subtitle">录入金额、类型和事由后提交审批</view>
    </view>

    <view class="section-card form-section">
      <view class="section-title">报销信息</view>
      <view class="field-card">
        <view class="field-row"><view class="field-label">申请人</view><view class="field-value">{{ authStore.realName || '-' }}</view></view>
        <view class="field-row"><view class="field-label">所属部门</view><view class="field-value">{{ authStore.department || '-' }}</view></view>
        <picker class="field-row" :range="reimbursementTypes" range-key="label" :value="typeIndex" @change="onTypeChange">
          <view class="field-label">报销类型 <text class="required-mark">*</text></view>
          <view class="field-value" :class="{ 'field-placeholder': !type }">{{ type || '请选择' }}</view>
        </picker>
        <view class="field-row">
          <view class="field-label">金额 <text class="required-mark">*</text></view>
          <input class="field-input amount-input" type="digit" v-model="amount" placeholder="0.00" />
        </view>
      </view>
    </view>

    <view class="section-card">
      <view class="section-header">
        <view class="section-title">报销事由</view>
        <view class="muted-text">{{ reason.length }}/500</view>
      </view>
      <textarea class="field-textarea" v-model="reason" maxlength="500" placeholder="说明费用用途、发生时间或关联事项" />
    </view>

    <view class="safe-bottom-bar">
      <button class="primary-button expense-button" :disabled="submitting" @click="handleSubmit">
        {{ submitting ? '正在提交...' : '提交报销' }}
      </button>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.form-page {
  padding-bottom: 160rpx;
}

.expense-hero {
  background: linear-gradient(135deg, #00b42a 0%, #23c343 100%);
}

.form-section {
  margin-top: -18rpx;
}

.amount-input {
  color: $color-error;
  font-weight: 800;
}

.expense-button {
  background: $color-success;
}
</style>
