<template>
  <div class="department-list">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">
          <ApartmentOutlined />
          部门列表
        </h2>
        <p class="page-subtitle">管理和查看所有部门信息</p>
      </div>
      <div class="header-right">
        <a-button type="primary" @click="showAddModal" size="large">
          <PlusOutlined />
          添加部门
        </a-button>
      </div>
    </div>

    <!-- 搜索卡片 -->
    <div class="search-card">
      <a-form layout="inline" class="search-form">
        <a-form-item>
          <a-input
            v-model:value="searchKeyword"
            placeholder="搜索部门名称/负责人"
            allowClear
            size="large"
            @pressEnter="handleSearch"
          >
            <template #prefix>
              <SearchOutlined style="color: #bfbfbf;" />
            </template>
          </a-input>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleSearch" size="large">
            <SearchOutlined />
            搜索
          </a-button>
        </a-form-item>
        <a-form-item>
          <a-button @click="handleReset" size="large">
            <ReloadOutlined />
            重置
          </a-button>
        </a-form-item>
      </a-form>
    </div>

    <!-- 表格卡片 -->
    <div class="table-card">
      <div class="table-title-bar">
        <div class="title-info">
          <ApartmentOutlined class="title-icon" />
          <span>部门数据列表</span>
          <a-tag color="blue">{{ departmentTotal }} 个部门</a-tag>
        </div>
      </div>

      <a-table
        :dataSource="departmentRecords"
        :columns="columns"
        :loading="loading"
        rowKey="id"
        :pagination="false"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'action'">
            <a-space>
              <a-button type="link" @click="handleEdit(record)">
                <EditOutlined /> 编辑
              </a-button>
              <a-popconfirm
                title="确定要删除这个部门吗？"
                ok-text="确定"
                cancel-text="取消"
                @confirm="handleDelete(record)"
              >
                <a-button type="link" danger>
                  <DeleteOutlined /> 删除
                </a-button>
              </a-popconfirm>
            </a-space>
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag :color="record.status === 0 ? 'green' : 'red'">
              {{ record.status === 0 ? '正常' : '停用' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'employeeCount'">
            <a-badge :count="record.employeeCount || 0" :numberStyle="{ backgroundColor: '#667eea' }" />
          </template>
        </template>
      </a-table>

      <!-- 分页 -->
      <div class="pagination-wrapper" v-if="departmentTotal > 0">
        <a-pagination
          v-model:current="currentPage"
          :total="departmentTotal"
          :pageSize="pageSize"
          showSizeChanger
          showQuickJumper
          :showTotal="(total) => `共 ${total} 条记录`"
          @change="handlePageChange"
          @showSizeChange="handleSizeChange"
        />
      </div>
    </div>

    <!-- 添加/编辑弹窗 -->
    <a-modal
      v-model:open="modalVisible"
      :title="modalTitle"
      width="600px"
      @ok="handleSubmit"
      @cancel="handleCancel"
      :confirmLoading="submitLoading"
    >
      <a-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        layout="vertical"
      >
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="部门名称" name="name">
              <a-input v-model:value="formData.name" placeholder="请输入部门名称" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="负责人" name="manager">
              <a-input v-model:value="formData.manager" placeholder="请输入负责人姓名" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="联系电话" name="phone">
              <a-input v-model:value="formData.phone" placeholder="请输入联系电话" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="状态" name="status">
              <a-select v-model:value="formData.status" placeholder="请选择状态">
                <a-select-option :value="0">正常</a-select-option>
                <a-select-option :value="1">停用</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>

        <a-form-item label="部门描述" name="description">
          <a-textarea v-model:value="formData.description" placeholder="请输入部门描述" :rows="4" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { message } from 'ant-design-vue'
import {
  ApartmentOutlined,
  PlusOutlined,
  SearchOutlined,
  ReloadOutlined,
  EditOutlined,
  DeleteOutlined
} from '@ant-design/icons-vue'
import { departmentApi } from '../../api/department'

const loading = ref(false)
const submitLoading = ref(false)
const modalVisible = ref(false)
const isEdit = ref(false)
const currentId = ref(null)

const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const departmentTotal = ref(0)
const departmentRecords = ref([])

const formRef = ref()
const formData = reactive({
  name: '',
  manager: '',
  phone: '',
  description: '',
  status: 0,
  employeeCount: 0
})

const formRules = {
  name: [{ required: true, message: '请输入部门名称', trigger: 'blur' }]
}

const modalTitle = computed(() => isEdit.value ? '编辑部门' : '添加部门')

const columns = [
  {
    title: '部门名称',
    dataIndex: 'name',
    key: 'name',
    ellipsis: true
  },
  {
    title: '负责人',
    dataIndex: 'manager',
    key: 'manager'
  },
  {
    title: '联系电话',
    dataIndex: 'phone',
    key: 'phone'
  },
  {
    title: '员工人数',
    dataIndex: 'employeeCount',
    key: 'employeeCount',
    align: 'center'
  },
  {
    title: '描述',
    dataIndex: 'description',
    key: 'description',
    ellipsis: true
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    align: 'center'
  },
  {
    title: '操作',
    key: 'action',
    align: 'center',
    width: 200
  }
]

const fetchDepartments = async () => {
  loading.value = true
  try {
    const res = await departmentApi.getDepartmentList({
      page: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchKeyword.value || undefined
    })
    const data = res.data.data
    departmentRecords.value = data.records || []
    departmentTotal.value = data.total || 0
  } catch (error) {
    message.error('获取部门列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchDepartments()
}

const handleReset = () => {
  searchKeyword.value = ''
  currentPage.value = 1
  fetchDepartments()
}

const handlePageChange = (page, size) => {
  currentPage.value = page
  pageSize.value = size
  fetchDepartments()
}

const handleSizeChange = (current, size) => {
  pageSize.value = size
  currentPage.value = 1
  fetchDepartments()
}

const showAddModal = () => {
  isEdit.value = false
  currentId.value = null
  Object.assign(formData, {
    name: '',
    manager: '',
    phone: '',
    description: '',
    status: 0,
    employeeCount: 0
  })
  modalVisible.value = true
}

const handleEdit = (record) => {
  isEdit.value = true
  currentId.value = record.id
  Object.assign(formData, {
    name: record.name,
    manager: record.manager,
    phone: record.phone,
    description: record.description,
    status: record.status,
    employeeCount: record.employeeCount
  })
  modalVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitLoading.value = true

    if (isEdit.value) {
      await departmentApi.updateDepartment(currentId.value, formData)
      message.success('更新成功')
    } else {
      await departmentApi.addDepartment(formData)
      message.success('添加成功')
    }

    modalVisible.value = false
    fetchDepartments()
  } catch (error) {
    if (error.message) {
      message.error(error.message)
    }
  } finally {
    submitLoading.value = false
  }
}

const handleCancel = () => {
  modalVisible.value = false
  formRef.value?.resetFields()
}

const handleDelete = async (record) => {
  try {
    await departmentApi.deleteDepartment(record.id)
    message.success('删除成功')
    fetchDepartments()
  } catch (error) {
    message.error(error.message || '删除失败')
  }
}

onMounted(() => {
  fetchDepartments()
})
</script>

<style scoped>
.department-list {
  padding: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.header-left .page-title {
  font-size: 24px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-left .page-subtitle {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
}

.search-card {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.table-card {
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
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
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}

.title-icon {
  color: #667eea;
  font-size: 18px;
}

.pagination-wrapper {
  margin-top: 24px;
  text-align: right;
}
</style>