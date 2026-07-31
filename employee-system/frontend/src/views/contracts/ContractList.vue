<template>
  <div class="contract-list">
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">
          <FileTextOutlined />
          合同管理
        </h2>
        <p class="page-subtitle">管理和查看员工合同信息</p>
      </div>
      <div class="header-right">
        <a-button v-if="selectedRowKeys.length > 0" danger @click="handleBatchDelete" :loading="batchDeleteLoading" size="large">
          <DeleteOutlined /> 批量删除 ({{ selectedRowKeys.length }})
        </a-button>
        <a-button type="primary" @click="showAddModal" size="large">
          <PlusOutlined />
          添加合同
        </a-button>
      </div>
    </div>

    <div class="search-card">
      <a-form layout="inline" class="search-form">
        <a-form-item>
          <a-input
            v-model:value="searchKeyword"
            placeholder="搜索合同编号/名称/甲乙双方/员工姓名"
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
            <a-select-option value="生效中">生效中</a-select-option>
            <a-select-option value="已到期">已到期</a-select-option>
            <a-select-option value="已终止">已终止</a-select-option>
            <a-select-option value="待签署">待签署</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="类型：">
          <a-select
            v-model:value="selectedType"
            placeholder="选择类型"
            allowClear
            size="large"
            style="width: 150px;"
            @change="handleSearch"
          >
            <a-select-option value="劳动合同">劳动合同</a-select-option>
            <a-select-option value="劳务合同">劳务合同</a-select-option>
            <a-select-option value="保密协议">保密协议</a-select-option>
            <a-select-option value="培训协议">培训协议</a-select-option>
            <a-select-option value="其他">其他</a-select-option>
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
          <span>合同列表</span>
          <a-tag color="blue">{{ contractTotal }} 条记录</a-tag>
        </div>
      </div>

      <a-table
        :dataSource="contractRecords"
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
          <template v-else-if="column.key === 'contractType'">
            <a-tag color="processing">{{ record.contractType }}</a-tag>
          </template>
          <template v-else-if="column.key === 'contractAmount'">
            <span>{{ record.currency || 'CNY' }} {{ formatAmount(record.contractAmount) }}</span>
          </template>
          <template v-else-if="column.key === 'action'">
            <div style="display: flex; align-items: center; justify-content: center;">
              <a-space>
                <a-tooltip title="查看详情">
                  <a-button type="link" @click="handleViewDetail(record)">
                    <EyeOutlined />
                  </a-button>
                </a-tooltip>

                <a-tooltip title="编辑">
                  <a-button type="link" @click="handleEdit(record)">
                    <EditOutlined />
                  </a-button>
                </a-tooltip>

                <a-popconfirm
                  title="确定要删除这条合同吗？"
                  ok-text="确定"
                  cancel-text="取消"
                  @confirm="handleDelete(record)"
                >
                  <a-button type="link" danger>
                    <DeleteOutlined />
                  </a-button>
                </a-popconfirm>
              </a-space>
            </div>
          </template>
        </template>
      </a-table>

      <div class="pagination-wrapper" v-if="contractTotal > 0">
        <a-pagination
          v-model:current="currentPage"
          :total="contractTotal"
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
      width="800px"
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
            <a-form-item label="合同编号" name="contractNo">
              <a-input v-model:value="formData.contractNo" placeholder="请输入合同编号" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="合同名称" name="contractName">
              <a-input v-model:value="formData.contractName" placeholder="请输入合同名称" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="甲方" name="partyA">
              <a-input v-model:value="formData.partyA" placeholder="请输入甲方名称" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="乙方" name="partyB">
              <a-input v-model:value="formData.partyB" placeholder="请输入乙方名称" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="合同类型" name="contractType">
              <a-select v-model:value="formData.contractType" placeholder="请选择合同类型">
                <a-select-option value="劳动合同">劳动合同</a-select-option>
                <a-select-option value="劳务合同">劳务合同</a-select-option>
                <a-select-option value="保密协议">保密协议</a-select-option>
                <a-select-option value="培训协议">培训协议</a-select-option>
                <a-select-option value="其他">其他</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="合同状态" name="status">
              <a-select v-model:value="formData.status" placeholder="请选择合同状态">
                <a-select-option value="生效中">生效中</a-select-option>
                <a-select-option value="已到期">已到期</a-select-option>
                <a-select-option value="已终止">已终止</a-select-option>
                <a-select-option value="待签署">待签署</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item label="签订日期" name="signDate">
              <a-date-picker
                v-model:value="formData.signDate"
                format="YYYY-MM-DD"
                valueFormat="YYYY-MM-DD"
                style="width: 100%;"
                placeholder="选择签订日期"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
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
          <a-col :span="8">
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

        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item label="合同金额" name="contractAmount">
              <a-input-number
                v-model:value="formData.contractAmount"
                :min="0"
                :precision="2"
                style="width: 100%;"
                placeholder="请输入金额"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="货币类型">
              <a-select v-model:value="formData.currency" style="width: 100%;">
                <a-select-option value="CNY">人民币</a-select-option>
                <a-select-option value="USD">美元</a-select-option>
                <a-select-option value="EUR">欧元</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="关联员工">
              <a-select
                v-model:value="formData.employeeId"
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
          </a-col>
        </a-row>

        <a-form-item label="合同内容">
          <div ref="quillRef" class="quill-editor"></div>
        </a-form-item>

        <a-form-item label="备注">
          <a-textarea v-model:value="formData.remark" placeholder="请输入备注" :rows="3" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="detailVisible"
      title="合同详情"
      width="900px"
      :footer="null"
      :bodyStyle="{ padding: '0' }"
    >
      <div v-if="currentRecord" class="contract-detail">
        <div class="detail-header">
          <div class="header-info">
            <h3>{{ currentRecord.contractName }}</h3>
            <div class="header-tags">
              <a-tag :color="getStatusColor(currentRecord.status)">{{ currentRecord.status }}</a-tag>
              <a-tag color="processing">{{ currentRecord.contractType }}</a-tag>
            </div>
          </div>
          <div class="header-no">
            合同编号：{{ currentRecord.contractNo }}
          </div>
        </div>

        <div class="detail-body">
          <div class="doc-section">
            <h4 class="section-title">合同信息</h4>
            <a-descriptions bordered :column="2" size="small">
              <a-descriptions-item label="甲方">{{ currentRecord.partyA || '-' }}</a-descriptions-item>
              <a-descriptions-item label="乙方">{{ currentRecord.partyB || '-' }}</a-descriptions-item>
              <a-descriptions-item label="签订日期">{{ currentRecord.signDate || '-' }}</a-descriptions-item>
              <a-descriptions-item label="生效日期">{{ currentRecord.startDate || '-' }}</a-descriptions-item>
              <a-descriptions-item label="到期日期">{{ currentRecord.endDate || '-' }}</a-descriptions-item>
              <a-descriptions-item label="合同金额">{{ currentRecord.currency || 'CNY' }} {{ formatAmount(currentRecord.contractAmount) }}</a-descriptions-item>
              <a-descriptions-item label="关联员工">{{ currentRecord.employeeName || '-' }}</a-descriptions-item>
              <a-descriptions-item label="所属部门">{{ currentRecord.department || '-' }}</a-descriptions-item>
            </a-descriptions>
          </div>

          <div class="doc-section">
            <h4 class="section-title">合同正文</h4>
            <div class="doc-content" v-html="currentRecord.contractContent || '<p style=\'color: #999;\'>暂无合同内容</p>'"></div>
          </div>

          <div class="doc-section" v-if="currentRecord.remark">
            <h4 class="section-title">备注</h4>
            <p class="remark-content">{{ currentRecord.remark }}</p>
          </div>
        </div>

        <div class="detail-footer">
          <a-descriptions :column="2" size="small">
            <a-descriptions-item label="创建时间">{{ currentRecord.createdTime || '-' }}</a-descriptions-item>
            <a-descriptions-item label="更新时间">{{ currentRecord.updatedTime || '-' }}</a-descriptions-item>
          </a-descriptions>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { message, Modal } from 'ant-design-vue'
import {
  FileTextOutlined,
  PlusOutlined,
  SearchOutlined,
  ReloadOutlined,
  EyeOutlined,
  EditOutlined,
  DeleteOutlined
} from '@ant-design/icons-vue'
import { contractApi } from '../../api/contract'
import { getEmployees } from '../../api/employee'
import Quill from 'quill'
import 'quill/dist/quill.snow.css'

const loading = ref(false)
const selectedRowKeys = ref([])
const batchDeleteLoading = ref(false)
let quillInstance = null

const destroyQuill = () => {
  if (quillInstance) {
    quillInstance = null
  }
}
const submitLoading = ref(false)
const modalVisible = ref(false)
const detailVisible = ref(false)
const isEdit = ref(false)

const searchKeyword = ref('')
const selectedStatus = ref(undefined)
const selectedType = ref(undefined)
const currentPage = ref(1)
const pageSize = ref(10)
const contractTotal = ref(0)
const contractRecords = ref([])
const currentRecord = ref(null)
const employeeOptions = ref([])

const formRef = ref()
const quillRef = ref(null)
const formData = reactive({
  id: null,
  contractNo: '',
  contractName: '',
  partyA: '',
  partyB: '',
  signDate: '',
  startDate: '',
  endDate: '',
  contractAmount: null,
  currency: 'CNY',
  contractType: '',
  status: '生效中',
  employeeId: null,
  employeeName: '',
  department: '',
  contractContent: '',
  attachments: '',
  remark: ''
})

const formRules = {
  contractNo: [{ required: true, message: '请输入合同编号', trigger: 'blur' }],
  contractName: [{ required: true, message: '请输入合同名称', trigger: 'blur' }],
  contractType: [{ required: true, message: '请选择合同类型', trigger: 'change' }],
  status: [{ required: true, message: '请选择合同状态', trigger: 'change' }]
}

const modalTitle = computed(() => isEdit.value ? '编辑合同' : '添加合同')

const rowSelection = computed(() => ({
  selectedRowKeys: selectedRowKeys.value,
  onChange: (keys) => { selectedRowKeys.value = keys }
}))

const columns = [
  {
    title: '合同编号',
    dataIndex: 'contractNo',
    key: 'contractNo',
    width: 140
  },
  {
    title: '合同名称',
    dataIndex: 'contractName',
    key: 'contractName',
    width: 160
  },
  {
    title: '合同类型',
    dataIndex: 'contractType',
    key: 'contractType',
    width: 120
  },
  {
    title: '甲方',
    dataIndex: 'partyA',
    key: 'partyA',
    width: 140
  },
  {
    title: '乙方',
    dataIndex: 'partyB',
    key: 'partyB',
    width: 140
  },
  {
    title: '签订日期',
    dataIndex: 'signDate',
    key: 'signDate',
    width: 120
  },
  {
    title: '合同金额',
    dataIndex: 'contractAmount',
    key: 'contractAmount',
    width: 160
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

const getStatusColor = (status) => {
  const colors = {
    '生效中': 'green',
    '已到期': 'default',
    '已终止': 'red',
    '待签署': 'orange'
  }
  return colors[status] || 'default'
}

const formatAmount = (amount) => {
  if (!amount) return '0.00'
  return parseFloat(amount).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

const fetchContracts = async () => {
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

    if (selectedType.value) {
      params.contractType = selectedType.value
    }

    const res = await contractApi.getContractList(params)
    const data = res.data.data
    contractRecords.value = data.records || []
    contractTotal.value = data.total || 0
  } catch (error) {
    message.error('获取合同列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchContracts()
}

const handleReset = () => {
  searchKeyword.value = ''
  selectedStatus.value = undefined
  selectedType.value = undefined
  currentPage.value = 1
  fetchContracts()
}

const handlePageChange = (page, size) => {
  currentPage.value = page
  pageSize.value = size
  fetchContracts()
}

const handleSizeChange = (current, size) => {
  pageSize.value = size
  currentPage.value = 1
  fetchContracts()
}

const initQuill = async (content = '') => {
  destroyQuill()
  await nextTick()
  if (quillRef.value) {
    quillInstance = new Quill(quillRef.value, {
      theme: 'snow',
      modules: {
        toolbar: [
          [{ 'header': [1, 2, 3, false] }],
          ['bold', 'italic', 'underline', 'strike'],
          [{ 'color': [] }, { 'background': [] }],
          [{ 'list': 'ordered' }, { 'list': 'bullet' }],
          [{ 'indent': '-1' }, { 'indent': '+1' }],
          [{ 'align': [] }],
          ['link', 'image'],
          ['clean']
        ]
      }
    })
    if (content) {
      quillInstance.root.innerHTML = content
    }
  }
}

const showAddModal = async () => {
  isEdit.value = false
  Object.assign(formData, {
    id: null,
    contractNo: '',
    contractName: '',
    partyA: '',
    partyB: '',
    signDate: '',
    startDate: '',
    endDate: '',
    contractAmount: null,
    currency: 'CNY',
    contractType: '',
    status: '生效中',
    employeeId: null,
    employeeName: '',
    department: '',
    contractContent: '',
    attachments: '',
    remark: ''
  })
  await loadAllEmployees()
  modalVisible.value = true
  initQuill('')
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

const handleEdit = async (record) => {
  isEdit.value = true
  try {
    const res = await contractApi.getContractById(record.id)
    const data = res.data.data
    Object.assign(formData, data)
    modalVisible.value = true
    initQuill(data.contractContent || '')
  } catch (error) {
    message.error('获取合同详情失败')
  }
}

const handleEmployeeChange = (employeeId) => {
  const employee = employeeOptions.value.find(e => e.id === employeeId)
  if (employee) {
    formData.employeeName = employee.name
    formData.department = employee.department
  }
}

const handleSubmit = async () => {
  try {
    if (quillInstance) {
      formData.contractContent = quillInstance.root.innerHTML
    }
    await formRef.value.validate()
    submitLoading.value = true

    if (isEdit.value) {
      await contractApi.updateContract(formData.id, formData)
      message.success('更新成功')
    } else {
      await contractApi.addContract(formData)
      message.success('添加成功')
    }
    modalVisible.value = false
    fetchContracts()
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
    await contractApi.deleteContract(record.id)
    message.success('删除成功')
    fetchContracts()
  } catch (error) {
    message.error(error.message || '删除失败')
  }
}

const handleBatchDelete = () => {
  Modal.confirm({
    title: '确认批量删除',
    content: `确定要删除选中的 ${selectedRowKeys.value.length} 条合同记录吗？此操作不可恢复。`,
    okText: '确定删除',
    okType: 'danger',
    cancelText: '取消',
    async onOk() {
      batchDeleteLoading.value = true;
      try {
        const res = await contractApi.batchDeleteContracts(selectedRowKeys.value);
        if (res.data.success) {
          message.success(`✅ 成功删除 ${res.data.data} 条记录`);
          selectedRowKeys.value = [];
          await fetchContracts();
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

const handleViewDetail = async (record) => {
  try {
    const res = await contractApi.getContractById(record.id)
    currentRecord.value = res.data.data
    detailVisible.value = true
  } catch (error) {
    message.error('获取合同详情失败')
  }
}

onMounted(() => {
  fetchContracts()
})
</script>

<style scoped>
.contract-list {
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

.contract-detail {
  min-height: 400px;
}

.detail-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #fff;
}

.detail-header h3 {
  margin: 0 0 8px 0;
  font-size: 20px;
}

.header-tags {
  display: flex;
  gap: 8px;
}

.header-tags :deep(.ant-tag) {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.3);
  color: #fff;
}

.header-no {
  font-size: 14px;
  opacity: 0.9;
}

.detail-body {
  padding: 24px;
}

.doc-section {
  margin-bottom: 24px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 2px solid #667eea;
}

.doc-content {
  background: #f9fafb;
  padding: 24px;
  border-radius: 8px;
  min-height: 200px;
  line-height: 1.8;
  color: #374151;
}

.doc-content p {
  margin: 0 0 16px 0;
}

.doc-content h1,
.doc-content h2,
.doc-content h3 {
  margin: 24px 0 16px 0;
}

.remark-content {
  background: #fffbe6;
  padding: 16px;
  border-radius: 8px;
  border-left: 4px solid #f59e0b;
  color: #92400e;
}

.detail-footer {
  background: #f9fafb;
  padding: 16px 24px;
  border-top: 1px solid #f0f0f0;
}

.quill-editor {
  min-height: 200px;
  position: relative;
  z-index: 1;
}

.quill-editor .ql-toolbar {
  border: 1px solid #d9d9d9;
  border-bottom: none;
  position: relative;
  z-index: 1;
}

.quill-editor .ql-container {
  border: 1px solid #d9d9d9;
  min-height: 200px;
  position: relative;
  z-index: 1;
}
</style>