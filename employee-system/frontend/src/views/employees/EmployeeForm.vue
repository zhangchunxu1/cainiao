<template>
  <div class="employee-form">
    <div class="form-header">
      <div class="header-icon">
        <UserAddOutlined v-if="!isEdit" />
        <EditOutlined v-else />
      </div>
      <div class="header-text">
        <h2>{{ isEdit ? '编辑员工信息' : '添加新员工' }}</h2>
        <p>{{ isEdit ? '修改员工的基本信息和职位详情' : '填写以下信息以创建新的员工档案' }}</p>
      </div>
    </div>

    <div class="form-card">
      <a-form
        :model="formState"
        :rules="rules"
        ref="formRef"
        layout="vertical"
        class="employee-form-content"
      >
        <a-row :gutter="[24, 0]">
          <a-col :span="12">
            <a-form-item label="姓名" name="name">
              <a-input 
                v-model:value="formState.name" 
                placeholder="请输入姓名" 
                size="large"
              >
                <template #prefix>
                  <UserOutlined style="color: #bfbfbf;" />
                </template>
              </a-input>
            </a-form-item>
          </a-col>

          <a-col :span="12">
            <a-form-item label="年龄" name="age">
              <a-input-number 
                v-model:value="formState.age" 
                :min="18" 
                :max="100" 
                style="width: 100%" 
                size="large"
                placeholder="请输入年龄"
              />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="[24, 0]">
          <a-col :span="12">
            <a-form-item label="性别" name="gender">
              <a-radio-group v-model:value="formState.gender" size="large" class="gender-group">
                <a-radio-button value="男">👨 男</a-radio-button>
                <a-radio-button value="女">👩 女</a-radio-button>
              </a-radio-group>
            </a-form-item>
          </a-col>

          <a-col :span="12">
            <a-form-item label="入职日期" name="hireDate">
              <a-date-picker 
                v-model:value="formState.hireDate" 
                style="width: 100%" 
                :format="dateFormat"
                value-format="YYYY-MM-DD"
                size="large"
                placeholder="选择入职日期"
              />
            </a-form-item>
          </a-col>
        </a-row>

        <a-divider orientation="left" class="section-divider">
          <PhoneOutlined /> 联系方式
        </a-divider>

        <a-row :gutter="[24, 0]">
          <a-col :span="12">
            <a-form-item label="电话号码" name="phone">
              <a-input 
                v-model:value="formState.phone" 
                placeholder="请输入手机号码" 
                size="large"
              >
                <template #prefix>
                  <PhoneOutlined style="color: #bfbfbf;" />
                </template>
              </a-input>
            </a-form-item>
          </a-col>

          <a-col :span="12">
            <a-form-item label="电子邮箱" name="email">
              <a-input 
                v-model:value="formState.email" 
                placeholder="请输入邮箱地址" 
                size="large"
              >
                <template #prefix>
                  <MailOutlined style="color: #bfbfbf;" />
                </template>
              </a-input>
            </a-form-item>
          </a-col>
        </a-row>

        <a-divider orientation="left" class="section-divider">
          <HomeOutlined /> 职位信息
        </a-divider>

        <a-row :gutter="[24, 0]">
          <a-col :span="12">
            <a-form-item label="所属部门" name="department">
              <a-select 
                v-model:value="formState.department" 
                placeholder="请选择部门" 
                size="large"
              >
                <a-select-option value="技术部">
                  💻 技术部
                </a-select-option>
                <a-select-option value="人事部">
                  👥 人事部
                </a-select-option>
                <a-select-option value="财务部">
                  💰 财务部
                </a-select-option>
                <a-select-option value="市场部">
                  📈 市场部
                </a-select-option>
                <a-select-option value="销售部">
                  🎯 销售部
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>

          <a-col :span="12">
            <a-form-item label="职位名称" name="position">
              <a-input 
                v-model:value="formState.position" 
                placeholder="请输入职位" 
                size="large"
              >
                <template #prefix>
                  <AimOutlined style="color: #bfbfbf;" />
                </template>
              </a-input>
            </a-form-item>
          </a-col>
        </a-row>

        <div class="form-actions">
          <a-space :size="16">
            <a-button type="primary" @click="onSubmit" size="large" class="submit-btn" :loading="loading">
              <CheckOutlined />
              {{ isEdit ? '更新信息' : '创建员工' }}
            </a-button>
            <a-button @click="onCancel" size="large" class="cancel-btn">
              <CloseCircleOutlined />
              取消
            </a-button>
          </a-space>
        </div>
      </a-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { message } from 'ant-design-vue';
import {
  UserAddOutlined,
  EditOutlined,
  UserOutlined,
  PhoneOutlined,
  MailOutlined,
  HomeOutlined,
  AimOutlined,
  CheckOutlined,
  CloseCircleOutlined
} from '@ant-design/icons-vue';
import { useEmployeeStore } from '../../store/employee';
import dayjs from 'dayjs';

const route = useRoute();
const router = useRouter();
const employeeStore = useEmployeeStore();
const formRef = ref(null);
const dateFormat = 'YYYY-MM-DD';
const loading = ref(false);

// 判断是否为编辑模式
const isEdit = computed(() => route.params.id !== undefined);
const employeeId = computed(() => route.params.id);

// 表单数据
const formState = reactive({
  name: '',
  age: 18,
  gender: '男',
  phone: '',
  email: '',
  department: '',
  position: '',
  hireDate: null
});

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度应为2-20个字符', trigger: 'blur' }
  ],
  age: [
    { required: true, message: '请输入年龄', trigger: 'change' },
    { type: 'number', min: 18, max: 100, message: '年龄应在18-100岁之间', trigger: 'change' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  phone: [
    { required: true, message: '请输入电话号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号码', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
  ],
  department: [
    { required: true, message: '请选择部门', trigger: 'change' }
  ],
  position: [
    { required: true, message: '请输入职位', trigger: 'blur' }
  ],
  hireDate: [
    { required: true, message: '请选择入职日期', trigger: 'change' }
  ]
};

// 获取员工数据
onMounted(async () => {
  if (isEdit.value) {
    try {
      await employeeStore.fetchEmployeeById(employeeId.value);
      const employee = employeeStore.currentEmployee;
      
      if (employee) {
        // 填充表单数据
        Object.keys(formState).forEach(key => {
          if (key === 'hireDate' && employee[key]) {
            formState[key] = dayjs(employee[key]);
          } else if (employee[key] !== undefined && employee[key] !== null) {
            formState[key] = employee[key];
          }
        });
      }
    } catch (error) {
      message.error('获取员工信息失败');
      router.push('/employees');
    }
  }
});

// 提交表单
const onSubmit = () => {
  formRef.value.validate().then(async () => {
    loading.value = true;
    try {
      // 处理日期格式
      const formData = { ...formState };
      if (formData.hireDate) {
        formData.hireDate = dayjs(formData.hireDate).format('YYYY-MM-DD');
      }
      
      if (isEdit.value) {
        await employeeStore.editEmployee(employeeId.value, formData);
        message.success('更新成功！');
      } else {
        await employeeStore.createEmployee(formData);
        message.success('添加成功！');
      }
      
      setTimeout(() => {
        router.push('/employees');
      }, 500);
    } catch (error) {
      message.error(error.message || '操作失败');
    } finally {
      loading.value = false;
    }
  }).catch(error => {
    console.log('验证失败:', error);
  });
};

// 取消操作
const onCancel = () => {
  router.push('/employees');
};
</script>

<style scoped>
.employee-form {
  max-width: 1100px;
  margin: 0 auto;
  animation: fadeInUp 0.4s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.form-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 12px;
  padding: 20px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 10px;
  box-shadow: 0 3px 14px rgba(102, 126, 234, 0.25);
}

.header-icon {
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  font-size: 26px;
  color: white;
  backdrop-filter: blur(10px);
}

.header-text h2 {
  color: white;
  font-size: 22px;
  font-weight: 600;
  margin: 0 0 6px 0 !important;
}

.header-text p {
  color: rgba(255, 255, 255, 0.9);
  margin: 0;
  font-size: 13px;
}

.form-card {
  background: white;
  padding: 24px;
  border-radius: 10px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.03);
}

.employee-form-content :deep(.ant-form-item-label > label) {
  font-weight: 500;
  color: #595959;
  font-size: 14px;
}

.employee-form-content :deep(.ant-input),
.employee-form-content :deep(.ant-input-number),
.employee-form-content :deep(.ant-select-selector),
.employee-form-content :deep(.ant-picker) {
  border-radius: 8px;
  border: 2px solid #f0f0f0;
  transition: all 0.3s;
}

.employee-form-content :deep(.ant-input:focus),
.employee-form-content :deep(.ant-input-number:focus),
.employee-form-content :deep(.ant-select-focused .ant-select-selector),
.employee-form-content :deep(.ant-picker-focused) {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.gender-group {
  width: 100%;
}

.gender-group :deep(.ant-radio-button-wrapper) {
  flex: 1;
  text-align: center;
  border-radius: 8px !important;
  font-weight: 500;
  height: 42px;
  line-height: 38px;
}

.section-divider {
  margin: 20px 0 16px 0 !important;
  font-weight: 600;
  color: #667eea;
  font-size: 14px;
}

.form-actions {
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
  text-align: center;
}

.submit-btn {
  min-width: 140px;
  height: 40px;
  font-size: 13px;
  font-weight: 600;
  border-radius: 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 3px 10px rgba(102, 126, 234, 0.3);
  transition: all 0.3s;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.45);
}

.cancel-btn {
  min-width: 120px;
  height: 48px;
  font-size: 15px;
  font-weight: 500;
  border-radius: 10px;
  border: 2px solid #d9d9d9;
  transition: all 0.3s;
}

.cancel-btn:hover {
  border-color: #667eea;
  color: #667eea;
  transform: translateY(-2px);
}

@media (max-width: 992px) {
  .ant-col {
    width: 100% !important;
    max-width: 100% !important;
    flex: 0 0 100% !important;
  }

  .form-card {
    padding: 18px;
  }

  .form-header {
    flex-direction: column;
    text-align: center;
    padding: 18px 20px;
  }
}
</style>