<template>
  <div class="login-container">
    <div class="background-pattern"></div>
    <div class="login-wrapper">
      <div class="login-left">
        <div class="brand-section">
          <div class="logo-icon">
            <TeamOutlined />
          </div>
          <h1 class="brand-title">员工管理系统</h1>
          <p class="brand-subtitle">Employee Management System</p>
          <div class="features">
            <div class="feature-item">
              <CheckCircleOutlined />
              <span>高效管理员工信息</span>
            </div>
            <div class="feature-item">
              <CheckCircleOutlined />
              <span>智能数据分析</span>
            </div>
            <div class="feature-item">
              <CheckCircleOutlined />
              <span>安全可靠的数据存储</span>
            </div>
          </div>
        </div>
      </div>
      
      <div class="login-right">
        <div class="login-box">
          <div class="login-header">
            <h2>欢迎回来</h2>
            <p>请登录您的账户以继续</p>
          </div>

          <a-form
            :model="formState"
            :rules="rules"
            @finish="handleLogin"
            layout="vertical"
            class="login-form"
          >
            <a-form-item name="username" label="用户名">
              <a-input
                v-model:value="formState.username"
                placeholder="请输入用户名"
                size="large"
              >
                <template #prefix>
                  <UserOutlined style="color: #1890ff;" />
                </template>
              </a-input>
            </a-form-item>

            <a-form-item name="password" label="密码">
              <a-input-password
                v-model:value="formState.password"
                placeholder="请输入密码"
                size="large"
              >
                <template #prefix>
                  <LockOutlined style="color: #1890ff;" />
                </template>
              </a-input-password>
            </a-form-item>

            <a-form-item>
              <a-button
                type="primary"
                html-type="submit"
                :loading="loading"
                block
                size="large"
                class="login-button"
              >
                {{ loading ? '登录中...' : '登 录' }}
              </a-button>
            </a-form-item>
          </a-form>

          <div class="login-footer">
            <a-divider>测试账号</a-divider>
            <div class="demo-account">
              <a-tag color="blue">用户名: admin</a-tag>
              <a-tag color="green">密码: admin123</a-tag>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { UserOutlined, LockOutlined, TeamOutlined, CheckCircleOutlined } from '@ant-design/icons-vue'
import { useAuthStore } from '../store/auth'

const router = useRouter()
const authStore = useAuthStore()
const loading = ref(false)

const formState = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  loading.value = true
  try {
    const success = await authStore.login(formState)
    if (success) {
      message.success({
        content: '登录成功！',
        duration: 2,
        style: {
          marginTop: '20vh'
        }
      })
      router.push('/employees')
    }
  } catch (error) {
    message.error(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
}

.background-pattern {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: 
    radial-gradient(circle at 20% 50%, rgba(255, 255, 255, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 80% 80%, rgba(255, 255, 255, 0.1) 0%, transparent 50%);
  animation: float 6s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-20px); }
}

.login-wrapper {
  display: flex;
  width: 900px;
  min-height: 550px;
  background: white;
  border-radius: 20px;
  box-shadow: 
    0 20px 60px rgba(0, 0, 0, 0.3),
    0 0 0 1px rgba(255, 255, 255, 0.1);
  overflow: hidden;
  position: relative;
  z-index: 1;
  animation: slideUp 0.6s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-left {
  flex: 1;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 40px;
  position: relative;
  overflow: hidden;
}

.login-left::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 70%);
  animation: rotate 20s linear infinite;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.brand-section {
  text-align: center;
  color: white;
  position: relative;
  z-index: 1;
}

.logo-icon {
  font-size: 64px;
  margin-bottom: 24px;
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.2));
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

.brand-title {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 12px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.brand-subtitle {
  font-size: 14px;
  opacity: 0.9;
  margin-bottom: 40px;
  letter-spacing: 2px;
}

.features {
  text-align: left;
  max-width: 280px;
  margin: 0 auto;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  font-size: 14px;
  opacity: 0.95;
  animation: fadeInLeft 0.8s ease-out backwards;
}

.feature-item:nth-child(1) { animation-delay: 0.2s; }
.feature-item:nth-child(2) { animation-delay: 0.4s; }
.feature-item:nth-child(3) { animation-delay: 0.6s; }

@keyframes fadeInLeft {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 0.95;
    transform: translateX(0);
  }
}

.feature-item .anticon {
  font-size: 18px;
  color: #52c41a;
}

.login-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 50px;
  background: #fafafa;
}

.login-box {
  width: 100%;
  max-width: 380px;
}

.login-header {
  margin-bottom: 36px;
  text-align: center;
}

.login-header h2 {
  font-size: 28px;
  font-weight: 600;
  color: #1f1f1f;
  margin-bottom: 8px;
}

.login-header p {
  color: #8c8c8c;
  font-size: 14px;
}

.login-form :deep(.ant-form-item-label > label) {
  font-weight: 500;
  color: #595959;
}

.login-form :deep(.ant-input),
.login-form :deep(.ant-input-password) {
  border-radius: 8px;
  border: 2px solid #f0f0f0;
  transition: all 0.3s;
}

.login-form :deep(.ant-input:focus),
.login-form :deep(.ant-input-password:focus),
.login-form :deep(.ant-input-affix-wrapper-focused) {
  border-color: #1890ff;
  box-shadow: 0 0 0 3px rgba(24, 144, 255, 0.1);
}

.login-button {
  height: 48px;
  font-size: 16px;
  font-weight: 500;
  border-radius: 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
  transition: all 0.3s;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.5);
}

.login-footer {
  margin-top: 24px;
}

.demo-account {
  display: flex;
  justify-content: center;
  gap: 12px;
  flex-wrap: wrap;
}

@media (max-width: 768px) {
  .login-wrapper {
    flex-direction: column;
    width: 90%;
    max-width: 450px;
    min-height: auto;
  }

  .login-left {
    padding: 40px 30px;
  }

  .brand-title {
    font-size: 24px;
  }

  .features {
    display: none;
  }

  .login-right {
    padding: 40px 30px;
  }
}
</style>