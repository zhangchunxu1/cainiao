<template>
  <div class="employee-list">
    <div class="table-header">
      <h2>员工列表</h2>
      <a-button type="primary" @click="$router.push('/employees/add')">
        添加员工
      </a-button>
    </div>
    
    <a-form layout="inline" class="search-form">
      <a-form-item>
        <a-input v-model:value="searchKeyword" placeholder="搜索姓名/部门/职位" allowClear />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" @click="handleSearch">搜索</a-button>
      </a-form-item>
    </a-form>
    
    <a-table
      :dataSource="employeeRecords"
      :columns="columns"
      :loading="loading"
      rowKey="id"
      :pagination="{
        showSizeChanger: true,
        showQuickJumper: true,
        showTotal: (total) => `共 ${total} 条记录`,
        total: employeeTotal,
        current: currentPage,
        pageSize: pageSize,
        onChange: handlePageChange,
        onShowSizeChange: handlePageChange
      }"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'action'">
          <a-space>
            <a-button type="link" @click="handleEdit(record)">编辑</a-button>
            <a-popconfirm
              title="确定要删除这条记录吗?"
              ok-text="确定"
              cancel-text="取消"
              @confirm="handleDelete(record)"
            >
              <a-button type="link" danger>删除</a-button>
            </a-popconfirm>
          </a-space>
        </template>
        <template v-else-if="column.key === 'hireDate'">
          {{ formatDate(record.hireDate) }}
        </template>
      </template>
    </a-table>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { message } from 'ant-design-vue';
import { useEmployeeStore } from '../../store/employee';
import dayjs from 'dayjs';

const router = useRouter();
const employeeStore = useEmployeeStore();

const loading = computed(() => employeeStore.loading);
const employeeRecords = computed(() => employeeStore.employees.records);
const employeeTotal = computed(() => employeeStore.employees.total);
const searchKeyword = ref('');
const currentPage = computed({
  get: () => employeeStore.employees.current,
  set: (val) => employeeStore.employees.current = val
});
const pageSize = computed({
  get: () => employeeStore.employees.size,
  set: (val) => employeeStore.employees.size = val
});

const columns = [
  {
    title: '员工ID',
    dataIndex: 'id',
    key: 'id',
    width: 100
  },
  {
    title: '姓名',
    dataIndex: 'name',
    key: 'name',
    width: 150
  },
  {
    title: '年龄',
    dataIndex: 'age',
    key: 'age',
    width: 100
  },
  {
    title: '性别',
    dataIndex: 'gender',
    key: 'gender',
    width: 100
  },
  {
    title: '电话',
    dataIndex: 'phone',
    key: 'phone',
    width: 150
  },
  {
    title: '邮箱',
    dataIndex: 'email',
    key: 'email',
    width: 200
  },
  {
    title: '部门',
    dataIndex: 'department',
    key: 'department',
    width: 150
  },
  {
    title: '职位',
    dataIndex: 'position',
    key: 'position',
    width: 150
  },
  {
    title: '入职日期',
    dataIndex: 'hireDate',
    key: 'hireDate',
    width: 150
  },
  {
    title: '操作',
    key: 'action',
    fixed: 'right',
    width: 150
  }
];

onMounted(async () => {
  await fetchEmployees();
});

const fetchEmployees = async () => {
  try {
    await employeeStore.fetchEmployees({
      current: currentPage.value,
      size: pageSize.value,
      keyword: searchKeyword.value
    });
  } catch (error) {
    message.error('获取员工列表失败');
  }
};

const handleSearch = () => {
  currentPage.value = 1;
  fetchEmployees();
};

const handlePageChange = (page, size) => {
  currentPage.value = page;
  pageSize.value = size || pageSize.value;
  fetchEmployees();
};

const handleEdit = (record) => {
  router.push(`/employees/edit/${record.id}`);
};

const handleDelete = async (record) => {
  try {
    const success = await employeeStore.removeEmployee(record.id);
    if (success) {
      message.success('删除成功');
    }
  } catch (error) {
    message.error('删除失败');
  }
};

const formatDate = (date) => {
  if (!date) return '';
  return dayjs(date).format('YYYY-MM-DD');
};
</script>

<style scoped>
.employee-list {
  width: 100%;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-form {
  margin-bottom: 20px;
}

:deep(.ant-table-pagination) {
  margin: 16px 0;
  justify-content: flex-end;
}
</style> 