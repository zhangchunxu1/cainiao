<template>
  <div class="leave-list">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">
          <CalendarOutlined />
          请假管理
        </h2>
        <p class="page-subtitle">管理员工请假申请和审批</p>
      </div>
      <div class="header-right">
        <a-button type="primary" @click="showAddModal" size="large">
          <PlusOutlined />
          申请请假
        </a-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-card">
      <a-row :gutter="16">
        <a-col :span="8">
          <div class="stat-item">
            <div class="stat-icon" style="background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);">
              <ClockCircleOutlined />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.pending || 0 }}</div>
              <div class="stat-label">待审批</div>
            </div>
          </div>
        </a-col>
        <a-col :span="8">
          <div class="stat-item">
            <div class="stat-icon" style="background: linear-gradient(135deg, #30cfd0 0%, #330867 100%);">
              <CheckCircleOutlined />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.approved || 0 }}</div>
              <div class="stat-label">已批准</div>
            </div>
          </div>
        </a-col>
        <a-col :span="8">
          <div class="stat-item">
            <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
              <CloseCircleOutlined />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.rejected || 0 }}</div>
              <div class="stat-label">已拒绝</div>
            </div>
          </div>
        </a-col>
      </a-row>
    </div>

    <!-- 搜索卡片 -->
    <div class="search-card">
      <a-form layout="inline" class="search-form">
        <a-form-item>
          <a-input
            v-model:value="searchKeyword"
            placeholder="搜索员工姓名/部门"
            allowClear
            size="large"
            @pressEnter="handleSearch"
          >
            <template #prefix>
              <SearchOutlined style="color: #bfbfbf;" />
            </template>
          </a-input>
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
            <a-select-option value="待审批">待审批</a-select-option>
            <a-select-option value="已批准">已批准</a-select-option>
            <a-select-option value="已拒绝">已拒绝</a-select-option>
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

    <!-- 表格卡片 -->
    <div class="table-card">
      <div class="table-title-bar">
        <div class="title-info">
          <CalendarOutlined class="title-icon" />
          <span>请假申请列表</span>
          <a-tag color="blue">{{ leaveTotal }} 条记录</a-tag>
        </div>
      </div>

      <a-table
        :dataSource="leaveRecords"
        :columns="columns"
        :loading="loading"
        rowKey="id"
        :pagination="false"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'status'">
            <a-tag :color="getStatusColor(record.status)">
              {{ record.status }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'leaveType'">
            <a-tag color="processing">{{ record.leaveType }}</a-tag>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-tooltip title="查看详情">
                <a-button type="link" @click="handleViewDetail(record)">
                  <EyeOutlined />
                </a-button>
              </a-tooltip>

              <template v-if="record.status === '待审批'">
                <a-popconfirm
                  title="确定批准此请假申请吗？"
                  ok-text="确定"
                  cancel-text="取消"
                  @confirm="handleApprove(record)"
                >
                  <a-button type="link" style="color: #52c41a;">
                    <CheckCircleOutlined /> 批准
                  </a-button>
                </a-popconfirm>

                <a-popconfirm
                  title="确定拒绝此请假申请吗？"
                  ok-text="确定"
                  cancel-text="取消"
                  @confirm="handleReject(record)"
                >
                  <a-button type="link" danger>
                    <CloseCircleOutlined /> 拒绝
                  </a-button>
                </a-popconfirm>
              </template>

              <a-popconfirm
                title="确定要删除这条记录吗？"
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
        </template>
      </a-table>

      <!-- 分页 -->
      <div class="pagination-wrapper" v-if="leaveTotal > 0">
        <a-pagination
          v-model:current="currentPage"
          :total="leaveTotal"
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
      width="650px"
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

        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="请假类型" name="leaveType">
              <a-select v-model:value="formData.leaveType" placeholder="请选择请假类型">
                <a-select-option value="事假">事假</a-select-option>
                <a-select-option value="病假">病假</a-select-option>
                <a-select-option value="年假">年假</a-select-option>
                <a-select-option value="婚假">婚假</a-select-option>
                <a-select-option value="产假">产假</a-select-option>
                <a-select-option value="丧假">丧假</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="请假天数" name="days">
              <a-input-number
                v-model:value="formData.days"
                :min="0.5"
                :max="365"
                :step="0.5"
                style="width: 100%;"
                placeholder="请输入天数"
              />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="开始日期" name="startDate">
              <a-date-picker
                v-model:value="formData.startDate"
                format="YYYY-MM-DD"
                valueFormat="YYYY-MM-DD"
                style="width: 100%;"
                placeholder="选择开始日期"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="结束日期" name="endDate">
              <a-date-picker
                v-model:value="formData.endDate"
                format="YYYY-MM-DD"
                valueFormat="YYYY-MM-DD"
                style="width: 100%;"
                placeholder="选择结束日期"
              />
            </a-form-item>
          </a-col>
        </a-row>

        <a-form-item label="请假原因" name="reason">
          <a-textarea v-model:value="formData.reason" placeholder="请输入请假原因" :rows="4" />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 详情弹窗 -->
    <a-modal
      v-model:open="detailVisible"
      title="请假详情"
      width="700px"
      :footer="null"
    >
      <div v-if="currentRecord" class="detail-content">
        <a-descriptions bordered :column="2">
          <a-descriptions-item label="员工姓名">{{ currentRecord.employeeName }}</a-descriptions-item>
          <a-descriptions-item label="部门">{{ currentRecord.department }}</a-descriptions-item>
          <a-descriptions-item label="请假类型">
            <a-tag color="processing">{{ currentRecord.leaveType }}</a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="请假天数">{{ currentRecord.days }} 天</a-descriptions-item>
          <a-descriptions-item label="开始日期">{{ currentRecord.startDate }}</a-descriptions-item>
          <a-descriptions-item label="结束日期">{{ currentRecord.endDate }}</a-descriptions-item>
          <a-descriptions-item label="状态" :span="2">
            <a-tag :color="getStatusColor(currentRecord.status)">{{ currentRecord.status }}</a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="请假原因" :span="2">{{ currentRecord.reason }}</a-descriptions-item>

          <template v-if="currentRecord.status !== '待审批'">
            <a-descriptions-item label="审批人">{{ currentRecord.approver }}</a-descriptions-item>
            <a-descriptions-item label="审批时间">{{ currentRecord.approvalTime }}</a-descriptions-item>
            <a-descriptions-item label="审批意见" :span="2">{{ currentRecord.approvalComment || '无' }}</a-descriptions-item>
          </template>
        </a-descriptions>
      </div>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import {
  CalendarOutlined,
  PlusOutlined,
  SearchOutlined,
  ReloadOutlined,
  ClockCircleOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
  EyeOutlined,
  DeleteOutlined
} from '@ant-design/icons-vue'
import { leaveRequestApi } from '../../api/leave'

const loading = ref(false)
const submitLoading = ref(false)
const modalVisible = ref(false)
const detailVisible = ref(false)
const isEdit = ref(false)

const searchKeyword = ref('')
const selectedStatus = ref(undefined)
const currentPage = ref(1)
const pageSize = ref(10)
const leaveTotal = ref(0)
const leaveRecords = ref([])
const currentRecord = ref(null)

const stats = reactive({
  pending: 0,
  approved: 0,
  rejected: 0
})

const formRef = ref()
const formData = reactive({
  employeeId: 1,
  employeeName: '张三',
  department: '技术部',
  leaveType: '',
  startDate: '',
  endDate: '',
  days: null,
  reason: ''
})

const formRules = {
  leaveType: [{ required: true, message: '请选择请假类型', trigger: 'change' }],
  startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }],
  days: [{ required: true, message: '请输入请假天数', trigger: 'blur' }],
  reason: [{ required: true, message: '请输入请假原因', trigger: 'blur' }]
}

const modalTitle = computed(() => '申请请假')

const columns = [
  {
    title: '员工姓名',
    dataIndex: 'employeeName',
    key: 'employeeName'
  },
  {
    title: '部门',
    dataIndex: 'department',
    key: 'department'
  },
  {
    title: '请假类型',
    dataIndex: 'leaveType',
    key: 'leaveType'
  },
  {
    title: '开始日期',
    dataIndex: 'startDate',
    key: 'startDate'
  },
  {
    title: '结束日期',
    dataIndex: 'endDate',
    key: 'endDate'
  },
  {
    title: '天数',
    dataIndex: 'days',
    key: 'days',
    align: 'center'
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
    width: 280
  }
]

const getStatusColor = (status) => {
  const colors = {
    '待审批': 'orange',
    '已批准': 'green',
    '已拒绝': 'red'
  }
  return colors[status] || 'default'
}

const fetchLeaveRequests = async () => {
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

    const res = await leaveRequestApi.getLeaveRequestList(params)
    const data = res.data.data
    leaveRecords.value = data.records || []
    leaveTotal.value = data.total || 0

    // 计算统计数据
    stats.pending = leaveRecords.value.filter(r => r.status === '待审批').length
    stats.approved = leaveRecords.value.filter(r => r.status === '已批准').length
    stats.rejected = leaveRecords.value.filter(r => r.status === '已拒绝').length
  } catch (error) {
    message.error('获取请假列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchLeaveRequests()
}

const handleReset = () => {
  searchKeyword.value = ''
  selectedStatus.value = undefined
  currentPage.value = 1
  fetchLeaveRequests()
}

const handlePageChange = (page, size) => {
  currentPage.value = page
  pageSize.value = size
  fetchLeaveRequests()
}

const handleSizeChange = (current, size) => {
  pageSize.value = size
  currentPage.value = 1
  fetchLeaveRequests()
}

const showAddModal = () => {
  Object.assign(formData, {
    employeeId: 1,
    employeeName: '张三',
    department: '技术部',
    leaveType: '',
    startDate: '',
    endDate: '',
    days: null,
    reason: ''
  })
  modalVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitLoading.value = true

    await leaveRequestApi.submitLeaveRequest(formData)
    message.success('提交成功')
    modalVisible.value = false
    fetchLeaveRequests()
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

const handleApprove = async (record) => {
  try {
    await leaveRequestApi.approveLeave(record.id, {
      approver: '管理员',
      comment: '同意'
    })
    message.success('已批准')
    fetchLeaveRequests()
  } catch (error) {
    message.error(error.message || '操作失败')
  }
}

const handleReject = async (record) => {
  try {
    await leaveRequestApi.rejectLeave(record.id, {
      approver: '管理员',
      comment: '不同意'
    })
    message.success('已拒绝')
    fetchLeaveRequests()
  } catch (error) {
    message.error(error.message || '操作失败')
  }
}

const handleDelete = async (record) => {
  try {
    await leaveRequestApi.deleteLeaveRequest(record.id)
    message.success('删除成功')
    fetchLeaveRequests()
  } catch (error) {
    message.error(error.message || '删除失败')
  }
}

const handleViewDetail = (record) => {
  currentRecord.value = record
  detailVisible.value = true
}

onMounted(() => {
  fetchLeaveRequests()
})
</script>

<style scoped>
.leave-list {
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