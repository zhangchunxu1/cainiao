<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { announcementApi } from '../../api/announcement'
import { formatDate } from '../../utils'
import type { Announcement } from '../../types'

const detail = ref<Announcement | null>(null)
const loading = ref(false)
const id = ref(0)

const loadDetail = async () => {
  if (!id.value) return
  loading.value = true
  try {
    detail.value = await announcementApi.getById(id.value)
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
  <view class="page-shell article-page">
    <view v-if="loading" class="empty-state">正在加载公告...</view>
    <view v-else-if="detail">
      <view class="article-header">
        <view class="article-tag">公告</view>
        <view class="article-title">{{ detail.title }}</view>
        <view class="article-meta">{{ detail.author || '系统公告' }} · {{ formatDate(detail.createTime, 'YYYY-MM-DD HH:mm') }}</view>
      </view>

      <view class="section-card article-card">
        <view class="article-content">{{ detail.content || '-' }}</view>
      </view>
    </view>
    <view v-else class="empty-state">暂无公告内容</view>
  </view>
</template>

<style lang="scss" scoped>
.article-page {
  padding-top: 36rpx;
}

.article-header {
  padding: 0 4rpx 26rpx;
}

.article-tag {
  display: inline-flex;
  padding: 8rpx 18rpx;
  border-radius: 999rpx;
  background: rgba(22, 119, 255, 0.12);
  color: $color-primary;
  font-size: 23rpx;
  font-weight: 800;
}

.article-title {
  margin-top: 22rpx;
  color: $color-text-primary;
  font-size: 40rpx;
  font-weight: 800;
  line-height: 1.35;
}

.article-meta {
  margin-top: 16rpx;
  color: $color-text-tertiary;
  font-size: 25rpx;
}

.article-content {
  color: $color-text-primary;
  font-size: 30rpx;
  line-height: 1.85;
  white-space: pre-wrap;
}
</style>
