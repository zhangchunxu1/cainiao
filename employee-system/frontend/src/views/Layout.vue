<template>
  <a-layout class="layout" :style="{ minHeight: '100vh' }">
    <!-- 侧边栏 -->
    <a-layout-sider
      v-model:collapsed="collapsed"
      :trigger="null"
      collapsible
      width="256"
      class="sider"
    >
      <div class="logo" @click="$router.push('/dashboard')">
        <div class="logo-icon">
          <TeamOutlined />
        </div>
        <h1 v-if="!collapsed" class="logo-title">员工管理系统</h1>
      </div>

      <a-menu
        v-model:selectedKeys="selectedKeys"
        theme="dark"
        mode="inline"
        class="side-menu"
      >
        <a-menu-item key="dashboard" @click="$router.push('/dashboard')">
          <DashboardOutlined />
          <span>首页概览</span>
        </a-menu-item>

        <a-sub-menu key="emp-sub">
          <template #icon><UserOutlined /></template>
          <template #title>员工管理</template>
          <a-menu-item key="employees" @click="$router.push('/employees')">
            <UnorderedListOutlined />
            <span>员工列表</span>
          </a-menu-item>
          <a-menu-item key="add" @click="$router.push('/employees/add')">
            <UserAddOutlined />
            <span>添加员工</span>
          </a-menu-item>
        </a-sub-menu>

        <a-sub-menu key="dept-sub">
          <template #icon><ApartmentOutlined /></template>
          <template #title>部门管理</template>
          <a-menu-item key="departments" @click="$router.push('/departments')">
            <TeamOutlined />
            <span>部门列表</span>
          </a-menu-item>
        </a-sub-menu>

        <a-sub-menu key="att-sub">
          <template #icon><ClockCircleOutlined /></template>
          <template #title>考勤管理</template>
          <a-menu-item key="attendance" @click="$router.push('/attendance')">
            <ScheduleOutlined />
            <span>考勤记录</span>
          </a-menu-item>
        </a-sub-menu>

        <a-sub-menu key="notice-sub">
          <template #icon><NotificationOutlined /></template>
          <template #title>公告通知</template>
          <a-menu-item key="announcements" @click="$router.push('/announcements')">
            <BellOutlined />
            <span>公告列表</span>
          </a-menu-item>
        </a-sub-menu>

        <a-sub-menu key="leave-sub">
          <template #icon><CalendarOutlined /></template>
          <template #title>请假管理</template>
          <a-menu-item key="leaves" @click="$router.push('/leaves')">
            <FormOutlined />
            <span>请假申请</span>
          </a-menu-item>
        </a-sub-menu>
      </a-menu>

      <div class="sider-footer" v-if="!collapsed">
        <div class="system-info">
          <span>Employee Management System</span>
          <span>Version 1.0.0</span>
        </div>
      </div>
    </a-layout-sider>

    <!-- 右侧内容区 -->
    <a-layout>
      <!-- 顶部栏 -->
      <a-layout-header class="header">
        <div class="header-content">
          <div class="header-left">
            <a-button
              type="text"
              class="collapse-btn"
              @click="() => (collapsed = !collapsed)"
            >
              <MenuUnfoldOutlined v-if="collapsed" />
              <MenuFoldOutlined v-else />
            </a-button>

            <a-breadcrumb class="breadcrumb">
              <a-breadcrumb-item>
                <HomeOutlined />
              </a-breadcrumb-item>
              <a-breadcrumb-item>{{ currentRoute?.meta?.title || '页面' }}</a-breadcrumb-item>
            </a-breadcrumb>
          </div>

          <div class="header-right">
            <div class="user-info">
              <a-avatar :size="28" style="background-color: #667eea; cursor: pointer;">
                        {{ authStore.username ? authStore.username.charAt(0).toUpperCase() : 'U' }}
                    </a-avatar>
              <div class="user-details">
                <span class="username">{{ authStore.username || '用户' }}</span>
                <span class="role">管理员</span>
              </div>
            </div>
            <a-button type="text" @click="handleLogout" class="logout-btn">
              <LogoutOutlined />
              <span>退出登录</span>
            </a-button>
          </div>
        </div>
      </a-layout-header>

      <!-- 主内容区 -->
      <a-layout-content class="content">
        <div class="content-wrapper">
          <router-view v-slot="{ Component }">
            <transition name="fade" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </div>
      </a-layout-content>

      <!-- 页脚 -->
      <a-layout-footer class="footer">
        <div class="footer-content">
          <TeamOutlined class="footer-icon" />
          <span>员工管理系统 ©2024 Created with ❤️</span>
        </div>
      </a-layout-footer>
    </a-layout>
  </a-layout>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '../store/auth';
import { message } from 'ant-design-vue';
import {
  TeamOutlined,
  DashboardOutlined,
  UnorderedListOutlined,
  UserAddOutlined,
  LogoutOutlined,
  HomeOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  UserOutlined,
  ApartmentOutlined,
  ClockCircleOutlined,
  ScheduleOutlined,
  NotificationOutlined,
  BellOutlined,
  CalendarOutlined,
  FormOutlined
} from '@ant-design/icons-vue';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const collapsed = ref(false);
const currentRoute = computed(() => route);

// 根据当前路由设置菜单选中项
const selectedKeys = computed(() => {
  const path = route.path;
  if (path === '/dashboard') {
    return ['dashboard'];
  }
  if (path.includes('/employees/add')) {
    return ['add'];
  }
  if (path.includes('/employees/edit')) {
    return ['employees'];
  }
  if (path.startsWith('/employees')) {
    return ['employees'];
  }
  if (path.startsWith('/departments')) {
    return ['departments'];
  }
  if (path.startsWith('/attendance')) {
    return ['attendance'];
  }
  if (path.startsWith('/announcements')) {
    return ['announcements'];
  }
  if (path.startsWith('/leaves')) {
    return ['leaves'];
  }
  return ['dashboard'];
});

const handleLogout = () => {
  authStore.logout();
  message.success({
    content: '已退出登录',
    duration: 2
  });
  router.push('/login');
};
</script>

<style scoped>
.layout {
  background: #f0f2f5;
}

/* 侧边栏样式 */
.sider {
  background: linear-gradient(180deg, #001529 0%, #003a70 100%) !important;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.15);
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  z-index: 100;
  height: 100vh;
}

.sider :deep(.ant-layout-sider-children) {
  display: flex;
  flex-direction: column;
}

.logo {
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 0 20px;
  cursor: pointer;
  transition: all 0.3s;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  overflow: hidden;
}

.logo:hover {
  background: rgba(255, 255, 255, 0.05);
}

.logo-icon {
  font-size: 32px;
  color: white;
  animation: pulse 2s ease-in-out infinite;
  flex-shrink: 0;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
}

.logo-title {
  color: white;
  font-size: 18px;
  font-weight: 600;
  margin: 0 !important;
  white-space: nowrap;
  letter-spacing: 1px;
  opacity: 1;
  transition: opacity 0.3s;
}

/* 菜单样式 */
.side-menu {
  flex: 1;
  background: transparent !important;
  border-right: none !important;
  margin-top: 8px;
  padding: 0 8px;
}

.side-menu :deep(.ant-menu-item) {
  margin: 4px 0;
  height: 48px;
  line-height: 48px;
  border-radius: 8px;
  transition: all 0.3s;
  display: flex;
  align-items: center;
}

.side-menu :deep(.ant-menu-item .anticon) {
  font-size: 18px;
  margin-right: 10px;
}

.side-menu :deep(.ant-menu-item:hover) {
  background: rgba(255, 255, 255, 0.1) !important;
  transform: translateX(4px);
}

.side-menu :deep(.ant-menu-item-selected) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.side-menu :deep(.ant-menu-item-selected::after) {
  display: none;
}

.sider-footer {
  padding: 16px 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}

.system-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
  color: rgba(255, 255, 255, 0.45);
  font-size: 11px;
  text-align: center;
}

/* 右侧布局 */
.layout > .ant-layout {
  flex: 1;
  min-width: 0;
  margin-left: 256px;
  transition: margin-left 0.2s;
}

.layout :deep(.ant-layout-sider-collapsed) ~ .ant-layout {
  margin-left: 80px;
}

/* 顶部栏 */
.header {
  background: white !important;
  padding: 0 16px;
  height: 52px;
  line-height: 52px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  position: sticky;
  top: 0;
  z-index: 99;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.collapse-btn {
  font-size: 16px;
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  transition: all 0.3s;
}

.collapse-btn:hover {
  background: #f0f0f0;
  transform: scale(1.05);
}

.breadcrumb {
  font-size: 14px;
}

.breadcrumb :deep(.anticon) {
  margin-right: 4px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 3px 10px;
  background: #f5f5f5;
  border-radius: 18px;
  transition: all 0.3s;
  cursor: pointer;
}

.user-info:hover {
  background: #e8e8e8;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.user-details {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
}

.username {
  color: #262626;
  font-size: 13px;
  font-weight: 500;
}

.role {
  color: #8c8c8c;
  font-size: 11px;
}

.logout-btn {
  color: #595959 !important;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 16px;
  border-radius: 8px;
  transition: all 0.3s !important;
}

.logout-btn:hover {
  background: #fff1f0 !important;
  color: #ff4d4f !important;
}

/* 主内容区 */
.content {
  padding: 10px;
  min-height: calc(100vh - 52px - 69px);
  background: #f5f5f5;
}

.content-wrapper {
  min-height: 100%;
  width: 100%;
}

/* 页脚 */
.footer {
  text-align: center;
  padding: 24px 50px;
  background: white;
}

.footer-content {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #8c8c8c;
}

.footer-icon {
  font-size: 16px;
  animation: heartbeat 1.5s ease-in-out infinite;
}

@keyframes heartbeat {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.2); }
}

/* 页面切换动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.fade-enter-from {
  opacity: 0;
  transform: translateY(20px);
}

.fade-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .user-details {
    display: none;
  }

  .logout-btn span {
    display: none;
  }

  .breadcrumb {
    display: none;
  }

  .content {
    padding: 16px;
  }
}
</style>