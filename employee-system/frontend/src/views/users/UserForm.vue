<template>
  <div class="user-form">
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">
          <UserOutlined />
          {{ isEdit ? '编辑账号' : '添加账号' }}
        </h2>
        <p class="page-subtitle">{{ isEdit ? '修改账号信息和权限' : '创建新的系统登录账号' }}</p>
      </div>
    </div>

    <div class="form-card">
      <a-form
        ref="formRef"
        :model="formState"
        :rules="formRules"
        layout="vertical"
        @finish="handleSubmit"
      >
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="用户名" name="username">
              <a-input
                v-model:value="formState.username"
                placeholder="请输入用户名"
                size="large"
                :disabled="isEdit"
              >
                <template #prefix>
                  <UserOutlined style="color: #bfbfbf;" />
                </template>
              </a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="真实姓名" name="realName">
              <a-input
                v-model:value="formState.realName"
                placeholder="请输入真实姓名"
                size="large"
              />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item :label="isEdit ? '密码（留空不修改）' : '登录密码'" name="password">
              <a-input-password
                v-model:value="formState.password"
                :placeholder="isEdit ? '留空则不修改密码' : '请输入登录密码'"
                size="large"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="角色" name="role">
              <a-select
                v-model:value="formState.role"
                size="large"
                placeholder="请选择角色"
              >
                <a-select-option value="admin">管理员</a-select-option>
                <a-select-option value="employee">普通员工</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>

        <div class="form-actions">
          <a-button size="large" @click="$router.push('/users')" class="cancel-btn">
            取消返回
          </a-button>
          <a-button type="primary" html-type="submit" size="large" :loading="submitting" class="submit-btn">
            <SaveOutlined v-if="!submitting" />
            {{ submitting ? '提交中...' : (isEdit ? '保存修改' : '创建账号') }}
          </a-button>
        </div>
      </a-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  UserOutlined,
  SaveOutlined
} from '@ant-design/icons-vue'
import { useUserStore } from '../../store/user'
import { userApi } from '../../api/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const formRef = ref(null)
const submitting = ref(false)
const userId = computed(() => route.params.id)

const isEdit = computed(() => !!userId.value && userId.value !== 'add')

const formState = reactive({
  username: '',
  realName: '',
  password: '',
  role: 'employee'
})

const formRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度3-20个字符', trigger: 'blur' }
  ],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  password: [
    { required: !isEdit.value, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6个字符', trigger: 'blur' }
  ],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

onMounted(async () => {
  if (isEdit.value) {
    try {
      const res = await userApi.getUserById(userId.value)
      if (res.data.success && res.data.code === 200) {
        const data = res.data.data
        formState.username = data.username
        formState.realName = data.realName || ''
        formState.role = data.role || 'employee'
        formState.password = ''
      }
    } catch (error) {
      message.error('获取用户信息失败')
    }
  }
})

const handleSubmit = async () => {
  submitting.value = true
  try {
    if (isEdit.value) {
      await userStore.editUser(userId.value, { ...formState })
      message.success('修改成功')
    } else {
      await userStore.createUser({ ...formState })
      message.success('创建成功')
    }
    router.push('/users')
  } catch (error) {
    message.error(error.message || '操作失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.user-form {
  width: 100%;
  animation: fadeInUp 0.4s ease-out;
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 20px 24px;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  border-radius: 10px;
  box-shadow: 0 3px 14px rgba(245, 87, 108, 0.25);
}

.header-left { color: white; }

.page-title {
  font-size: 22px;
  font-weight: 600;
  margin: 0 0 6px 0 !important;
  display: flex;
  align-items: center;
  gap: 10px;
}

.page-subtitle {
  margin: 0 !important;
  opacity: 0.9;
  font-size: 13px;
}

.form-card {
  background: white;
  padding: 32px;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.form-card :deep(.ant-form-item-label > label) {
  font-weight: 600;
  color: #262626;
}

.form-card :deep(.ant-input),
.form-card :deep(.ant-select-selector),
.form-card :deep(.ant-input-password) {
  border-radius: 8px;
  height: 42px;
  font-size: 14px;
}

.form-card :deep(.ant-input:focus),
.form-card :deep(.ant-input-focused),
.form-card :deep(.ant-select-focused .ant-select-selector) {
  border-color: #f5576c;
  box-shadow: 0 0 0 3px rgba(245, 87, 108, 0.1);
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 32px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.cancel-btn {
  height: 44px;
  padding: 0 28px;
  border-radius: 8px;
  font-weight: 500;
  font-size: 14px;
  transition: all 0.3s;
}

.submit-btn {
  height: 44px;
  padding: 0 32px;
  border-radius: 8px;
  font-weight: 600;
  font-size: 14px;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  border: none;
  transition: all 0.3s;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(245, 87, 108, 0.35);
}
</style>
