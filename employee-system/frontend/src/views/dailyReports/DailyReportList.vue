<template>
  <div class="daily-report-list">
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">
          <FileTextOutlined />
          日报管理
        </h2>
        <p class="page-subtitle">管理员工日报提交和审核</p>
      </div>
      <div class="header-right">
        <a-button type="primary" @click="showAddModal" size="large">
          <PlusOutlined />
          提交日报
        </a-button>
      </div>
    </div>

    <div class="stats-card">
      <a-row :gutter="16">
        <a-col :span="8">
          <div class="stat-item">
            <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
              <FileTextOutlined />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.submitted || 0 }}</div>
              <div class="stat-label">已提交</div>
            </div>
          </div>
        </a-col>
        <a-col :span="8">
          <div class="stat-item">
            <div class="stat-icon" style="background: linear-gradient(135deg, #30cfd0 0%, #330867 100%);">
              <CheckCircleOutlined />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.reviewed || 0 }}</div>
              <div class="stat-label">已审核</div>
            </div>
          </div>
        </a-col>
        <a-col :span="8">
          <div class="stat-item">
            <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
              <CalendarOutlined />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ reportTotal }}</div>
              <div class="stat-label">总记录</div>
            </div>
          </div>
        </a-col>
      </a-row>
    </div>

    <div class="search-card">
      <a-form layout="inline" class="search-form">
        <a-form-item>
          <a-input
            v-model:value="searchKeyword"
            placeholder="搜索员工姓名/部门"
            allowClear
            size="large"
            @pressEnter="handleSearch"
          />
        </a-form-item>
        <a-form-item label="状态：">
          <a-select
            v-model:value="selectedStatus"
            placeholder="选择状态"
            allowClear
            size="large"
            style="width: 150px;"
            @change="handleSearch"
          >
            <a-select-option value="已提交">已提交</a-select-option>
            <a-select-option value="已审核">已审核</a-select-option>
          </a-select>
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

    <div class="table-card">
      <div class="table-title-bar">
        <div class="title-info">
          <FileTextOutlined class="title-icon" />
          <span>日报列表</span>
          <a-tag color="blue">{{ reportTotal }} 条记录</a-tag>
        </div>
        <a-button v-if="selectedRowKeys.length > 0" danger @click="handleBatchDelete" :loading="batchDeleteLoading">
          <DeleteOutlined /> 批量删除 ({{ selectedRowKeys.length }})
        </a-button>
      </div>

      <a-table
        :dataSource="reports"
        :columns="columns"
        :loading="loading"
        rowKey="id"
        :pagination="false"
        :scroll="{ x: 1000 }"
        :rowSelection="rowSelection"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'status'">
            <a-tag :color="getStatusColor(record.status)">
              {{ record.status }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'action'">
            <div style="display: flex; align-items: center; justify-content: center; gap: 8px;">
              <a-tooltip title="查看详情">
                <a-button type="text" @click="handleViewDetail(record)">
                  <EyeOutlined />
                </a-button>
              </a-tooltip>

              <template v-if="record.status === '已提交' && (authStore.isAdmin || authStore.isManager)">
                <a-popconfirm
                  title="确定审核此日报吗？"
                  ok-text="确定"
                  cancel-text="取消"
                  @confirm="handleReview(record)"
                >
                  <a-button type="text" style="color: #52c41a;">
                    <CheckCircleOutlined />
                  </a-button>
                </a-popconfirm>
              </template>

              <template v-if="authStore.isAdmin || record.status === '已提交'">
                <a-popconfirm
                  title="确定要删除这条记录吗？"
                  ok-text="确定"
                  cancel-text="取消"
                  @confirm="handleDelete(record)"
                >
                  <a-button type="text" danger>
                    <DeleteOutlined />
                  </a-button>
                </a-popconfirm>
              </template>
            </div>
          </template>
        </template>
      </a-table>

      <div class="pagination-wrapper" v-if="reportTotal > 0">
        <a-pagination
          v-model:current="currentPage"
          :total="reportTotal"
          :pageSize="pageSize"
          showSizeChanger
          showQuickJumper
          :showTotal="(total) => `共 ${total} 条记录`"
          @change="handlePageChange"
          @showSizeChange="handleSizeChange"
        />
      </div>
    </div>

    <a-modal
      v-model:open="modalVisible"
      :title="modalTitle"
      width="700px"
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
            <a-form-item label="员工姓名" name="employeeName">
              <a-input v-model:value="formData.employeeName" placeholder="请输入员工姓名" disabled />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="部门" name="department">
              <a-input v-model:value="formData.department" placeholder="请输入部门" disabled />
            </a-form-item>
          </a-col>
        </a-row>

        <a-form-item label="日报日期" name="reportDate">
          <a-date-picker
            v-model:value="formData.reportDate"
            format="YYYY-MM-DD"
            valueFormat="YYYY-MM-DD"
            style="width: 100%;"
            placeholder="选择日报日期"
          />
        </a-form-item>

        <a-form-item label="今日工作内容" name="todayWork">
          <a-textarea v-model:value="formData.todayWork" placeholder="请输入今日工作内容" :rows="4" />
        </a-form-item>

        <a-form-item label="明日工作计划" name="tomorrowWork">
          <a-textarea v-model:value="formData.tomorrowWork" placeholder="请输入明日工作计划" :rows="3" />
        </a-form-item>

        <a-form-item label="问题与困难" name="issues">
          <a-textarea v-model:value="formData.issues" placeholder="请输入遇到的问题与困难" :rows="2" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="detailVisible"
      title="日报详情"
      width="750px"
      :footer="null"
    >
      <div v-if="currentRecord" class="detail-content">
        <a-descriptions bordered :column="2">
          <a-descriptions-item label="员工姓名">{{ currentRecord.employeeName }}</a-descriptions-item>
          <a-descriptions-item label="部门">{{ currentRecord.department }}</a-descriptions-item>
          <a-descriptions-item label="日报日期">{{ currentRecord.reportDate }}</a-descriptions-item>
          <a-descriptions-item label="状态">
            <a-tag :color="getStatusColor(currentRecord.status)">{{ currentRecord.status }}</a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="今日工作内容" :span="2">{{ currentRecord.todayWork }}</a-descriptions-item>
          <a-descriptions-item label="明日工作计划" :span="2">{{ currentRecord.tomorrowWork || '无' }}</a-descriptions-item>
          <a-descriptions-item label="问题与困难" :span="2">{{ currentRecord.issues || '无' }}</a-descriptions-item>

          <template v-if="currentRecord.status === '已审核'">
            <a-descriptions-item label="审核人">{{ currentRecord.reviewer }}</a-descriptions-item>
            <a-descriptions-item label="审核时间">{{ currentRecord.reviewTime }}</a-descriptions-item>
            <a-descriptions-item label="审核意见" :span="2">{{ currentRecord.reviewComment || '无' }}</a-descriptions-item>
          </template>
        </a-descriptions>
      </div>
    </a-modal>

    <a-modal
      v-model:open="reviewModalVisible"
      title="审核日报"
      width="500px"
      @ok="handleConfirmReview"
      @cancel="handleCancelReview"
      :confirmLoading="reviewLoading"
    >
      <a-form
        ref="reviewFormRef"
        :model="reviewForm"
        layout="vertical"
      >
        <a-form-item label="审核意见">
          <a-textarea v-model:value="reviewForm.comment" placeholder="请输入审核意见" :rows="4" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { message, Modal } from 'ant-design-vue'
import {
  FileTextOutlined,
  PlusOutlined,
  SearchOutlined,
  ReloadOutlined,
  CheckCircleOutlined,
  EyeOutlined,
  DeleteOutlined,
  CalendarOutlined
} from '@ant-design/icons-vue'
import { dailyReportApi } from '../../api/dailyReport'
import { useAuthStore } from '../../store/auth'

const loading = ref(false)
const submitLoading = ref(false)
const reviewLoading = ref(false)
const modalVisible = ref(false)
const detailVisible = ref(false)
const reviewModalVisible = ref(false)
const selectedRowKeys = ref([])
const batchDeleteLoading = ref(false)

const searchKeyword = ref('')
const selectedStatus = ref(undefined)
const currentPage = ref(1)
const pageSize = ref(10)
const reportTotal = ref(0)
const reports = ref([])
const currentRecord = ref(null)
const authStore = useAuthStore()

const stats = reactive({
  submitted: 0,
  reviewed: 0
})

const formRef = ref()
const reviewFormRef = ref()
const formData = reactive({
  employeeId: authStore.userId ? Number(authStore.userId) : 1,
  employeeName: authStore.realName || authStore.username,
  department: authStore.department,
  reportDate: '',
  todayWork: '',
  tomorrowWork: '',
  issues: ''
})

const reviewForm = reactive({
  comment: ''
})

const formRules = {
  reportDate: [{ required: true, message: '请选择日报日期', trigger: 'change' }],
  todayWork: [{ required: true, message: '请输入今日工作内容', trigger: 'blur' }]
}

const modalTitle = computed(() => '提交日报')

const rowSelection = computed(() => ({
  selectedRowKeys: selectedRowKeys.value,
  onChange: (keys) => { selectedRowKeys.value = keys }
}))

const columns = [
  {
    title: '员工姓名',
    dataIndex: 'employeeName',
    key: 'employeeName',
    ellipsis: true
  },
  {
    title: '部门',
    dataIndex: 'department',
    key: 'department',
    ellipsis: true
  },
  {
    title: '日报日期',
    dataIndex: 'reportDate',
    key: 'reportDate',
    ellipsis: true
  },
  {
    title: '今日工作',
    dataIndex: 'todayWork',
    key: 'todayWork',
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
    width: 200,
    fixed: 'right'
  }
]

const getStatusColor = (status) => {
  const colors = {
    '已提交': 'orange',
    '已审核': 'green'
  }
  return colors[status] || 'default'
}

const fetchDailyReports = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value
    }

    if (searchKeyword.value) {
      params.keyword = searchKeyword.value
    }

    if (selectedStatus.value) {
      params.status = selectedStatus.value
    }

    const res = await dailyReportApi.getDailyReportList(params)
    const data = res.data.data
    reports.value = data.records || []
    reportTotal.value = data.total || 0

    stats.submitted = reports.value.filter(r => r.status === '已提交').length
    stats.reviewed = reports.value.filter(r => r.status === '已审核').length
  } catch (error) {
    message.error('获取日报列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchDailyReports()
}

const handleReset = () => {
  searchKeyword.value = ''
  selectedStatus.value = undefined
  currentPage.value = 1
  fetchDailyReports()
}

const handlePageChange = (page, size) => {
  currentPage.value = page
  pageSize.value = size
  fetchDailyReports()
}

const handleSizeChange = (current, size) => {
  pageSize.value = size
  currentPage.value = 1
  fetchDailyReports()
}

const showAddModal = () => {
  const today = new Date()
  const year = today.getFullYear()
  const month = String(today.getMonth() + 1).padStart(2, '0')
  const day = String(today.getDate()).padStart(2, '0')
  
  Object.assign(formData, {
    employeeId: authStore.userId ? Number(authStore.userId) : 1,
    employeeName: authStore.realName || authStore.username,
    department: authStore.department,
    reportDate: `${year}-${month}-${day}`,
    todayWork: '',
    tomorrowWork: '',
    issues: ''
  })
  modalVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitLoading.value = true

    const res = await dailyReportApi.submitDailyReport(formData)
    if (res.data && res.data.success) {
      message.success('提交成功')
      modalVisible.value = false
      fetchDailyReports()
    } else {
      message.error(res.data?.message || '提交失败')
    }
  } catch (error) {
    console.error('提交日报失败:', error)
    if (error.message) {
      message.error(error.message)
    } else {
      message.error('提交失败，请稍后重试')
    }
  } finally {
    submitLoading.value = false
  }
}

const handleCancel = () => {
  modalVisible.value = false
  formRef.value?.resetFields()
}

const handleViewDetail = (record) => {
  currentRecord.value = record
  detailVisible.value = true
}

const handleReview = (record) => {
  currentRecord.value = record
  reviewForm.comment = ''
  reviewModalVisible.value = true
}

const handleConfirmReview = async () => {
  try {
    reviewLoading.value = true
    const res = await dailyReportApi.reviewDailyReport(currentRecord.value.id, {
      reviewComment: reviewForm.comment
    })
    if (res.data && res.data.success) {
      message.success('审核成功')
      reviewModalVisible.value = false
      fetchDailyReports()
    } else {
      message.error(res.data?.message || '审核失败')
    }
  } catch (error) {
    console.error('审核日报失败:', error)
    if (error.message) {
      message.error(error.message)
    } else {
      message.error('审核失败，请稍后重试')
    }
  } finally {
    reviewLoading.value = false
  }
}

const handleCancelReview = () => {
  reviewModalVisible.value = false
}

const handleDelete = async (record) => {
  try {
    const res = await dailyReportApi.deleteDailyReport(record.id)
    if (res.data && res.data.success) {
      message.success('删除成功')
      fetchDailyReports()
    } else {
      message.error(res.data?.message || '删除失败')
    }
  } catch (error) {
    console.error('删除日报失败:', error)
    if (error.message) {
      message.error(error.message)
    } else {
      message.error('删除失败，请稍后重试')
    }
  }
}

const handleBatchDelete = () => {
  Modal.confirm({
    title: '确认批量删除',
    content: `确定要删除选中的 ${selectedRowKeys.value.length} 条日报记录吗？此操作不可恢复。`,
    okText: '确定删除',
    okType: 'danger',
    cancelText: '取消',
    async onOk() {
      batchDeleteLoading.value = true;
      try {
        const res = await dailyReportApi.batchDeleteDailyReports(selectedRowKeys.value);
        if (res.data.success) {
          message.success(`✅ 成功删除 ${res.data.data} 条记录`);
          selectedRowKeys.value = [];
          await fetchDailyReports();
        } else {
          message.error(res.data.message || '批量删除失败');
        }
      } catch (error) {
        message.error(error.message || '❌ 批量删除失败');
      } finally {
        batchDeleteLoading.value = false;
      }
    }
  });
};

onMounted(() => {
  fetchDailyReports()
})
</script>

<style scoped>
.daily-report-list {
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

.stats-card {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.stat-item {
  display: flex;
  align-items: center;
  padding: 16px;
  background: #f9fafb;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.stat-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #fff;
  margin-right: 16px;
  flex-shrink: 0;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #6b7280;
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
