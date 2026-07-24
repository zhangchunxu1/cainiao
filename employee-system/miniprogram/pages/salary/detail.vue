<script setup lang="ts">
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { salaryApi } from '../../api/salary'
import { formatAmount, formatDate } from '../../utils'
import type { SalarySlip } from '../../types'

const detail = ref<SalarySlip | null>(null)
const loading = ref(false)
const id = ref(0)

const incomeItems = computed(() => {
  const item = detail.value
  if (!item) return []
  return [
    ['基本工资', item.basicSalary],
    ['绩效工资', item.performance],
    ['奖金', item.bonus],
    ['津贴', item.allowance],
    ['加班费', item.overtimePay]
  ]
})

const deductionItems = computed(() => {
  const item = detail.value
  if (!item) return []
  return [
    ['社会保险', item.socialInsurance],
    ['住房公积金', item.housingFund],
    ['个人所得税', item.personalTax],
    ['其他扣除', item.otherDeductions]
  ]
})

const totalIncome = computed(() => incomeItems.value.reduce((sum, item) => sum + Number(item[1] || 0), 0))
const totalDeduction = computed(() => deductionItems.value.reduce((sum, item) => sum + Number(item[1] || 0), 0))

const loadDetail = async () => {
  if (!id.value) return
  loading.value = true
  try {
    detail.value = await salaryApi.getById(id.value)
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
  <view class="page-shell salary-page">
    <view v-if="loading" class="empty-state">正在加载工资条...</view>
    <view v-else-if="detail">
      <view class="hero-panel salary-hero">
        <view class="hero-kicker">{{ detail.month }} 工资条</view>
        <view class="hero-title">¥{{ formatAmount(detail.netSalary) }}</view>
        <view class="hero-subtitle">{{ detail.employeeName }} · {{ detail.department }}</view>
      </view>

      <view class="section-card salary-summary">
        <view class="metric-grid">
          <view class="metric-item"><view class="metric-label">应发合计</view><view class="metric-value">¥{{ formatAmount(totalIncome) }}</view></view>
          <view class="metric-item"><view class="metric-label">扣除合计</view><view class="metric-value deduction">¥{{ formatAmount(totalDeduction) }}</view></view>
        </view>
      </view>

      <view class="section-card">
        <view class="section-title">收入明细</view>
        <view v-for="item in incomeItems" :key="item[0]" class="salary-row">
          <text>{{ item[0] }}</text>
          <text>¥{{ formatAmount(Number(item[1])) }}</text>
        </view>
      </view>

      <view class="section-card">
        <view class="section-title">扣除明细</view>
        <view v-for="item in deductionItems" :key="item[0]" class="salary-row">
          <text>{{ item[0] }}</text>
          <text class="deduction">-¥{{ formatAmount(Number(item[1])) }}</text>
        </view>
      </view>

      <view class="section-card">
        <view class="salary-row final-row">
          <text>实发工资</text>
          <text>¥{{ formatAmount(detail.netSalary) }}</text>
        </view>
        <view class="list-meta">生成时间：{{ formatDate(detail.createTime, 'YYYY-MM-DD HH:mm') }}</view>
      </view>
    </view>
    <view v-else class="empty-state">暂无工资条数据</view>
  </view>
</template>

<style lang="scss" scoped>
.salary-hero {
  background: linear-gradient(135deg, #eb2f96 0%, #f759ab 100%);
}

.salary-summary {
  margin-top: -18rpx;
}

.deduction {
  color: $color-error !important;
}

.salary-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 22rpx 0;
  border-bottom: 1rpx solid $color-border-light;
  color: $color-text-secondary;
  font-size: 28rpx;
}

.salary-row:last-child {
  border-bottom: 0;
}

.salary-row text:last-child {
  color: $color-text-primary;
  font-weight: 800;
}

.final-row {
  padding-top: 0;
  color: $color-text-primary;
  font-size: 32rpx;
  font-weight: 800;
}

.final-row text:last-child {
  color: $color-error;
  font-size: 40rpx;
}
</style>
