<template>
  <div class="page-container">
    <a-card :bordered="false" class="card-container">
      <div class="search-bar">
        <a-form :model="searchForm" layout="inline">
          <a-form-item label="员工姓名">
            <a-input
              v-model:value="searchForm.keyword"
              placeholder="请输入员工姓名"
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <UserOutlined />
              </template>
            </a-input>
          </a-form-item>
          <a-form-item label="发放月份">
            <a-month-picker
              v-model:value="searchForm.payMonth"
              placeholder="选择月份"
              format="YYYY-MM"
            />
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="handleSearch">
              <SearchOutlined /> 查询
            </a-button>
            <a-button style="margin-left: 8px" @click="handleReset">
              <ReloadOutlined /> 重置
            </a-button>
            <a-button type="primary" style="margin-left: 8px" @click="handleAdd">
              <PlusOutlined /> 添加
            </a-button>
          </a-form-item>
        </a-form>
      </div>

      <a-table
        :columns="columns"
        :data-source="salarySlips.data.records"
        :pagination="pagination"
        :loading="loading"
        row-key="id"
        scroll="{ x: 1200 }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'payMonth'">
            {{ formatMonth(record.payMonth) }}
          </template>
          <template v-if="column.key === 'basicSalary'">
            {{ formatMoney(record.basicSalary) }}
          </template>
          <template v-if="column.key === 'performanceBonus'">
            {{ formatMoney(record.performanceBonus) }}
          </template>
          <template v-if="column.key === 'overtimePay'">
            {{ formatMoney(record.overtimePay) }}
          </template>
          <template v-if="column.key === 'allowance'">
            {{ formatMoney(record.allowance) }}
          </template>
          <template v-if="column.key === 'totalIncome'">
            {{ formatMoney(record.totalIncome) }}
          </template>
          <template v-if="column.key === 'socialInsurance'">
            {{ formatMoney(record.socialInsurance) }}
          </template>
          <template v-if="column.key === 'housingFund'">
            {{ formatMoney(record.housingFund) }}
          </template>
          <template v-if="column.key === 'tax'">
            {{ formatMoney(record.tax) }}
          </template>
          <template v-if="column.key === 'totalDeduction'">
            {{ formatMoney(record.totalDeduction) }}
          </template>
          <template v-if="column.key === 'netSalary'">
            <span class="net-salary">{{ formatMoney(record.netSalary) }}</span>
          </template>
          <template v-if="column.key === 'status'">
            <a-tag :color="getStatusColor(record.status)">
              {{ record.status }}
            </a-tag>
          </template>
          <template v-if="column.key === 'action'">
            <a-space>
              <a-button size="small" @click="handleView(record)">
                <EyeOutlined /> 查看
              </a-button>
              <a-button size="small" @click="handleEdit(record)">
                <EditOutlined /> 编辑
              </a-button>
              <a-button size="small" danger @click="handleDelete(record)">
                <DeleteOutlined /> 删除
              </a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal
      v-model:open="viewModalVisible"
      title="工资条详情"
      :footer="null"
      width="800px"
    >
      <div class="salary-detail" v-if="currentSalarySlip">
        <div class="detail-header">
          <h3>{{ currentSalarySlip.employeeName }} - {{ formatMonth(currentSalarySlip.payMonth) }}工资条</h3>
          <a-tag :color="getStatusColor(currentSalarySlip.status)">
            {{ currentSalarySlip.status }}
          </a-tag>
        </div>
        <a-descriptions :column="2" bordered>
          <a-descriptions-item label="员工姓名">{{ currentSalarySlip.employeeName }}</a-descriptions-item>
          <a-descriptions-item label="所属部门">{{ currentSalarySlip.department }}</a-descriptions-item>
        </a-descriptions>
        <div class="salary-section">
          <h4>收入明细</h4>
          <a-descriptions :column="2" bordered>
            <a-descriptions-item label="基本工资">{{ formatMoney(currentSalarySlip.basicSalary) }}</a-descriptions-item>
            <a-descriptions-item label="绩效奖金">{{ formatMoney(currentSalarySlip.performanceBonus) }}</a-descriptions-item>
            <a-descriptions-item label="加班工资">{{ formatMoney(currentSalarySlip.overtimePay) }}</a-descriptions-item>
            <a-descriptions-item label="各项津贴">{{ formatMoney(currentSalarySlip.allowance) }}</a-descriptions-item>
            <a-descriptions-item label="收入合计" :span="2">
              <strong>{{ formatMoney(currentSalarySlip.totalIncome) }}</strong>
            </a-descriptions-item>
          </a-descriptions>
        </div>
        <div class="salary-section">
          <h4>扣款明细</h4>
          <a-descriptions :column="2" bordered>
            <a-descriptions-item label="社会保险">{{ formatMoney(currentSalarySlip.socialInsurance) }}</a-descriptions-item>
            <a-descriptions-item label="住房公积金">{{ formatMoney(currentSalarySlip.housingFund) }}</a-descriptions-item>
            <a-descriptions-item label="个人所得税">{{ formatMoney(currentSalarySlip.tax) }}</a-descriptions-item>
            <a-descriptions-item label="扣款合计" :span="2">
              <strong>{{ formatMoney(currentSalarySlip.totalDeduction) }}</strong>
            </a-descriptions-item>
          </a-descriptions>
        </div>
        <div class="salary-total">
          <span>实发工资：</span>
          <span class="total-amount">{{ formatMoney(currentSalarySlip.netSalary) }}</span>
        </div>
        <div v-if="currentSalarySlip.remark" class="salary-remark">
          <strong>备注：</strong>{{ currentSalarySlip.remark }}
        </div>
      </div>
    </a-modal>

    <a-modal
      v-model:open="editModalVisible"
      :title="isEdit ? '编辑工资条' : '添加工资条'"
      :footer="null"
      width="700px"
    >
      <a-form
        :model="editForm"
        :label-col="{ span: 6 }"
        :wrapper-col="{ span: 18 }"
      >
        <a-form-item label="员工姓名">
          <a-select
            v-model:value="editForm.employeeId"
            :disabled="isEdit"
            placeholder="请选择员工"
            show-search
            :filter-option="filterEmployeeOption"
            @change="handleEmployeeChange"
            style="width: 100%"
            :getPopupContainer="(triggerNode) => triggerNode.parentElement"
          >
            <a-select-option
              v-for="employee in employeeOptions"
              :key="employee.id"
              :value="employee.id"
            >
              {{ employee.name }} - {{ employee.department }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="所属部门">
          <a-input v-model:value="editForm.department" disabled />
        </a-form-item>
        <a-form-item label="发放月份">
          <a-month-picker
            v-model:value="editForm.payMonth"
            format="YYYY-MM"
            valueFormat="YYYY-MM"
            @change="handlePayMonthChange"
            :getPopupContainer="(triggerNode) => triggerNode.parentElement"
          />
        </a-form-item>
        <a-divider orientation="left">收入明细</a-divider>
        <a-form-item label="基本工资">
          <a-input-number v-model:value="editForm.basicSalary" style="width: 100%" />
        </a-form-item>
        <a-form-item label="绩效奖金">
          <a-input-number v-model:value="editForm.performanceBonus" style="width: 100%" />
        </a-form-item>
        <a-form-item label="加班工资">
          <a-input-number v-model:value="editForm.overtimePay" style="width: 100%" />
        </a-form-item>
        <a-form-item label="各项津贴">
          <a-input-number v-model:value="editForm.allowance" style="width: 100%" />
        </a-form-item>
        <a-divider orientation="left">扣款明细</a-divider>
        <a-form-item label="社会保险">
          <a-input-number v-model:value="editForm.socialInsurance" style="width: 100%" />
        </a-form-item>
        <a-form-item label="住房公积金">
          <a-input-number v-model:value="editForm.housingFund" style="width: 100%" />
        </a-form-item>
        <a-form-item label="个人所得税">
          <a-input-number v-model:value="editForm.tax" style="width: 100%" />
        </a-form-item>
        <a-form-item label="状态">
          <a-select v-model:value="editForm.status">
            <a-select-option value="已发放">已发放</a-select-option>
            <a-select-option value="未发放">未发放</a-select-option>
            <a-select-option value="已作废">已作废</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="备注">
          <a-textarea v-model:value="editForm.remark" :rows="3" />
        </a-form-item>
      </a-form>
      <div class="modal-footer">
        <a-button @click="editModalVisible = false">取消</a-button>
        <a-button type="primary" @click="handleSubmit">提交</a-button>
      </div>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import {
  UserOutlined,
  SearchOutlined,
  ReloadOutlined,
  EyeOutlined,
  EditOutlined,
  DeleteOutlined,
  PlusOutlined
} from '@ant-design/icons-vue'
import { useAuthStore } from '../../store/auth'
import { salaryApi } from '../../api/salary'
import { getEmployees } from '../../api/employee'

const authStore = useAuthStore()

const loading = ref(false)
const searchForm = reactive({
  keyword: '',
  payMonth: null
})

const salarySlips = ref({
  data: {
    records: [],
    total: 0,
    size: 10,
    current: 1,
    pages: 1
  }
})

const pagination = computed(() => ({
  current: salarySlips.value.data.current,
  pageSize: salarySlips.value.data.size,
  total: salarySlips.value.data.total,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total) => `共 ${total} 条记录`,
  onChange: (page, pageSize) => {
    salarySlips.value.data.current = page
    salarySlips.value.data.size = pageSize
    fetchSalarySlips()
  },
  onShowSizeChange: (current, pageSize) => {
    salarySlips.value.data.size = pageSize
    fetchSalarySlips()
  }
}))

const columns = [
  {
    title: '员工姓名',
    dataIndex: 'employeeName',
    key: 'employeeName',
    width: 120
  },
  {
    title: '部门',
    dataIndex: 'department',
    key: 'department',
    width: 120
  },
  {
    title: '发放月份',
    dataIndex: 'payMonth',
    key: 'payMonth',
    width: 120
  },
  {
    title: '基本工资',
    dataIndex: 'basicSalary',
    key: 'basicSalary',
    width: 120
  },
  {
    title: '绩效奖金',
    dataIndex: 'performanceBonus',
    key: 'performanceBonus',
    width: 120
  },
  {
    title: '加班工资',
    dataIndex: 'overtimePay',
    key: 'overtimePay',
    width: 120
  },
  {
    title: '津贴',
    dataIndex: 'allowance',
    key: 'allowance',
    width: 100
  },
  {
    title: '收入合计',
    dataIndex: 'totalIncome',
    key: 'totalIncome',
    width: 120
  },
  {
    title: '社保',
    dataIndex: 'socialInsurance',
    key: 'socialInsurance',
    width: 100
  },
  {
    title: '公积金',
    dataIndex: 'housingFund',
    key: 'housingFund',
    width: 100
  },
  {
    title: '个税',
    dataIndex: 'tax',
    key: 'tax',
    width: 100
  },
  {
    title: '扣款合计',
    dataIndex: 'totalDeduction',
    key: 'totalDeduction',
    width: 120
  },
  {
    title: '实发工资',
    dataIndex: 'netSalary',
    key: 'netSalary',
    width: 130
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    width: 100,
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

const viewModalVisible = ref(false)
const editModalVisible = ref(false)
const isEdit = ref(false)
const currentSalarySlip = ref(null)
const employeeOptions = ref([])
const editForm = reactive({
  id: null,
  employeeId: null,
  employeeName: '',
  department: '',
  payMonth: null,
  basicSalary: 0,
  performanceBonus: 0,
  overtimePay: 0,
  allowance: 0,
  socialInsurance: 0,
  housingFund: 0,
  tax: 0,
  status: '未发放',
  remark: ''
})

async function fetchSalarySlips() {
  loading.value = true
  try {
    const params = {
      page: salarySlips.value.data.current,
      pageSize: salarySlips.value.data.size
    }
    if (searchForm.keyword) {
      params.keyword = searchForm.keyword
    }
    if (searchForm.payMonth) {
      params.payMonth = searchForm.payMonth.format('YYYY-MM')
    }
    if (!authStore.isAdmin) {
      params.employeeId = authStore.userId
    }
    const res = await salaryApi.getSalarySlips(params)
    if (res.data.success && res.data.code === 200) {
      const data = res.data.data
      salarySlips.value.data = {
        records: data.records || [],
        total: data.total || 0,
        size: data.size || 10,
        current: data.current || 1,
        pages: data.pages || 1
      }
    } else {
      throw new Error(res.data.message || '获取数据失败')
    }
  } catch (error) {
    message.error(error.message || '获取工资条列表失败')
    console.error('获取工资条列表失败:', error)
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  salarySlips.value.data.current = 1
  fetchSalarySlips()
}

function handleReset() {
  searchForm.keyword = ''
  searchForm.payMonth = null
  salarySlips.value.data.current = 1
  fetchSalarySlips()
}

function handleView(record) {
  currentSalarySlip.value = record
  viewModalVisible.value = true
}

async function handleAdd() {
  isEdit.value = false
  editForm.id = null
  editForm.employeeId = null
  editForm.employeeName = ''
  editForm.department = ''
  editForm.payMonth = null
  editForm.basicSalary = 0
  editForm.performanceBonus = 0
  editForm.overtimePay = 0
  editForm.allowance = 0
  editForm.socialInsurance = 0
  editForm.housingFund = 0
  editForm.tax = 0
  editForm.status = '未发放'
  editForm.remark = ''
  await loadAllEmployees()
  editModalVisible.value = true
}

async function loadAllEmployees() {
  try {
    const res = await getEmployees({ page: 1, pageSize: 100 })
    if (res.data.success) {
      employeeOptions.value = res.data.data.records || []
    }
  } catch (error) {
    console.error('加载员工列表失败:', error)
  }
}

function filterEmployeeOption(input, option) {
  return option.children.toLowerCase().includes(input.toLowerCase())
}

function handleEdit(record) {
  isEdit.value = true
  editForm.id = record.id
  editForm.employeeId = record.employeeId
  editForm.employeeName = record.employeeName
  editForm.department = record.department
  editForm.payMonth = record.payMonth ? formatMonth(record.payMonth) : null
  editForm.basicSalary = Number(record.basicSalary) || 0
  editForm.performanceBonus = Number(record.performanceBonus) || 0
  editForm.overtimePay = Number(record.overtimePay) || 0
  editForm.allowance = Number(record.allowance) || 0
  editForm.socialInsurance = Number(record.socialInsurance) || 0
  editForm.housingFund = Number(record.housingFund) || 0
  editForm.tax = Number(record.tax) || 0
  editForm.status = record.status || '未发放'
  editForm.remark = record.remark || ''
  editModalVisible.value = true
}

function handleEmployeeChange(employeeId) {
  const employee = employeeOptions.value.find(e => e.id === employeeId)
  if (employee) {
    editForm.employeeName = employee.name
    editForm.department = employee.department
  }
}

function handlePayMonthChange(date, dateString) {
  editForm.payMonth = dateString
}

async function handleSubmit() {
  try {
    const data = {
      ...editForm,
      totalIncome: calculateTotalIncome(),
      totalDeduction: calculateTotalDeduction(),
      netSalary: calculateNetSalary()
    }
    let res
    if (isEdit.value) {
      res = await salaryApi.updateSalarySlip(editForm.id, data)
    } else {
      res = await salaryApi.createSalarySlip(data)
    }
    if (res.data.success && res.data.code === 200) {
      message.success(isEdit.value ? '工资条更新成功' : '工资条添加成功')
      editModalVisible.value = false
      fetchSalarySlips()
    } else {
      throw new Error(res.data.message || '操作失败')
    }
  } catch (error) {
    message.error(error.message || '操作失败')
    console.error('操作失败:', error)
  }
}

function calculateTotalIncome() {
  return (editForm.basicSalary || 0) + (editForm.performanceBonus || 0) + (editForm.overtimePay || 0) + (editForm.allowance || 0)
}

function calculateTotalDeduction() {
  return (editForm.socialInsurance || 0) + (editForm.housingFund || 0) + (editForm.tax || 0)
}

function calculateNetSalary() {
  return calculateTotalIncome() - calculateTotalDeduction()
}

async function handleDelete(record) {
  try {
    const res = await salaryApi.deleteSalarySlip(record.id)
    if (res.data.success && res.data.code === 200) {
      message.success('工资条删除成功')
      fetchSalarySlips()
    } else {
      throw new Error(res.data.message || '删除失败')
    }
  } catch (error) {
    message.error(error.message || '删除工资条失败')
    console.error('删除工资条失败:', error)
  }
}

function formatMonth(date) {
  if (!date) return ''
  const d = new Date(date)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`
}

function formatMoney(value) {
  if (!value) return '0.00'
  return Number(value).toFixed(2)
}

function getStatusColor(status) {
  const colors = {
    '已发放': 'green',
    '未发放': 'orange',
    '已作废': 'red'
  }
  return colors[status] || 'default'
}

onMounted(() => {
  fetchSalarySlips()
})
</script>

<style scoped>
.page-container {
  padding: 20px;
}

.card-container {
  border-radius: 8px;
}

.search-bar {
  margin-bottom: 20px;
}

.net-salary {
  font-weight: bold;
  color: #1890ff;
}

.salary-detail {
  padding: 10px;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.detail-header h3 {
  margin: 0;
}

.salary-section {
  margin-bottom: 15px;
}

.salary-section h4 {
  margin: 0 0 10px 0;
  font-size: 14px;
  color: #666;
}

.salary-total {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-top: 20px;
  padding: 15px;
  background: #f5f5f5;
  border-radius: 4px;
  font-size: 18px;
}

.total-amount {
  margin-left: 10px;
  font-weight: bold;
  color: #1890ff;
  font-size: 24px;
}

.salary-remark {
  margin-top: 15px;
  padding: 10px;
  background: #fffbe6;
  border-radius: 4px;
  color: #ad8b00;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  padding: 16px;
  border-top: 1px solid #f0f0f0;
}
</style>