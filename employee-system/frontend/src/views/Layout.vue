<template>
  <a-layout class="layout">
    <a-layout-header class="header">
      <div class="logo">员工管理系统</div>
      <a-menu
        v-model:selectedKeys="selectedKeys"
        theme="dark"
        mode="horizontal"
        :style="{ lineHeight: '64px' }"
      >
        <a-menu-item key="employees">
          <router-link to="/employees">员工列表</router-link>
        </a-menu-item>
        <a-menu-item key="add">
          <router-link to="/employees/add">添加员工</router-link>
        </a-menu-item>
      </a-menu>
    </a-layout-header>
    <a-layout-content class="content">
      <a-breadcrumb :style="{ margin: '16px 0' }">
        <a-breadcrumb-item>首页</a-breadcrumb-item>
        <a-breadcrumb-item>{{ currentRoute?.meta?.title || '页面' }}</a-breadcrumb-item>
      </a-breadcrumb>
      <div class="content-wrapper">
        <router-view v-slot="{ Component }">
          <component :is="Component" />
        </router-view>
      </div>
    </a-layout-content>
    <a-layout-footer class="footer">
      员工管理系统 ©2024 Created by Admin
    </a-layout-footer>
  </a-layout>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute();
const currentRoute = computed(() => route);

// 根据当前路由设置菜单选中项
const selectedKeys = computed(() => {
  const path = route.path;
  if (path.includes('/employees/add')) {
    return ['add'];
  }
  if (path.includes('/employees/edit')) {
    return ['employees'];
  }
  return ['employees'];
});
</script>

<style scoped>
.layout {
  min-height: 100vh;
}

.header {
  display: flex;
  align-items: center;
  padding: 0 50px;
}

.logo {
  color: white;
  font-size: 18px;
  font-weight: bold;
  margin-right: 30px;
}

.content {
  padding: 0 50px;
  margin: 24px 0;
}

.content-wrapper {
  background: #fff;
  padding: 24px;
  min-height: 280px;
  border-radius: 4px;
}

.footer {
  text-align: center;
  padding: 24px 50px;
  color: rgba(0, 0, 0, 0.45);
}

:deep(.ant-layout-header) {
  height: 64px;
  padding: 0;
  line-height: 64px;
  background: #001529;
}

:deep(.ant-menu-dark) {
  background: transparent;
}

:deep(.ant-breadcrumb) {
  margin: 16px 0;
}
</style> 