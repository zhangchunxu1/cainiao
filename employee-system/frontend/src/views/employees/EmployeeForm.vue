<template>
  <div class="employee-form">
    <h2>{{ isEdit ? '编辑员工' : '添加员工' }}</h2>
    
    <a-form
      :model="formState"
      :rules="rules"
      ref="formRef"
      :label-col="{ span: 6 }"
      :wrapper-col="{ span: 14 }"
    >
      <a-form-item label="姓名" name="name">
        <a-input v-model:value="formState.name" placeholder="请输入姓名" />
      </a-form-item>
      
      <a-form-item label="年龄" name="age">
        <a-input-number v-model:value="formState.age" :min="18" :max="100" style="width: 100%" />
      </a-form-item>
      
      <a-form-item label="性别" name="gender">
        <a-radio-group v-model:value="formState.gender">
          <a-radio value="男">男</a-radio>
          <a-radio value="女">女</a-radio>
        </a-radio-group>
      </a-form-item>
      
      <a-form-item label="电话" name="phone">
        <a-input v-model:value="formState.phone" placeholder="请输入电话号码" />
      </a-form-item>
      
      <a-form-item label="邮箱" name="email">
        <a-input v-model:value="formState.email" placeholder="请输入邮箱地址" />
      </a-form-item>
      
      <a-form-item label="部门" name="department">
        <a-select v-model:value="formState.department" placeholder="请选择部门">
          <a-select-option value="技术部">技术部</a-select-option>
          <a-select-option value="人事部">人事部</a-select-option>
          <a-select-option value="财务部">财务部</a-select-option>
          <a-select-option value="市场部">市场部</a-select-option>
          <a-select-option value="销售部">销售部</a-select-option>
        </a-select>
      </a-form-item>
      
      <a-form-item label="职位" name="position">
        <a-input v-model:value="formState.position" placeholder="请输入职位" />
      </a-form-item>
      
      <a-form-item label="入职日期" name="hireDate">
        <a-date-picker 
          v-model:value="formState.hireDate" 
          style="width: 100%" 
          :format="dateFormat"
          value-format="YYYY-MM-DD"
        />
      </a-form-item>
      
      <a-form-item :wrapper-col="{ offset: 6, span: 14 }">
        <a-space>
          <a-button type="primary" @click="onSubmit">保存</a-button>
          <a-button @click="onCancel">取消</a-button>
        </a-space>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { message } from 'ant-design-vue';
import { useEmployeeStore } from '../../store/employee';
import dayjs from 'dayjs';

const route = useRoute();
const router = useRouter();
const employeeStore = useEmployeeStore();
const formRef = ref(null);
const dateFormat = 'YYYY-MM-DD';

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
    try {
      // 处理日期格式
      const formData = { ...formState };
      if (formData.hireDate) {
        formData.hireDate = dayjs(formData.hireDate).format('YYYY-MM-DD');
      }
      
      if (isEdit.value) {
        await employeeStore.editEmployee(employeeId.value, formData);
        message.success('更新成功');
      } else {
        await employeeStore.createEmployee(formData);
        message.success('添加成功');
      }
      
      router.push('/employees');
    } catch (error) {
      message.error(error.message || '操作失败');
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
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}
</style> 