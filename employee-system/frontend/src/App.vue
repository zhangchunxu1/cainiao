<template>
  <a-config-provider :locale="locale">
    <div class="app">
      <router-view></router-view>
    </div>
  </a-config-provider>
</template>

<script setup>
import { ConfigProvider } from 'ant-design-vue';
import zhCN from 'ant-design-vue/es/locale/zh_CN';
import { ref } from 'vue';

const locale = ref(zhCN);
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  height: 100%;
  width: 100%;
}

.app {
  min-height: 100vh;
  padding: 20px;
  background-color: #f0f2f5;
}

#app {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto,
    'Helvetica Neue', Arial, 'Noto Sans', sans-serif, 'Apple Color Emoji',
    'Segoe UI Emoji', 'Segoe UI Symbol', 'Noto Color Emoji';
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}
</style> 