<script setup lang="ts">
import { computed } from 'vue'
import { useAuthStore } from '../../store/auth'

const authStore = useAuthStore()

const roleText = computed(() => {
  const map: Record<string, string> = {
    admin: '系统管理员',
    manager: '部门负责人',
    employee: '普通员工'
  }
  return map[authStore.role] || authStore.role || '员工'
})

const menuItems = [
  { mark: '假', title: '我的请假', path: '/pages/apply/index', type: 'tab' },
  { mark: '报', title: '我的报销', path: '/pages/apply/index', type: 'tab' },
  { mark: '日', title: '我的日报', path: '/pages/apply/index', type: 'tab' },
  { mark: '勤', title: '我的考勤', path: '/pages/attendance/index', type: 'tab' },
  { mark: '资', title: '工资条', path: '/pages/salary/detail?id=0', type: 'navigate' },
  { mark: '告', title: '公告通知', path: '/pages/home/index', type: 'tab' }
]

const goToPage = (item: typeof menuItems[number]) => {
  if (item.type === 'tab') {
    uni.switchTab({ url: item.path })
    return
  }
  uni.navigateTo({ url: item.path })
}

const handleLogout = () => {
  uni.showModal({
    title: '退出登录',
    content: '确定退出当前账号吗？',
    success: (res) => {
      if (!res.confirm) return
      authStore.logout()
      uni.reLaunch({ url: '/pages/login/index' })
    }
  })
}
</script>

<template>
  <view class="page-shell with-tabbar">
    <view class="hero-panel mine-hero">
      <view class="avatar">{{ authStore.realName?.charAt(0) || '员' }}</view>
      <view class="hero-title">{{ authStore.realName || '未登录' }}</view>
      <view class="hero-subtitle">{{ authStore.department || '员工管理系统' }} · {{ roleText }}</view>
    </view>

    <view class="section-card account-card">
      <view class="section-header">
        <view class="section-title">账号信息</view>
      </view>
      <view class="field-card">
        <view class="field-row">
          <view class="field-label">用户名</view>
          <view class="field-value">{{ authStore.username || '-' }}</view>
        </view>
        <view class="field-row">
          <view class="field-label">角色</view>
          <view class="field-value">{{ roleText }}</view>
        </view>
        <view class="field-row">
          <view class="field-label">部门</view>
          <view class="field-value">{{ authStore.department || '-' }}</view>
        </view>
      </view>
    </view>

    <view class="section-card">
      <view class="section-header">
        <view class="section-title">常用功能</view>
      </view>
      <view class="menu-grid">
        <view v-for="item in menuItems" :key="item.title" class="menu-item" @click="goToPage(item)">
          <view class="menu-mark">{{ item.mark }}</view>
          <view class="menu-title">{{ item.title }}</view>
        </view>
      </view>
    </view>

    <view class="section-card settings-card">
      <view class="list-row">
        <view class="list-title">消息通知</view>
        <view class="list-meta">系统公告、审批结果提醒</view>
      </view>
      <view class="list-row">
        <view class="list-title">版本信息</view>
        <view class="list-meta">v1.0.0</view>
      </view>
    </view>

    <button class="danger-button logout-button" @click="handleLogout">退出登录</button>
  </view>
</template>

<style lang="scss" scoped>
.mine-hero {
  display: flex;
  flex-direction: column;
  align-items: center;
  border-radius: 0 0 24rpx 24rpx;
  text-align: center;
}

.avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 116rpx;
  height: 116rpx;
  margin-bottom: 22rpx;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.22);
  color: #fff;
  font-size: 50rpx;
  font-weight: 800;
}

.account-card {
  margin-top: -18rpx;
}

.menu-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14rpx;
}

.menu-item {
  padding: 22rpx 8rpx;
  border-radius: 12rpx;
  background: #f7f9fc;
  text-align: center;
}

.menu-mark {
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

.menu-title {
  color: $color-text-primary;
  font-size: 25rpx;
  font-weight: 700;
}

.logout-button {
  margin-top: 24rpx;
}
</style>
