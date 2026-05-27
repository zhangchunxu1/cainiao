<template>
  <div class="announcement-list">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">
          <NotificationOutlined />
          公告通知
        </h2>
        <p class="page-subtitle">发布和管理公司公告通知</p>
      </div>
      <div class="header-right">
        <a-button type="primary" @click="showAddModal" size="large">
          <PlusOutlined />
          发布公告
        </a-button>
      </div>
    </div>

    <!-- 搜索卡片 -->
    <div class="search-card">
      <a-form layout="inline" class="search-form">
        <a-form-item>
          <a-input
            v-model:value="searchKeyword"
            placeholder="搜索公告标题/内容"
            allowClear
            size="large"
            @pressEnter="handleSearch"
          >
            <template #prefix>
              <SearchOutlined style="color: #bfbfbf;" />
            </template>
          </a-input>
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
            <a-select-option value="通知">通知</a-select-option>
            <a-select-option value="公告">公告</a-select-option>
            <a-select-option value="紧急">紧急</a-select-option>
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

    <!-- 公告列表卡片 -->
    <div class="announcement-cards" v-if="!loading && announcementRecords.length > 0">
      <div
        v-for="record in announcementRecords"
        :key="record.id"
        class="announcement-card"
        :class="{ 'is-top': record.isTop === 1 }"
      >
        <div class="card-header">
          <div class="card-title-area">
            <a-tag v-if="record.isTop === 1" color="red" class="top-tag">置顶</a-tag>
            <a-tag :color="getTypeColor(record.type)" class="type-tag">{{ record.type }}</a-tag>
            <h3 class="card-title" @click="handleView(record)">{{ record.title }}</h3>
          </div>
          <div class="card-actions">
            <a-dropdown :trigger="['click']">
              <a-button type="text" size="small">
                <MoreOutlined />
              </a-button>
              <template #overlay>
                <a-menu>
                  <a-menu-item key="edit" @click="handleEdit(record)">
                    <EditOutlined /> 编辑
                  </a-menu-item>
                  <a-menu-divider />
                  <a-menu-item key="delete" danger @click="handleDelete(record)">
                    <DeleteOutlined /> 删除
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </div>
        </div>

        <div class="card-content" @click="handleView(record)">
          {{ record.content.substring(0, 150) }}{{ record.content.length > 150 ? '...' : '' }}
        </div>

        <div class="card-footer">
          <span class="publisher">
            <UserOutlined /> {{ record.publisher || '系统' }}
          </span>
          <span class="time">
            <ClockCircleOutlined /> {{ record.createTime }}
          </span>
          <span class="views">
            <EyeOutlined /> {{ record.viewCount || 0 }} 次浏览
          </span>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-if="!loading && announcementRecords.length === 0" class="empty-state">
      <a-empty description="暂无公告数据" />
    </div>

    <!-- 分页 -->
    <div class="pagination-wrapper" v-if="announcementTotal > 0">
      <a-pagination
        v-model:current="currentPage"
        :total="announcementTotal"
        :pageSize="pageSize"
        showSizeChanger
        showQuickJumper
        :showTotal="(total) => `共 ${total} 条记录`"
        @change="handlePageChange"
        @showSizeChange="handleSizeChange"
      />
    </div>

    <!-- 添加/编辑弹窗 -->
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
          <a-col :span="16">
            <a-form-item label="公告标题" name="title">
              <a-input v-model:value="formData.title" placeholder="请输入公告标题" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="类型" name="type">
              <a-select v-model:value="formData.type" placeholder="请选择类型">
                <a-select-option value="通知">通知</a-select-option>
                <a-select-option value="公告">公告</a-select-option>
                <a-select-option value="紧急">紧急</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>

        <a-form-item label="发布人" name="publisher">
          <a-input v-model:value="formData.publisher" placeholder="请输入发布人" />
        </a-form-item>

        <a-form-item label="公告内容" name="content">
          <a-textarea v-model:value="formData.content" placeholder="请输入公告内容" :rows="6" />
        </a-form-item>

        <a-form-item label="是否置顶" name="isTop">
          <a-switch v-model:checked="formData.isTop" checkedChildren="是" unCheckedChildren="否" />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 详情弹窗 -->
    <a-modal
      v-model:open="detailVisible"
      title="公告详情"
      width="800px"
      :footer="null"
    >
      <div v-if="currentRecord" class="detail-content">
        <h2 style="margin-bottom: 12px;">{{ currentRecord.title }}</h2>
        <div class="detail-meta">
          <a-space>
            <a-tag :color="getTypeColor(currentRecord.type)">{{ currentRecord.type }}</a-tag>
            <span><UserOutlined /> {{ currentRecord.publisher }}</span>
            <span><ClockCircleOutlined /> {{ currentRecord.createTime }}</span>
            <span><EyeOutlined /> {{ currentRecord.viewCount }} 次浏览</span>
          </a-space>
        </div>
        <a-divider />
        <div class="detail-text">{{ currentRecord.content }}</div>
      </div>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import {
  NotificationOutlined,
  PlusOutlined,
  SearchOutlined,
  ReloadOutlined,
  EditOutlined,
  DeleteOutlined,
  MoreOutlined,
  UserOutlined,
  ClockCircleOutlined,
  EyeOutlined
} from '@ant-design/icons-vue'
import { announcementApi } from '../../api/announcement'

const loading = ref(false)
const submitLoading = ref(false)
const modalVisible = ref(false)
const detailVisible = ref(false)
const isEdit = ref(false)
const currentId = ref(null)
const currentRecord = ref(null)

const searchKeyword = ref('')
const selectedType = ref(undefined)
const currentPage = ref(1)
const pageSize = ref(10)
const announcementTotal = ref(0)
const announcementRecords = ref([])

const formRef = ref()
const formData = reactive({
  title: '',
  content: '',
  publisher: '',
  type: '通知',
  status: 1,
  isTop: false,
  viewCount: 0
})

const formRules = {
  title: [{ required: true, message: '请输入公告标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入公告内容', trigger: 'blur' }]
}

const modalTitle = computed(() => isEdit.value ? '编辑公告' : '发布公告')

const getTypeColor = (type) => {
  const colors = {
    '通知': 'blue',
    '公告': 'purple',
    '紧急': 'red'
  }
  return colors[type] || 'default'
}

const fetchAnnouncements = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value
    }

    if (searchKeyword.value) {
      params.keyword = searchKeyword.value
    }

    if (selectedType.value) {
      params.type = selectedType.value
    }

    const res = await announcementApi.getAnnouncementList(params)
    const data = res.data.data
    announcementRecords.value = data.records || []
    announcementTotal.value = data.total || 0
  } catch (error) {
    message.error('获取公告列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchAnnouncements()
}

const handleReset = () => {
  searchKeyword.value = ''
  selectedType.value = undefined
  currentPage.value = 1
  fetchAnnouncements()
}

const handlePageChange = (page, size) => {
  currentPage.value = page
  pageSize.value = size
  fetchAnnouncements()
}

const handleSizeChange = (current, size) => {
  pageSize.value = size
  currentPage.value = 1
  fetchAnnouncements()
}

const showAddModal = () => {
  isEdit.value = false
  currentId.value = null
  Object.assign(formData, {
    title: '',
    content: '',
    publisher: '',
    type: '通知',
    status: 1,
    isTop: false,
    viewCount: 0
  })
  modalVisible.value = true
}

const handleEdit = (record) => {
  isEdit.value = true
  currentId.value = record.id
  Object.assign(formData, {
    title: record.title,
    content: record.content,
    publisher: record.publisher,
    type: record.type,
    status: record.status,
    isTop: record.isTop === 1,
    viewCount: record.viewCount
  })
  modalVisible.value = true
}

const handleView = async (record) => {
  try {
    const res = await announcementApi.getAnnouncementById(record.id)
    currentRecord.value = res.data.data
    detailVisible.value = true
  } catch (error) {
    message.error('获取公告详情失败')
  }
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitLoading.value = true

    const submitData = {
      ...formData,
      isTop: formData.isTop ? 1 : 0
    }

    if (isEdit.value) {
      await announcementApi.updateAnnouncement(currentId.value, submitData)
      message.success('更新成功')
    } else {
      await announcementApi.addAnnouncement(submitData)
      message.success('发布成功')
    }

    modalVisible.value = false
    fetchAnnouncements()
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
    await announcementApi.deleteAnnouncement(record.id)
    message.success('删除成功')
    fetchAnnouncements()
  } catch (error) {
    message.error(error.message || '删除失败')
  }
}

onMounted(() => {
  fetchAnnouncements()
})
</script>

<style scoped>
.announcement-list {
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

.announcement-cards {
  display: grid;
  gap: 16px;
}

.announcement-card {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  border-left: 4px solid #667eea;
  transition: all 0.3s ease;
  cursor: pointer;
}

.announcement-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.announcement-card.is-top {
  border-left-color: #ff4d4f;
  background: linear-gradient(to right, #fff5f5, #ffffff);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.card-title-area {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.top-tag {
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
  cursor: pointer;
  transition: color 0.3s ease;
}

.card-title:hover {
  color: #667eea;
}

.card-content {
  color: #6b7280;
  line-height: 1.6;
  margin-bottom: 12px;
  font-size: 14px;
}

.card-footer {
  display: flex;
  gap: 20px;
  padding-top: 12px;
  border-top: 1px solid #f3f4f6;
  font-size: 13px;
  color: #9ca3af;
}

.card-footer span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.empty-state {
  text-align: center;
  padding: 60px 0;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.pagination-wrapper {
  margin-top: 24px;
  text-align: right;
}

.detail-content .detail-meta {
  color: #6b7280;
  font-size: 14px;
}

.detail-text {
  line-height: 1.8;
  color: #374151;
  white-space: pre-wrap;
}
</style>