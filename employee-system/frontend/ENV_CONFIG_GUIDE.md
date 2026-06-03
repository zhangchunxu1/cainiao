# 前端多环境配置使用指南

## 📦 环境列表

| 环境 | 文件 | 用途 |
|------|------|------|
| **开发环境** | `.env.development` | 本地开发调试 |
| **测试环境** | `.env.staging` | 测试/预发布服务器 |
| **生产环境** | `.env.production` | 正式线上环境 |

---

## 🚀 使用命令

### 开发模式（自动打开浏览器）

```bash
# 启动开发环境
npm run dev

# 启动测试环境(连接测试服务器)
npm run dev:staging
```

### 打包构建

```bash
# 打包开发环境 → 输出到 dist-development/
npm run build:dev

# 打包测试环境 → 输出到 dist-staging/
npm run build:staging

# 打包生产环境 → 输出到 dist-production/
npm run build:prod
```

### 预览打包结果

```bash
# 预览开发环境打包
npm run preview

# 预览测试环境打包
npm run preview:staging

# 预览生产环境打包
npm run preview:prod
```

---

## 📝 环境变量说明

### 必须配置项

| 变量名 | 说明 | 示例 |
|--------|------|------|
| `VITE_APP_TITLE` | 应用标题 | `员工管理系统` |
| `VITE_API_BASE_URL` | API接口地址 | `http://localhost:8080` |

### 可选配置项

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| `VITE_TIMEOUT` | 接口超时时间(ms) | `15000` |
| `VITE_ENABLE_MOCK` | 是否启用Mock数据 | `false` |
| `VITE_SHOW_DEBUG_TOOL` | 是否显示调试工具 | `false` |
| `VITE_ERROR_REPORT` | 是否启用错误上报 | `false` |
| `VITE_SOURCE_MAP` | 是否生成SourceMap | `false` |

---

## 💻 在代码中使用

### 方式1：直接读取环境变量

```javascript
// 在任何组件中
const apiUrl = import.meta.env.VITE_API_BASE_URL
const title = import.meta.env.VITE_APP_TITLE
console.log('当前环境:', import.meta.env.MODE)
```

### 方式2：使用config工具（推荐）

```javascript
import config from '@/config'

// 获取配置
console.log(config.APP_TITLE)        // 应用标题
console.log(config.API_BASE_URL)     // API地址
console.log(config.isDev)            // 是否开发环境
console.log(config.isProd)           // 是否生产环境
console.log(config.getEnvName())     // 获取环境名称

// 条件判断
if (config.isDev) {
  console.log('这是开发环境')
}

if (config.SHOW_DEBUG_TOOL) {
  // 显示调试工具
}
```

### 示例：在Vue组件中显示环境标识

```vue
<template>
  <div class="env-tag" v-if="!config.isProd">
    <a-tag :color="envColor">{{ config.getEnvName() }}</a-tag>
  </div>
</template>

<script setup>
import config from '@/config'
import { computed } from 'vue'

const envColor = computed(() => {
  if (config.isDev) return 'green'
  if (config.isStaging) return 'orange'
  return 'red'
})
</script>

<style scoped>
.env-tag {
  position: fixed;
  top: 10px;
  right: 10px;
  z-index: 9999;
}
</style>
```

---

## 📂 打包输出目录结构

```
frontend/
├── dist-development/    # 开发环境打包
│   ├── index.html
│   └── assets/
├── dist-staging/        # 测试环境打包
│   ├── index.html
│   └── assets/
├── dist-production/     # 生产环境打包
│   ├── index.html
│   └── assets/
```

---

## 🔧 Vite配置优化说明

### 1. 按环境输出到不同目录
```javascript
build: {
  outDir: `dist-${mode}`,  // dist-development / dist-staging / dist-production
}
```

### 2. 代码分割优化
```javascript
rollupOptions: {
  output: {
    manualChunks: {
      'vue-vendor': ['vue', 'vue-router', 'pinia'],           // Vue核心
      'antd-vendor': ['ant-design-vue', '@ant-design/icons-vue'], // UI库
      'utils-vendor': ['axios', 'dayjs']                        // 工具库
    }
  }
}
```
**效果：** 将大依赖拆分成独立chunk，利用浏览器缓存，提升加载速度。

### 3. SourceMap控制
- **开发环境**：开启SourceMap，方便调试
- **生产环境**：关闭SourceMap，减小体积、保护源码

---

## 🌐 部署建议

### Nginx配置示例

```nginx
# 生产环境
server {
    listen 80;
    server_name www.example.com;
    
    root /path/to/dist-production;
    index index.html;
    
    # SPA路由支持
    location / {
        try_files $uri $uri/ /index.html;
    }
    
    # API代理
    location /api/ {
        proxy_pass https://api.example.com/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
    
    # Gzip压缩
    gzip on;
    gzip_types text/plain text/css application/json application/javascript text/xml;
}
```

---

## ⚠️ 注意事项

1. **环境变量必须以 `VITE_` 开头**，否则Vite不会暴露给客户端代码
2. **修改 `.env` 文件后需要重启开发服务器**
3. **不要将敏感信息（密码、密钥）提交到Git**
4. **生产环境记得关闭 SourceMap 和调试工具**

---

*最后更新：2026年06月01日*
