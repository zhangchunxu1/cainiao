import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd())
  
  return {
    plugins: [vue()],
    base: './',
    server: {
      port: 3000,
      open: true,
      proxy: {
        '/api': {
          target: env.VITE_API_BASE_URL || 'http://localhost:8080',
          changeOrigin: true,
          rewrite: (path) => path.replace(/^\/api/, '')
        }
      }
    },
    build: {
      outDir: `dist-${mode}`,
      assetsDir: 'assets',
      sourcemap: env.VITE_SOURCE_MAP === 'true',
      chunkSizeWarningLimit: 1500,
      rollupOptions: {
        output: {
          manualChunks: {
            'vue-vendor': ['vue', 'vue-router', 'pinia'],
            'antd-vendor': ['ant-design-vue', '@ant-design/icons-vue'],
            'utils-vendor': ['axios', 'dayjs']
          }
        }
      }
    },
    define: {
      __APP_ENV__: JSON.stringify(env)
    }
  }
}) 