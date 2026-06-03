/**
 * 环境配置工具
 * 用于读取和管理不同环境的配置项
 */

// 获取当前环境变量
const env = import.meta.env

export default {
  // 应用标题
  APP_TITLE: env.VITE_APP_TITLE || '员工管理系统',
  
  // API基础地址
  API_BASE_URL: env.VITE_API_BASE_URL || 'http://localhost:8080',
  
  // 是否开发环境
  isDev: env.MODE === 'development',
  
  // 是否测试/预发布环境
  isStaging: env.MODE === 'staging',
  
  // 是否生产环境
  isProd: env.MODE === 'production',
  
  // 当前环境名称
  getEnvName() {
    const names = {
      development: '开发环境',
      staging: '测试环境',
      production: '生产环境'
    }
    return names[env.MODE] || '未知环境'
  },
  
  // 接口超时时间(ms)
  TIMEOUT: Number(env.VITE_TIMEOUT) || 15000,
  
  // 是否启用Mock数据
  ENABLE_MOCK: env.VITE_ENABLE_MOCK === 'true',
  
  // 是否显示调试工具
  SHOW_DEBUG_TOOL: env.VITE_SHOW_DEBUG_TOOL === 'true',
  
  // 是否启用错误上报
  ERROR_REPORT: env.VITE_ERROR_REPORT === 'true',
  
  // 错误上报地址
  ERROR_REPORT_URL: env.VITE_ERROR_REPORT_URL || '',
  
  // 获取所有配置(用于调试)
  getAll() {
    return {
      mode: env.MODE,
      title: this.APP_TITLE,
      apiUrl: this.API_BASE_URL,
      timeout: this.TIMEOUT,
      enableMock: this.ENABLE_MOCK,
      showDebug: this.SHOW_DEBUG_TOOL,
      errorReport: this.ERROR_REPORT
    }
  }
}
