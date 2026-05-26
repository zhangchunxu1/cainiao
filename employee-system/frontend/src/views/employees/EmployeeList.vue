<template>
  <div class="employee-list">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">
          <UnorderedListOutlined />
          员工列表
        </h2>
        <p class="page-subtitle">管理和查看所有员工信息</p>
      </div>
      <div class="header-right">
        <a-button type="primary" @click="$router.push('/employees/add')" class="add-button" size="large">
          <PlusOutlined />
          添加员工
        </a-button>
      </div>
    </div>

    <!-- 搜索卡片 -->
    <div class="search-card">
      <a-form layout="inline" class="search-form">
        <a-form-item>
          <a-input
            v-model:value="searchKeyword"
            placeholder="搜索姓名/部门/职位"
            allowClear
            size="large"
            class="search-input"
            @pressEnter="handleSearch"
          >
            <template #prefix>
              <SearchOutlined style="color: #bfbfbf;" />
            </template>
          </a-input>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleSearch" size="large" class="search-button">
            <SearchOutlined />
            搜索
          </a-button>
        </a-form-item>
        <a-form-item>
          <a-button @click="handleReset" size="large" class="reset-button">
            <ReloadOutlined />
            重置
          </a-button>
        </a-form-item>
      </a-form>
    </div>

    <!-- 表格卡片 -->
    <div class="table-card">
      <!-- 表格标题栏 -->
      <div class="table-title-bar">
        <div class="title-info">
          <UnorderedListOutlined class="title-icon" />
          <span>员工数据列表</span>
          <a-tag color="blue" class="count-tag">{{ employeeTotal }} 条记录</a-tag>
        </div>
        <div class="table-actions">
          <a-tooltip title="刷新数据">
            <a-button shape="circle" size="small" @click="fetchEmployees" :loading="loading">
              <ReloadOutlined />
            </a-button>
          </a-tooltip>
        </div>
      </div>

      <!-- 表格主体 -->
      <a-table
        :dataSource="employeeRecords"
        :columns="columns"
        :loading="loading"
        rowKey="id"
        :scroll="{ x: 1400 }"
        :pagination="false"
        class="employee-table"
        :locale="{ emptyText: '暂无员工数据' }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'action'">
            <a-space>
              <a-tooltip title="编辑员工">
                <a-button type="link" @click="handleEdit(record)" class="edit-btn">
                  <EditOutlined />
                  编辑
                </a-button>
              </a-tooltip>
              <a-popconfirm
                title="确定要删除这条记录吗？"
                ok-text="确定"
                cancel-text="取消"
                @confirm="handleDelete(record)"
                okType="danger"
              >
                <a-tooltip title="删除员工">
                  <a-button type="link" danger class="delete-btn">
                    <DeleteOutlined />
                    删除
                  </a-button>
                </a-tooltip>
              </a-popconfirm>
            </a-space>
          </template>
          <template v-else-if="column.key === 'hireDate'">
            <span class="date-cell">
              <CalendarOutlined />
              {{ formatDate(record.hireDate) }}
            </span>
          </template>
          <template v-else-if="column.key === 'gender'">
            <a-tag :color="record.gender === '男' ? 'blue' : 'pink'" class="gender-tag">
              {{ record.gender === '男' ? '👨' : '👩' }} {{ record.gender }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'department'">
            <a-tag color="processing" class="dept-tag">
              {{ getDepartmentIcon(record.department) }} {{ record.department }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'name'">
            <div class="name-cell">
              <a-avatar :size="36" :style="{ backgroundColor: getAvatarColor(record.name), fontSize: '14px', fontWeight: '600' }">
                {{ record.name.charAt(0) }}
              </a-avatar>
              <span class="name-text">{{ record.name }}</span>
            </div>
          </template>
          <template v-else-if="column.key === 'position'">
            <span class="position-cell">
              <AimOutlined />
              {{ record.position }}
            </span>
          </template>
          <template v-else-if="column.key === 'phone'">
            <span class="contact-cell">
              <PhoneOutlined />
              {{ record.phone }}
            </span>
          </template>
          <template v-else-if="column.key === 'email'">
            <span class="contact-cell">
              <MailOutlined />
              {{ record.email }}
            </span>
          </template>
        </template>
      </a-table>

      <!-- 分页组件 -->
      <div class="pagination-wrapper" v-if="employeeTotal > 0">
        <div class="pagination-info">
          共 <strong>{{ employeeTotal }}</strong> 条记录，
          当前第 <strong>{{ currentPage }}</strong> / {{ Math.ceil(employeeTotal / pageSize) || 1 }} 页
        </div>
        <a-pagination
          :current="currentPage"
          :pageSize="pageSize"
          :total="employeeTotal"
          showSizeChanger
          showQuickJumper
          :showTotal="(total, range) => `${range[0]}-${range[1]} of ${total} items`"
          :pageSizeOptions="['10', '20', '50', '100']"
          @change="handlePageChange"
          @showSizeChange="handlePageChange"
          class="custom-pagination"
        >
          <template #buildOptionText="props">
            <span>{{ props.value }} 条/页</span>
          </template>
        </a-pagination>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { message } from 'ant-design-vue';
import {
  TeamOutlined,
  PlusOutlined,
  SearchOutlined,
  ReloadOutlined,
  EditOutlined,
  DeleteOutlined,
  CalendarOutlined,
  HomeOutlined,
  UnorderedListOutlined,
  AimOutlined,
  PhoneOutlined,
  MailOutlined
} from '@ant-design/icons-vue';
import { useEmployeeStore } from '../../store/employee';
import dayjs from 'dayjs';

const router = useRouter();
const employeeStore = useEmployeeStore();

const loading = computed(() => employeeStore.loading);
const employeeRecords = computed(() => employeeStore.employees.records);
const employeeTotal = computed(() => employeeStore.employees.total);

// 计算属性：当前页和每页大小
const currentPage = computed({
  get: () => employeeStore.employees.current,
  set: (val) => employeeStore.employees.current = val
});

const pageSize = computed({
  get: () => employeeStore.employees.size,
  set: (val) => employeeStore.employees.size = val
});

const searchKeyword = ref('');

const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    key: 'id',
    width: 70,
    align: 'center',
    sorter: (a, b) => a.id - b.id,
    customRender: ({ text }) => `#${text}`
  },
  {
    title: '姓名',
    dataIndex: 'name',
    key: 'name',
    width: 140,
    fixed: 'left'
  },
  {
    title: '年龄',
    dataIndex: 'age',
    key: 'age',
    width: 80,
    align: 'center',
    sorter: (a, b) => a.age - b.age
  },
  {
    title: '性别',
    dataIndex: 'gender',
    key: 'gender',
    width: 100,
    align: 'center'
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
    width: 220
  },
  {
    title: '部门',
    dataIndex: 'department',
    key: 'department',
    width: 120
  },
  {
    title: '职位',
    dataIndex: 'position',
    key: 'position',
    width: 140
  },
  {
    title: '入职日期',
    dataIndex: 'hireDate',
    key: 'hireDate',
    width: 130,
    sorter: (a, b) => new Date(a.hireDate) - new Date(b.hireDate)
  },
  {
    title: '操作',
    key: 'action',
    fixed: 'right',
    width: 160,
    align: 'center'
  }
];

const colors = ['#667eea', '#f093fb', '#4facfe', '#43e97b', '#fa709a', '#fee140', '#30cfd0'];

const getAvatarColor = (name) => {
  if (!name) return colors[0];
  const index = name.charCodeAt(0) % colors.length;
  return colors[index];
};

const getDepartmentIcon = (dept) => {
  const icons = {
    '技术部': '💻',
    '人事部': '👥',
    '财务部': '💰',
    '市场部': '📈',
    '销售部': '🎯'
  };
  return icons[dept] || '🏢';
};

onMounted(async () => {
  await fetchEmployees();
});

const fetchEmployees = async () => {
  try {
    await employeeStore.fetchEmployees({
      page: currentPage.value,
      pageSize: pageSize.value,
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

const handleReset = () => {
  searchKeyword.value = '';
  currentPage.value = 1;
  fetchEmployees();
};

const handlePageChange = (page, size) => {
  currentPage.value = page;
  if (size) {
    pageSize.value = size;
  }
  fetchEmployees();
};

const handleEdit = (record) => {
  router.push(`/employees/edit/${record.id}`);
};

const handleDelete = async (record) => {
  try {
    const success = await employeeStore.removeEmployee(record.id);
    if (success) {
      message.success('✅ 删除成功');
    }
  } catch (error) {
    message.error('❌ 删除失败');
  }
};

const formatDate = (date) => {
  if (!date) return '-';
  return dayjs(date).format('YYYY-MM-DD');
};
</script>

<style scoped>
.employee-list {
  width: 100%;
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

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding: 20px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 10px;
  box-shadow: 0 3px 14px rgba(102, 126, 234, 0.25);
}

.header-left {
  color: white;
}

.page-title {
  font-size: 22px;
  font-weight: 600;
  margin: 0 0 6px 0 !important;
  display: flex;
  align-items: center;
  gap: 10px;
}

.page-title .anticon {
  font-size: 26px;
}

.page-subtitle {
  margin: 0 !important;
  opacity: 0.9;
  font-size: 13px;
  letter-spacing: 0.5px;
}

.add-button {
  height: 40px;
  padding: 0 22px;
  font-size: 13px;
  font-weight: 600;
  border-radius: 8px;
  background: white;
  color: #667eea;
  border: none;
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.15);
  transition: all 0.3s;
}

.add-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.25);
  background: #f5f5f5;
  color: #764ba2;
}

/* 搜索卡片 */
.search-card {
  background: white;
  padding: 16px 20px;
  border-radius: 10px;
  margin-bottom: 12px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.03);
}

.search-form {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.search-input {
  width: 320px;
  border-radius: 8px;
}

.search-input :deep(.ant-input) {
  border-radius: 8px;
  height: 38px;
  font-size: 14px;
}

.search-input :deep(.ant-input:focus),
.search-input :deep(.ant-input-focused) {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.search-button {
  border-radius: 10px;
  height: 44px;
  padding: 0 28px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s;
}

.search-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.35);
}

.reset-button {
  border-radius: 10px;
  height: 44px;
  padding: 0 28px;
  font-weight: 500;
  transition: all 0.3s;
}

.reset-button:hover {
  border-color: #667eea;
  color: #667eea;
  transform: translateY(-2px);
}

/* 表格卡片 */
.table-card {
  background: white;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.03);
}

.table-title-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.title-info {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 15px;
  font-weight: 600;
  color: #262626;
}

.title-icon {
  font-size: 18px;
  color: #667eea;
}

.count-tag {
  border-radius: 10px;
  font-size: 12px;
  font-weight: 500;
}

.table-actions .ant-btn {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  transition: all 0.3s;
}

.table-actions .ant-btn:hover {
  background: #f0f0f0;
  transform: rotate(180deg);
}

/* 表格样式 */
.employee-table {
  border-radius: 12px;
  overflow: hidden;
}

.employee-table :deep(.ant-table-thead > tr > th) {
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%) !important;
  font-weight: 600;
  color: #262626;
  border-bottom: 2px solid #d9d9d9;
  font-size: 14px;
  padding: 16px 12px;
}

.employee-table :deep(.ant-table-tbody > tr) {
  transition: all 0.25s ease;
}

.employee-table :deep(.ant-table-tbody > tr:hover > td) {
  background: #fafbfc !important;
  transform: scale(1.01);
}

.employee-table :deep(.ant-table-tbody > tr > td) {
  padding: 14px 12px;
  transition: all 0.25s ease;
  vertical-align: middle;
}

.employee-table :deep(.ant-empty) {
  padding: 60px 0;
}

/* 单元格样式 */
.name-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.name-text {
  font-weight: 600;
  color: #262626;
  font-size: 14px;
}

.date-cell {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: #595959;
  font-size: 13px;
}

.date-cell .anticon {
  color: #1890ff;
  font-size: 14px;
}

.gender-tag {
  border-radius: 12px;
  font-weight: 500;
  font-size: 13px;
  padding: 4px 12px;
}

.dept-tag {
  border-radius: 12px;
  font-weight: 500;
  font-size: 13px;
  padding: 4px 12px;
}

.position-cell {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: #595959;
  font-size: 13px;
}

.position-cell .anticon {
  color: #52c41a;
  font-size: 13px;
}

.contact-cell {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: #595959;
  font-size: 13px;
}

.contact-cell .anticon {
  color: #1890ff;
  font-size: 13px;
}

.edit-btn {
  color: #1890ff !important;
  font-weight: 600;
  font-size: 13px;
  padding: 4px 8px;
  border-radius: 6px;
  transition: all 0.3s;
}

.edit-btn:hover {
  background: #e6f7ff !important;
  color: #40a9ff !important;
  transform: scale(1.05);
}

.delete-btn {
  color: #ff4d4f !important;
  font-weight: 600;
  font-size: 13px;
  padding: 4px 8px;
  border-radius: 6px;
  transition: all 0.3s;
}

.delete-btn:hover {
  background: #fff1f0 !important;
  color: #ff7875 !important;
  transform: scale(1.05);
}

/* 分页样式 */
.pagination-wrapper {
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}

.pagination-info {
  color: #8c8c8c;
  font-size: 13px;
}

.pagination-info strong {
  color: #262626;
  font-weight: 600;
}

.custom-pagination :deep(.ant-pagination-item-active a) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  border-color: transparent !important;
  color: white !important;
  font-weight: 600;
}

.custom-pagination :deep(.ant-pagination-item) {
  border-radius: 8px;
  transition: all 0.3s;
}

.custom-pagination :deep(.ant-pagination-item:hover:not(.ant-pagination-item-active)) {
  border-color: #667eea;
}

.custom-pagination :deep(.ant-pagination-prev),
.custom-pagination :deep(.ant-pagination-next) {
  border-radius: 8px;
}

.custom-pagination :deep(.ant-select-selector) {
  border-radius: 8px !important;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .page-header {
    flex-direction: column;
    gap: 16px;
    text-align: center;
    padding: 18px 20px;
  }

  .search-input {
    width: 100%;
  }

  .pagination-wrapper {
    flex-direction: column;
    text-align: center;
  }
}

@media (max-width: 768px) {
  .table-card {
    padding: 14px;
  }

  .table-title-bar {
    flex-direction: column;
    gap: 10px;
  }
}
</style>