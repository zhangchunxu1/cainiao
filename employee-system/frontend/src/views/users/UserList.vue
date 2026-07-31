<template>
  <div class="user-list">
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">
          <UserOutlined />
          账号管理
        </h2>
        <p class="page-subtitle">管理系统登录账号和角色权限</p>
      </div>
      <div class="header-right">
        <a-button v-if="selectedRowKeys.length > 0" danger @click="handleBatchDelete" :loading="batchDeleteLoading" size="large" style="margin-right: 12px;">
          <DeleteOutlined /> 批量删除 ({{ selectedRowKeys.length }})
        </a-button>
        <a-button type="primary" @click="$router.push('/users/add')" class="add-button" size="large">
          <PlusOutlined />
          添加账号
        </a-button>
      </div>
    </div>

    <div class="search-card">
      <a-form layout="inline" class="search-form">
        <a-form-item>
          <a-input
            v-model:value="searchKeyword"
            placeholder="搜索用户名/姓名/角色"
            allowClear
            size="large"
            class="search-input"
            @pressEnter="handleSearch"
          />
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

    <div class="table-card">
      <div class="table-title-bar">
        <div class="title-info">
          <UserOutlined class="title-icon" />
          <span>账号列表</span>
          <a-tag color="blue" class="count-tag">{{ userTotal }} 条记录</a-tag>
        </div>
        <div class="table-actions">
          <a-tooltip title="刷新数据">
            <a-button shape="circle" size="small" @click="fetchUsers" :loading="loading">
              <ReloadOutlined />
            </a-button>
          </a-tooltip>
        </div>
      </div>

      <a-table
        :dataSource="userRecords"
        :columns="columns"
        :loading="loading"
        rowKey="id"
        :pagination="false"
        :scroll="{ x: 800 }"
        :rowSelection="rowSelection"
        class="user-table"
        :locale="{ emptyText: '暂无用户数据' }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'action'">
            <div style="display: flex; align-items: center; justify-content: center;">
              <a-space>
              <a-tooltip title="编辑">
                <a-button type="link" @click="handleEdit(record)" class="edit-btn">
                  <EditOutlined />
                  编辑
                </a-button>
              </a-tooltip>
              <a-popconfirm
                v-if="record.id !== 1"
                title="确定要删除该账号吗？"
                ok-text="确定"
                cancel-text="取消"
                @confirm="handleDelete(record)"
                okType="danger"
              >
                <a-tooltip title="删除">
                  <a-button type="link" danger class="delete-btn">
                    <DeleteOutlined />
                    删除
                  </a-button>
                </a-tooltip>
              </a-popconfirm>
              <a-popconfirm
                title="确定要重置密码为 123456 吗？"
                ok-text="确定"
                cancel-text="取消"
                @confirm="handleResetPassword(record)"
              >
                <a-tooltip title="重置密码">
                  <a-button type="link" class="reset-pwd-btn">
                    <KeyOutlined />
                    重置密码
                  </a-button>
                </a-tooltip>
              </a-popconfirm>
              </a-space>
            </div>
          </template>
          <template v-else-if="column.key === 'role'">
            <a-tag :color="record.role === 'admin' ? 'red' : (record.role === 'manager' ? 'orange' : 'blue')" class="role-tag">
              {{ record.role === 'admin' ? '👑 管理员' : (record.role === 'manager' ? '🏢 部门领导' : '👤 普通员工') }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'username'">
            <div class="name-cell">
              <a-avatar :size="36" :style="{ backgroundColor: getAvatarColor(record.username), fontSize: '14px', fontWeight: '600' }">
                {{ record.username.charAt(0).toUpperCase() }}
              </a-avatar>
              <span class="name-text">{{ record.username }}</span>
            </div>
          </template>
        </template>
      </a-table>

      <div class="pagination-wrapper" v-if="userTotal > 0">
        <div class="pagination-info">
          共 <strong>{{ userTotal }}</strong> 条记录，
          当前第 <strong>{{ currentPage }}</strong> / {{ Math.ceil(userTotal / pageSize) || 1 }} 页
        </div>
        <a-pagination
          :current="currentPage"
          :pageSize="pageSize"
          :total="userTotal"
          showSizeChanger
          showQuickJumper
          :pageSizeOptions="['10', '20', '50']"
          @change="handlePageChange"
          @showSizeChange="handlePageChange"
          class="custom-pagination"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { message, Modal } from 'ant-design-vue'
import {
  UserOutlined,
  PlusOutlined,
  SearchOutlined,
  ReloadOutlined,
  EditOutlined,
  DeleteOutlined,
  KeyOutlined
} from '@ant-design/icons-vue'
import { useUserStore } from '../../store/user'
import { userApi } from '../../api/user'

const router = useRouter()
const userStore = useUserStore()

const loading = computed(() => userStore.loading)
const userRecords = computed(() => userStore.users.records)
const userTotal = computed(() => userStore.users.total)

const currentPage = computed({
  get: () => userStore.users.current,
  set: (val) => userStore.users.current = val
})

const pageSize = computed({
  get: () => userStore.users.size,
  set: (val) => userStore.users.size = val
})

const searchKeyword = ref('')
const selectedRowKeys = ref([])
const batchDeleteLoading = ref(false)

const rowSelection = computed(() => ({
  selectedRowKeys: selectedRowKeys.value,
  onChange: (keys) => {
    selectedRowKeys.value = keys
  }
}))

const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    key: 'id',
    width: 70,
    align: 'center',
    customRender: ({ text }) => `#${text}`
  },
  {
    title: '用户名',
    dataIndex: 'username',
    key: 'username',
    width: 160,
    ellipsis: true
  },
  {
    title: '真实姓名',
    dataIndex: 'realName',
    key: 'realName',
    width: 140,
    ellipsis: true,
    customRender: ({ text }) => text || '-'
  },
  {
    title: '角色',
    dataIndex: 'role',
    key: 'role',
    width: 140,
    align: 'center'
  },
  {
    title: '操作',
    key: 'action',
    fixed: 'right',
    width: 240,
    align: 'center'
  }
]

const colors = ['#667eea', '#f093fb', '#4facfe', '#43e97b', '#fa709a', '#fee140']

const getAvatarColor = (name) => {
  if (!name) return colors[0]
  const index = name.charCodeAt(0) % colors.length
  return colors[index]
}

onMounted(async () => {
  await fetchUsers()
})

const fetchUsers = async () => {
  try {
    await userStore.fetchUsers({
      page: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchKeyword.value
    })
  } catch (error) {
    message.error('获取用户列表失败')
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchUsers()
}

const handleReset = () => {
  searchKeyword.value = ''
  currentPage.value = 1
  fetchUsers()
}

const handlePageChange = (page, size) => {
  currentPage.value = page
  if (size) pageSize.value = size
  fetchUsers()
}

const handleEdit = (record) => {
  router.push(`/users/edit/${record.id}`)
}

const handleDelete = async (record) => {
  try {
    await userStore.removeUser(record.id)
    message.success('删除成功')
  } catch (error) {
    message.error('删除失败')
  }
}

const handleResetPassword = async (record) => {
  try {
    await userApi.resetPassword(record.id)
    message.success(`已将 ${record.username} 的密码重置为 123456`)
  } catch (error) {
    message.error('重置失败')
  }
}

const handleBatchDelete = () => {
  Modal.confirm({
    title: '确认批量删除',
    content: `确定要删除选中的 ${selectedRowKeys.value.length} 条用户记录吗？此操作不可恢复。`,
    okText: '确定删除',
    okType: 'danger',
    cancelText: '取消',
    async onOk() {
      batchDeleteLoading.value = true;
      try {
        const count = await userStore.batchRemoveUsers(selectedRowKeys.value);
        message.success(`✅ 成功删除 ${count} 条记录`);
        selectedRowKeys.value = [];
      } catch (error) {
        message.error(error.message || '❌ 批量删除失败');
      } finally {
        batchDeleteLoading.value = false;
      }
    }
  });
};
</script>

<style scoped>
.user-list {
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
  margin-bottom: 12px;
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

.add-button {
  height: 40px;
  padding: 0 22px;
  font-size: 13px;
  font-weight: 600;
  border-radius: 8px;
  background: white;
  color: #f5576c;
  border: none;
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.15);
  transition: all 0.3s;
}

.add-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.25);
}

.search-card {
  background: white;
  padding: 16px 20px;
  border-radius: 10px;
  margin-bottom: 12px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.03);
}

.search-form { display: flex; gap: 10px; flex-wrap: wrap; }

.search-input { width: 320px; }

.search-input :deep(.ant-input) {
  border-radius: 8px;
  height: 38px;
  font-size: 14px;
}

.search-button {
  border-radius: 10px;
  height: 44px;
  padding: 0 28px;
  font-weight: 600;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  border: none;
  transition: all 0.3s;
}

.reset-button {
  border-radius: 10px;
  height: 44px;
  padding: 0 28px;
  font-weight: 500;
  transition: all 0.3s;
}

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

.title-icon { font-size: 18px; color: #f5576c; }

.count-tag { border-radius: 10px; font-size: 12px; }

.user-table :deep(.ant-table-thead > tr > th) {
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%) !important;
  font-weight: 600;
  color: #262626;
  border-bottom: 2px solid #d9d9d9;
  font-size: 14px;
  padding: 16px 12px;
}

.user-table :deep(.ant-table-tbody > tr:hover > td) {
  background: #fafbfc !important;
}

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

.role-tag {
  border-radius: 12px;
  font-weight: 500;
  font-size: 13px;
  padding: 4px 12px;
}

.edit-btn {
  color: #1890ff !important;
  font-weight: 600;
  font-size: 13px;
  padding: 4px 8px;
  border-radius: 6px;
}

.delete-btn {
  color: #ff4d4f !important;
  font-weight: 600;
  font-size: 13px;
  padding: 4px 8px;
  border-radius: 6px;
}

.reset-pwd-btn {
  color: #faad14 !important;
  font-weight: 600;
  font-size: 13px;
  padding: 4px 8px;
  border-radius: 6px;
}

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

.pagination-info { color: #8c8c8c; font-size: 13px; }

.pagination-info strong { color: #262626; font-weight: 600; }

.custom-pagination :deep(.ant-pagination-item-active a) {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%) !important;
  border-color: transparent !important;
  color: white !important;
  font-weight: 600;
}
</style>
