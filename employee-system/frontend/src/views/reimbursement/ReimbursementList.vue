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
        :data-source="reimbursementList"
        :pagination="pagination"
        :loading="loading"
        row-key="id"
        @change="handleTableChange"
      >
        <template #status="{ text }">
          <a-tag :color="getStatusColor(text)">{{ text }}</a-tag>
        </template>
        <template #action="{ record }">
          <a-button type="link" @click="handleView(record)">
            <EyeOutlined /> 查看
          </a-button>
          <a-button type="link" @click="handleEdit(record)" v-if="record.status === '待审批'">
            <EditOutlined /> 编辑
          </a-button>
          <a-button type="link" danger @click="handleDelete(record)" v-if="record.status === '待审批'">
            <DeleteOutlined /> 删除
          </a-button>
          <a-button type="link" @click="handleManagerApprove(record)" v-if="record.status === '待审批'">
            <CheckCircleOutlined /> 部门审批
          </a-button>
          <a-button type="link" @click="handleFinanceApprove(record)" v-if="record.status === '待财务审批'">
            <CheckCircleOutlined /> 财务审批
          </a-button>
        </template>
      </a-table>
    </a-card>

    <a-modal
      v-model:open="viewModalVisible"
      title="报销详情"
      :footer="null"
      width="700px"
    >
      <a-descriptions :column="2" bordered>
        <a-descriptions-item label="报销单号">{{ currentReimbursement?.reimbursementNo }}</a-descriptions-item>
        <a-descriptions-item label="员工姓名">{{ currentReimbursement?.employeeName }}</a-descriptions-item>
        <a-descriptions-item label="部门">{{ currentReimbursement?.department }}</a-descriptions-item>
        <a-descriptions-item label="报销类型">{{ currentReimbursement?.type }}</a-descriptions-item>
        <a-descriptions-item label="报销金额">¥{{ currentReimbursement?.amount }}</a-descriptions-item>
        <a-descriptions-item label="状态">
          <a-tag :color="getStatusColor(currentReimbursement?.status)">{{ currentReimbursement?.status }}</a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="申请日期">{{ currentReimbursement?.applyDate }}</a-descriptions-item>
        <a-descriptions-item label="备注">{{ currentReimbursement?.remark || '-' }}</a-descriptions-item>
        <a-descriptions-item label="报销事由" :span="2">{{ currentReimbursement?.reason }}</a-descriptions-item>
      </a-descriptions>
      <a-divider />
      <h4>审批记录</h4>
      <a-descriptions :column="2" bordered>
        <a-descriptions-item label="部门经理审批人">{{ currentReimbursement?.managerApprover || '-' }}</a-descriptions-item>
        <a-descriptions-item label="部门经理审批日期">{{ currentReimbursement?.managerApproveDate || '-' }}</a-descriptions-item>
        <a-descriptions-item label="部门经理审批意见" :span="2">{{ currentReimbursement?.managerRemark || '-' }}</a-descriptions-item>
        <a-descriptions-item label="财务审批人">{{ currentReimbursement?.financeApprover || '-' }}</a-descriptions-item>
        <a-descriptions-item label="财务审批日期">{{ currentReimbursement?.financeApproveDate || '-' }}</a-descriptions-item>
        <a-descriptions-item label="财务审批意见" :span="2">{{ currentReimbursement?.financeRemark || '-' }}</a-descriptions-item>
      </a-descriptions>
    </a-modal>

    <a-modal
      v-model:open="editModalVisible"
      :title="isEdit ? '编辑报销' : '添加报销'"
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
        <a-form-item label="报销类型">
          <a-select v-model:value="editForm.type" style="width: 100%">
            <a-select-option value="差旅费">差旅费</a-select-option>
            <a-select-option value="交通费">交通费</a-select-option>
            <a-select-option value="办公用品">办公用品</a-select-option>
            <a-select-option value="业务招待">业务招待</a-select-option>
            <a-select-option value="培训费">培训费</a-select-option>
            <a-select-option value="广告费">广告费</a-select-option>
            <a-select-option value="财务费用">财务费用</a-select-option>
            <a-select-option value="福利费">福利费</a-select-option>
            <a-select-option value="其他">其他</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="报销金额">
          <a-input-number v-model:value="editForm.amount" style="width: 100%" :min="0" />
        </a-form-item>
        <a-form-item label="报销事由">
          <a-textarea v-model:value="editForm.reason" :rows="4" placeholder="请输入报销事由" />
        </a-form-item>
        <a-form-item label="备注">
          <a-input v-model:value="editForm.remark" />
        </a-form-item>
        <a-form-item :wrapper-col="{ offset: 6, span: 18 }">
          <a-button type="primary" @click="handleSubmit" style="margin-right: 8px">
            {{ isEdit ? '更新' : '提交' }}
          </a-button>
          <a-button @click="editModalVisible = false">取消</a-button>
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="approveModalVisible"
      :title="approveModalTitle"
      :footer="null"
      width="500px"
    >
      <a-form :model="approveForm" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="审批人">
          <a-input v-model:value="approveForm.approver" />
        </a-form-item>
        <a-form-item label="审批意见">
          <a-textarea v-model:value="approveForm.remark" :rows="4" placeholder="请输入审批意见" />
        </a-form-item>
        <a-form-item :wrapper-col="{ offset: 6, span: 18 }">
          <a-button type="primary" @click="handleApproveSubmit" style="margin-right: 8px">
            同意
          </a-button>
          <a-button danger @click="handleRejectSubmit">
            驳回
          </a-button>
          <a-button @click="approveModalVisible = false" style="margin-left: 8px">
            取消
          </a-button>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { message, Modal } from 'ant-design-vue'
import {
  UserOutlined,
  SearchOutlined,
  ReloadOutlined,
  EyeOutlined,
  EditOutlined,
  DeleteOutlined,
  PlusOutlined,
  CheckCircleOutlined
} from '@ant-design/icons-vue'
import { reimbursementApi } from '../../api/reimbursement'
import { getEmployees } from '../../api/employee'
import { useAuthStore } from '../../store/auth'

const authStore = useAuthStore()

const loading = ref(false)
const reimbursementList = ref([])
const viewModalVisible = ref(false)
const editModalVisible = ref(false)
const approveModalVisible = ref(false)
const isEdit = ref(false)
const currentReimbursement = ref(null)
const currentApproveType = ref('')
const employeeOptions = ref([])

const searchForm = reactive({
  keyword: ''
})

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showTotal: (total) => `共 ${total} 条`
})

const columns = [
  { title: '报销单号', dataIndex: 'reimbursementNo', key: 'reimbursementNo', width: 150 },
  { title: '员工姓名', dataIndex: 'employeeName', key: 'employeeName', width: 100 },
  { title: '部门', dataIndex: 'department', key: 'department', width: 100 },
  { title: '报销类型', dataIndex: 'type', key: 'type', width: 100 },
  { title: '报销金额', dataIndex: 'amount', key: 'amount', width: 120, render: (val) => `¥${val}` },
  { title: '状态', dataIndex: 'status', key: 'status', width: 120, slots: { customRender: 'status' } },
  { title: '申请日期', dataIndex: 'applyDate', key: 'applyDate', width: 120 },
  { title: '操作', key: 'action', width: 250, slots: { customRender: 'action' }, fixed: 'right' }
]

const editForm = reactive({
  id: null,
  employeeId: null,
  employeeName: '',
  department: '',
  reimbursementNo: '',
  type: '',
  amount: 0,
  reason: '',
  status: '待审批',
  applyDate: '',
  managerApproveDate: '',
  managerApprover: '',
  managerRemark: '',
  financeApproveDate: '',
  financeApprover: '',
  financeRemark: '',
  remark: ''
})

const approveForm = reactive({
  approver: '',
  remark: ''
})

onMounted(() => {
  fetchReimbursements()
})

async function fetchReimbursements() {
  loading.value = true
  try {
    const res = await reimbursementApi.getReimbursements({
      page: pagination.current,
      pageSize: pagination.pageSize,
      keyword: searchForm.keyword
    })
    if (res.data.success) {
      reimbursementList.value = res.data.data.records || []
      pagination.total = res.data.data.total || 0
    }
  } catch (error) {
    message.error('获取报销列表失败')
    console.error('获取报销列表失败:', error)
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pagination.current = 1
  fetchReimbursements()
}

function handleReset() {
  searchForm.keyword = ''
  pagination.current = 1
  fetchReimbursements()
}

function handleTableChange(paginationInfo) {
  pagination.current = paginationInfo.current
  pagination.pageSize = paginationInfo.pageSize
  fetchReimbursements()
}

function handleView(record) {
  currentReimbursement.value = record
  viewModalVisible.value = true
}

async function handleAdd() {
  isEdit.value = false
  editForm.id = null
  editForm.employeeId = null
  editForm.employeeName = ''
  editForm.department = ''
  editForm.reimbursementNo = ''
  editForm.type = ''
  editForm.amount = 0
  editForm.reason = ''
  editForm.status = '待审批'
  editForm.applyDate = ''
  editForm.managerApproveDate = ''
  editForm.managerApprover = ''
  editForm.managerRemark = ''
  editForm.financeApproveDate = ''
  editForm.financeApprover = ''
  editForm.financeRemark = ''
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

function handleEmployeeChange(employeeId) {
  const employee = employeeOptions.value.find(e => e.id === employeeId)
  if (employee) {
    editForm.employeeName = employee.name
    editForm.department = employee.department
  }
}

function handleEdit(record) {
  isEdit.value = true
  editForm.id = record.id
  editForm.employeeId = record.employeeId
  editForm.employeeName = record.employeeName
  editForm.department = record.department
  editForm.reimbursementNo = record.reimbursementNo
  editForm.type = record.type
  editForm.amount = Number(record.amount) || 0
  editForm.reason = record.reason || ''
  editForm.status = record.status || '待审批'
  editForm.applyDate = record.applyDate || ''
  editForm.managerApproveDate = record.managerApproveDate || ''
  editForm.managerApprover = record.managerApprover || ''
  editForm.managerRemark = record.managerRemark || ''
  editForm.financeApproveDate = record.financeApproveDate || ''
  editForm.financeApprover = record.financeApprover || ''
  editForm.financeRemark = record.financeRemark || ''
  editForm.remark = record.remark || ''
  editModalVisible.value = true
}

async function handleSubmit() {
  if (!editForm.employeeId) {
    message.error('请选择员工')
    return
  }
  if (!editForm.type) {
    message.error('请选择报销类型')
    return
  }
  if (!editForm.amount || editForm.amount <= 0) {
    message.error('请输入有效金额')
    return
  }
  if (!editForm.reason) {
    message.error('请输入报销事由')
    return
  }

  try {
    const data = { ...editForm }
    if (!data.reimbursementNo) {
      data.reimbursementNo = 'BX-' + new Date().getFullYear() + '-' + String(Date.now()).slice(-4)
    }
    if (!data.applyDate) {
      data.applyDate = new Date().toISOString().split('T')[0]
    }

    let res
    if (isEdit.value) {
      res = await reimbursementApi.updateReimbursement(editForm.id, data)
    } else {
      res = await reimbursementApi.createReimbursement(data)
    }

    if (res.data.success && res.data.code === 200) {
      message.success(isEdit.value ? '报销更新成功' : '报销添加成功')
      editModalVisible.value = false
      fetchReimbursements()
    } else {
      throw new Error(res.data.message || '操作失败')
    }
  } catch (error) {
    message.error(error.message || '操作失败')
    console.error('操作失败:', error)
  }
}

function handleDelete(record) {
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除报销单 ${record.reimbursementNo} 吗？`,
    okText: '确定',
    cancelText: '取消',
    async onOk() {
      try {
        const res = await reimbursementApi.deleteReimbursement(record.id)
        if (res.data.success) {
          message.success('删除成功')
          fetchReimbursements()
        } else {
          throw new Error(res.data.message)
        }
      } catch (error) {
        message.error(error.message || '删除失败')
      }
    }
  })
}

function handleManagerApprove(record) {
  currentReimbursement.value = record
  currentApproveType.value = 'manager'
  approveForm.approver = authStore.realName || ''
  approveForm.remark = ''
  approveModalTitle.value = '部门经理审批'
  approveModalVisible.value = true
}

function handleFinanceApprove(record) {
  currentReimbursement.value = record
  currentApproveType.value = 'finance'
  approveForm.approver = authStore.realName || ''
  approveForm.remark = ''
  approveModalTitle.value = '财务审批'
  approveModalVisible.value = true
}

async function handleApproveSubmit() {
  if (!approveForm.approver) {
    message.error('请输入审批人')
    return
  }

  try {
    let res
    if (currentApproveType.value === 'manager') {
      res = await reimbursementApi.managerApprove(currentReimbursement.value.id, {
        approver: approveForm.approver,
        remark: approveForm.remark || '同意'
      })
    } else {
      res = await reimbursementApi.financeApprove(currentReimbursement.value.id, {
        approver: approveForm.approver,
        remark: approveForm.remark || '审核通过'
      })
    }

    if (res.data.success) {
      message.success('审批成功')
      approveModalVisible.value = false
      fetchReimbursements()
    } else {
      throw new Error(res.data.message || '审批失败')
    }
  } catch (error) {
    message.error(error.message || '审批失败')
    console.error('审批失败:', error)
  }
}

async function handleRejectSubmit() {
  if (!approveForm.approver) {
    message.error('请输入审批人')
    return
  }
  if (!approveForm.remark) {
    message.error('请输入驳回原因')
    return
  }

  try {
    let res
    if (currentApproveType.value === 'manager') {
      res = await reimbursementApi.managerReject(currentReimbursement.value.id, {
        approver: approveForm.approver,
        remark: approveForm.remark
      })
    } else {
      res = await reimbursementApi.financeReject(currentReimbursement.value.id, {
        approver: approveForm.approver,
        remark: approveForm.remark
      })
    }

    if (res.data.success) {
      message.success('驳回成功')
      approveModalVisible.value = false
      fetchReimbursements()
    } else {
      throw new Error(res.data.message || '驳回失败')
    }
  } catch (error) {
    message.error(error.message || '驳回失败')
    console.error('驳回失败:', error)
  }
}

function getStatusColor(status) {
  const colorMap = {
    '待审批': 'orange',
    '待财务审批': 'blue',
    '已审批': 'green',
    '已驳回': 'red'
  }
  return colorMap[status] || 'default'
}

const approveModalTitle = ref('')
</script>

<style scoped>
.page-container {
  padding: 24px;
  background: #f5f5f5;
  min-height: calc(100vh - 64px);
}

.card-container {
  margin-bottom: 24px;
}

.search-bar {
  margin-bottom: 16px;
}
</style>