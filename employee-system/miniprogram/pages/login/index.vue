<script setup lang="ts">
import { ref } from 'vue'
import { useAuthStore } from '../../store/auth'

const authStore = useAuthStore()
const username = ref('admin')
const password = ref('admin123')
const loading = ref(false)

const handleLogin = async () => {
  if (!username.value.trim()) {
    uni.showToast({ title: '请输入用户名', icon: 'none' })
    return
  }
  if (!password.value.trim()) {
    uni.showToast({ title: '请输入密码', icon: 'none' })
    return
  }

  loading.value = true
  try {
    const success = await authStore.login({
      username: username.value.trim(),
      password: password.value
    })
    if (success) {
      uni.showToast({ title: '登录成功', icon: 'success' })
      setTimeout(() => {
        uni.switchTab({ url: '/pages/home/index' })
      }, 500)
    }
  } catch (error: any) {
    uni.showToast({ title: error.message || '登录失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <view class="login-page">
    <view class="login-hero">
      <view class="product-mark">员</view>
      <view>
        <view class="hero-title">员工管理系统</view>
        <view class="hero-subtitle">移动端考勤、申请、公告与薪资查询</view>
      </view>
    </view>

    <view class="login-card">
      <view class="form-heading">账号登录</view>
      <view class="form-note">使用后台账号进入小程序工作台</view>

      <view class="input-block">
        <text class="input-label">用户名</text>
        <input
          class="login-input"
          v-model="username"
          placeholder="请输入用户名"
          placeholder-class="field-placeholder"
        />
      </view>

      <view class="input-block">
        <text class="input-label">密码</text>
        <input
          class="login-input"
          v-model="password"
          type="password"
          placeholder="请输入密码"
          placeholder-class="field-placeholder"
          @confirm="handleLogin"
        />
      </view>

      <button class="primary-button login-button" :disabled="loading" @click="handleLogin">
        {{ loading ? '正在登录...' : '登录' }}
      </button>

      <view class="demo-account">默认账号：admin / admin123</view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  padding: 96rpx 36rpx 40rpx;
  background: linear-gradient(180deg, #eaf3ff 0%, #f6f8fb 46%, #f6f8fb 100%);
}

.login-hero {
  display: flex;
  align-items: center;
  gap: 24rpx;
  margin-bottom: 52rpx;
}

.product-mark {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 112rpx;
  height: 112rpx;
  border-radius: 26rpx;
  background: #1677ff;
  color: #fff;
  font-size: 52rpx;
  font-weight: 800;
  box-shadow: 0 16rpx 34rpx rgba(22, 119, 255, 0.24);
}

.hero-title {
  color: #111827;
  font-size: 46rpx;
  font-weight: 800;
  line-height: 1.2;
}

.hero-subtitle {
  margin-top: 10rpx;
  color: #5f6f86;
  font-size: 26rpx;
}

.login-card {
  padding: 40rpx 32rpx;
  border: 1rpx solid #edf1f7;
  border-radius: 18rpx;
  background: #fff;
  box-shadow: 0 16rpx 40rpx rgba(31, 41, 55, 0.08);
}

.form-heading {
  color: #111827;
  font-size: 38rpx;
  font-weight: 800;
}

.form-note {
  margin-top: 10rpx;
  margin-bottom: 34rpx;
  color: #7b8798;
  font-size: 25rpx;
}

.input-block {
  margin-bottom: 24rpx;
}

.input-label {
  display: block;
  margin-bottom: 12rpx;
  color: #344054;
  font-size: 26rpx;
  font-weight: 700;
}

.login-input {
  height: 92rpx;
  padding: 0 24rpx;
  border: 1rpx solid #e5e9f0;
  border-radius: 12rpx;
  background: #f8fafc;
  color: #111827;
  font-size: 30rpx;
}

.login-button {
  margin-top: 36rpx;
}

.demo-account {
  margin-top: 24rpx;
  color: #86909c;
  font-size: 24rpx;
  text-align: center;
}
</style>
