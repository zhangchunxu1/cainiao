<template>
  <div class="attendance-list">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">
          <ClockCircleOutlined />
          考勤管理
        </h2>
        <p class="page-subtitle">记录和管理员工考勤信息</p>
      </div>
      <div class="header-right">
        <a-button type="primary" @click="handleCheckIn" size="large" :loading="checkInLoading">
          <LoginOutlined />
          签到
        </a-button>
        <a-button type="primary" style="background: #52c41a; border-color: #52c41a;" @click="handleCheckOut" size="large" :loading="checkOutLoading">
          <LogoutOutlined />
          签退
        </a-button>
      </div>
    </div>

    <!-- 签到/签退卡片 -->
    <div class="status-card">
      <a-row :gutter="16">
        <a-col :span="8">
          <div class="stat-item">
            <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
              <ClockCircleOutlined />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ currentTime }}</div>
              <div class="stat-label">当前时间</div>
            </div>
          </div>
        </a-col>
        <a-col :span="8">
          <div class="stat-item">
            <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
              <LoginOutlined />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ todayCheckIn || '--:--' }}</div>
              <div class="stat-label">今日签到</div>
            </div>
          </div>
        </a-col>
        <a-col :span="8">
          <div class="stat-item">
            <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
              <LogoutOutlined />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ todayCheckOut || '--:--' }}</div>
              <div class="stat-label">今日签退</div>
            </div>
          </div>
        </a-col>
      </a-row>
    </div>

    <!-- 搜索卡片 -->
    <div class="search-card">
      <a-form layout="inline" class="search-form">
        <a-form-item label="员工姓名：">
          <a-input
            v-model:value="searchKeyword"
            placeholder="输入员工姓名"
            allowClear
            size="large"
            @pressEnter="handleSearch"
          >
            <template #prefix>
              <SearchOutlined style="color: #bfbfbf;" />
            </template>
          </a-input>
        </a-form-item>
        <a-form-item label="日期范围：">
          <a-range-picker
            v-model:value="dateRange"
            size="large"
            format="YYYY-MM-DD"
            valueFormat="YYYY-MM-DD"
          />
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
          <ClockCircleOutlined class="title-icon" />
          <span>考勤记录列表</span>
          <a-tag color="blue">{{ attendanceTotal }} 条记录</a-tag>
        </div>
      </div>

      <a-table
        :dataSource="attendanceRecords"
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
          <template v-else-if="column.key === 'workHours'">
            <span :style="{ color: parseFloat(record.workHours) >= 8 ? '#52c41a' : '#ff4d4f', fontWeight: '600' }">
              {{ record.workHours }} 小时
            </span>
          </template>
        </template>
      </a-table>

      <!-- 分页 -->
      <div class="pagination-wrapper" v-if="attendanceTotal > 0">
        <a-pagination
          v-model:current="currentPage"
          :total="attendanceTotal"
          :pageSize="pageSize"
          showSizeChanger
          showQuickJumper
          :showTotal="(total) => `共 ${total} 条记录`"
          @change="handlePageChange"
          @showSizeChange="handleSizeChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { message } from 'ant-design-vue'
import {
  ClockCircleOutlined,
  LoginOutlined,
  LogoutOutlined,
  SearchOutlined,
  ReloadOutlined
} from '@ant-design/icons-vue'
import { attendanceApi } from '../../api/attendance'

const loading = ref(false)
const checkInLoading = ref(false)
const checkOutLoading = ref(false)

const searchKeyword = ref('')
const dateRange = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const attendanceTotal = ref(0)
const attendanceRecords = ref([])

const currentTime = ref('')
const todayCheckIn = ref('')
const todayCheckOut = ref('')
let timeInterval = null

const columns = [
  {
    title: '员工姓名',
    dataIndex: 'employeeName',
    key: 'employeeName'
  },
  {
    title: '日期',
    dataIndex: 'date',
    key: 'date'
  },
  {
    title: '签到时间',
    dataIndex: 'checkInTime',
    key: 'checkInTime'
  },
  {
    title: '签退时间',
    dataIndex: 'checkOutTime',
    key: 'checkOutTime'
  },
  {
    title: '工作时长',
    dataIndex: 'workHours',
    key: 'workHours',
    align: 'center'
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    align: 'center'
  }
]

const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

const getStatusColor = (status) => {
  const colors = {
    '正常': 'green',
    '迟到': 'orange',
    '早退': 'red',
    '缺卡': 'default',
    '迟到/早退': 'red'
  }
  return colors[status] || 'default'
}

const fetchAttendance = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value
    }

    if (searchKeyword.value) {
      params.keyword = searchKeyword.value
    }

    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }

    const res = await attendanceApi.getAttendanceList(params)
    const data = res.data.data
    attendanceRecords.value = data.records || []
    attendanceTotal.value = data.total || 0

    // 更新今日签到签退时间（取当前用户的最新记录）
    const todayRecord = attendanceRecords.value.find(
      r => r.date === new Date().toISOString().split('T')[0]
    )
    if (todayRecord) {
      todayCheckIn.value = todayRecord.checkInTime || ''
      todayCheckOut.value = todayRecord.checkOutTime || ''
    }
  } catch (error) {
    message.error('获取考勤记录失败')
  } finally {
    loading.value = false
  }
}

const handleCheckIn = async () => {
  checkInLoading.value = true
  try {
    const res = await attendanceApi.checkIn({
      employeeId: 1,
      employeeName: '张三'
    })
    message.success('签到成功')
    fetchAttendance()
  } catch (error) {
    message.error(error.message || '签到失败')
  } finally {
    checkInLoading.value = false
  }
}

const handleCheckOut = async () => {
  checkOutLoading.value = true
  try {
    const res = await attendanceApi.checkOut({
      employeeId: 1
    })
    message.success('签退成功')
    fetchAttendance()
  } catch (error) {
    message.error(error.message || '签退失败')
  } finally {
    checkOutLoading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchAttendance()
}

const handleReset = () => {
  searchKeyword.value = ''
  dateRange.value = []
  currentPage.value = 1
  fetchAttendance()
}

const handlePageChange = (page, size) => {
  currentPage.value = page
  pageSize.value = size
  fetchAttendance()
}

const handleSizeChange = (current, size) => {
  pageSize.value = size
  currentPage.value = 1
  fetchAttendance()
}

onMounted(() => {
  updateTime()
  timeInterval = setInterval(updateTime, 1000)
  fetchAttendance()
})

onUnmounted(() => {
  if (timeInterval) {
    clearInterval(timeInterval)
  }
})
</script>

<style scoped>
.attendance-list {
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

.header-right {
  display: flex;
  gap: 12px;
}

.status-card {
  background: #fff;
  padding: 24px;
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
  font-size: 20px;
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