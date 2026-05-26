<template>
  <div class="dashboard">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="banner-content">
        <div class="banner-text">
          <h1>欢迎回来，{{ authStore.username || '管理员' }} 👋</h1>
          <p>今天是 {{ currentDate }}，祝您工作愉快！</p>
        </div>
        <div class="banner-icon">
          <TeamOutlined />
        </div>
      </div>
    </div>

    <!-- 统计卡片区域 -->
    <div class="stats-grid">
      <div class="stat-card primary">
        <div class="stat-icon">
          <TeamOutlined />
        </div>
        <div class="stat-info">
          <h3>{{ employeeTotal }}</h3>
          <p>员工总数</p>
        </div>
        <div class="stat-trend up">
          <CaretUpOutlined />
        </div>
      </div>

      <div class="stat-card male">
        <div class="stat-icon">
          <ManOutlined />
        </div>
        <div class="stat-info">
          <h3>{{ maleCount }}</h3>
          <p>男性员工</p>
        </div>
        <div class="stat-percentage">
          {{ malePercentage }}%
        </div>
      </div>

      <div class="stat-card female">
        <div class="stat-icon">
          <WomanOutlined />
        </div>
        <div class="stat-info">
          <h3>{{ femaleCount }}</h3>
          <p>女性员工</p>
        </div>
        <div class="stat-percentage">
          {{ femalePercentage }}%
        </div>
      </div>

      <div class="stat-card success">
        <div class="stat-icon">
          <HomeOutlined />
        </div>
        <div class="stat-info">
          <h3>{{ departmentCount }}</h3>
          <p>部门数量</p>
        </div>
      </div>
    </div>

    <!-- 数据分析面板 -->
    <div class="data-analysis-card">
      <div class="analysis-header">
        <div class="header-left">
          <BarChartOutlined class="icon" />
          <h3>数据分析</h3>
        </div>
        <div class="header-right">
          <a-tag color="blue">{{ employeeTotal }} 条数据</a-tag>
        </div>
      </div>

      <div class="analysis-body">
        <!-- 左侧：性别分布 -->
        <div class="analysis-section gender">
          <div class="section-title-bar">
            <PieChartOutlined class="section-icon" />
            <span>性别分布</span>
          </div>

          <div class="gender-stats">
            <div class="gender-stat-item male">
              <div class="stat-avatar">👨</div>
              <div class="stat-details">
                <div class="stat-number">{{ maleCount }}</div>
                <div class="stat-label">男性员工</div>
              </div>
              <div class="stat-percentage-badge male">
                {{ malePercentage }}%
              </div>
            </div>

            <div class="gender-progress-wrapper">
              <div class="gender-progress-bar">
                <div
                  class="progress-fill male"
                  :style="{ width: malePercentage + '%' }"
                ></div>
              </div>
            </div>

            <div class="gender-stat-item female">
              <div class="stat-avatar">👩</div>
              <div class="stat-details">
                <div class="stat-number">{{ femaleCount }}</div>
                <div class="stat-label">女性员工</div>
              </div>
              <div class="stat-percentage-badge female">
                {{ femalePercentage }}%
              </div>
            </div>

            <div class="gender-progress-wrapper">
              <div class="gender-progress-bar">
                <div
                  class="progress-fill female"
                  :style="{ width: femalePercentage + '%' }"
                ></div>
              </div>
            </div>
          </div>

          <div class="total-summary">
            <div class="summary-label">总计</div>
            <div class="summary-value">{{ employeeTotal }}</div>
          </div>
        </div>

        <!-- 分隔线 -->
        <div class="divider"></div>

        <!-- 右侧：部门排行 -->
        <div class="analysis-section department">
          <div class="section-title-bar">
            <ApartmentOutlined class="section-icon" />
            <span>部门排行</span>
          </div>

          <div class="dept-rankings">
            <div
              v-for="(dept, index) in departmentStats"
              :key="dept.name"
              class="dept-ranking-item"
            >
              <div class="rank-badge" :class="'rank-' + (index + 1)">
                {{ index + 1 }}
              </div>
              <div class="dept-main">
                <div class="dept-name-row">
                  <span class="dept-emoji">{{ getDepartmentIcon(dept.name) }}</span>
                  <span class="dept-name-text">{{ dept.name }}</span>
                </div>
                <div class="dept-mini-progress">
                  <div
                    class="mini-bar"
                    :style="{ width: (dept.count / employeeTotal * 100) + '%', background: dept.color }"
                  ></div>
                </div>
              </div>
              <div class="dept-numbers">
                <strong>{{ dept.count }}</strong>
                <small>人</small>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 快捷操作 -->
    <div class="quick-actions">
      <h3 class="section-title">
        <ThunderboltOutlined /> 快捷操作
      </h3>
      <div class="action-buttons">
        <a-button
          type="primary"
          size="large"
          class="action-btn"
          @click="$router.push('/employees/add')"
        >
          <UserAddOutlined />
          添加员工
        </a-button>
        <a-button
          size="large"
          class="action-btn"
          @click="$router.push('/employees')"
        >
          <UnorderedListOutlined />
          查看列表
        </a-button>
        <a-button
          size="large"
          class="action-btn refresh"
          @click="fetchData"
          :loading="loading"
        >
          <ReloadOutlined />
          刷新数据
        </a-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useAuthStore } from '../store/auth';
import { useEmployeeStore } from '../store/employee';
import dayjs from 'dayjs';
import {
  TeamOutlined,
  ManOutlined,
  WomanOutlined,
  HomeOutlined,
  PieChartOutlined,
  BarChartOutlined,
  ApartmentOutlined,
  ThunderboltOutlined,
  UserAddOutlined,
  UnorderedListOutlined,
  ReloadOutlined,
  CaretUpOutlined
} from '@ant-design/icons-vue';

const authStore = useAuthStore();
const employeeStore = useEmployeeStore();
const loading = computed(() => employeeStore.loading);

const currentDate = dayjs().format('YYYY年MM月DD日 dddd');

const employeeRecords = computed(() => employeeStore.employees.records);
const employeeTotal = computed(() => employeeStore.employees.total);

// 计算统计数据
const maleCount = computed(() => {
  return employeeRecords.value.filter(emp => emp.gender === '男').length;
});

const femaleCount = computed(() => {
  return employeeRecords.value.filter(emp => emp.gender === '女').length;
});

const departmentCount = computed(() => {
  const departments = new Set(employeeRecords.value.map(emp => emp.department));
  return departments.size;
});

const malePercentage = computed(() => {
  if (employeeTotal.value === 0) return 0;
  return Math.round((maleCount.value / employeeTotal.value) * 100);
});

const femalePercentage = computed(() => {
  if (employeeTotal.value === 0) return 0;
  return Math.round((femaleCount.value / employeeTotal.value) * 100);
});

// 部门统计
const departmentStats = computed(() => {
  const deptMap = {};
  employeeRecords.value.forEach(emp => {
    if (!deptMap[emp.department]) {
      deptMap[emp.department] = { name: emp.department, count: 0 };
    }
    deptMap[emp.department].count++;
  });

  const colors = ['#667eea', '#f093fb', '#4facfe', '#43e97b', '#fa709a'];
  return Object.values(deptMap)
    .sort((a, b) => b.count - a.count)
    .map((dept, index) => ({
      ...dept,
      color: colors[index % colors.length]
    }));
});

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
  await fetchData();
});

const fetchData = async () => {
  try {
    await employeeStore.fetchEmployees({
      page: 1,
      pageSize: 100
    });
  } catch (error) {
    console.error('获取数据失败');
  }
};
</script>

<style scoped>
.dashboard {
  animation: fadeInUp 0.5s ease-out;
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

/* 欢迎横幅 */
.welcome-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 10px;
  padding: 20px 24px;
  margin-bottom: 12px;
  box-shadow: 0 3px 14px rgba(102, 126, 234, 0.22);
}

.banner-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.banner-text h1 {
  color: white;
  font-size: 22px;
  font-weight: 600;
  margin: 0 0 6px 0 !important;
}

.banner-text p {
  color: rgba(255, 255, 255, 0.9);
  margin: 0 !important;
  font-size: 13px;
}

.banner-icon {
  font-size: 50px;
  color: rgba(255, 255, 255, 0.2);
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

/* 统计卡片 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  margin-bottom: 12px;
}

.stat-card {
  background: white;
  padding: 16px 18px;
  border-radius: 10px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.03);
  display: flex;
  align-items: center;
  gap: 12px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
}

.stat-card.primary::before { background: linear-gradient(90deg, #667eea, #764ba2); }
.stat-card.male::before { background: linear-gradient(90deg, #f093fb, #f5576c); }
.stat-card.female::before { background: linear-gradient(90deg, #4facfe, #00f2fe); }
.stat-card.success::before { background: linear-gradient(90deg, #43e97b, #38f9d7); }

.stat-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.12);
}

.stat-icon {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}

.primary .stat-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}
.male .stat-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
}
.female .stat-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
}
.success .stat-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
  color: white;
}

.stat-info h3 {
  font-size: 26px;
  font-weight: 700;
  margin: 0 0 2px 0 !important;
  color: #262626;
}

.stat-info p {
  margin: 0 !important;
  color: #8c8c8c;
  font-size: 12px;
  font-weight: 500;
}

.stat-trend {
  position: absolute;
  top: 16px;
  right: 16px;
  color: #52c41a;
  font-size: 20px;
}

.stat-percentage {
  position: absolute;
  top: 16px;
  right: 20px;
  font-size: 18px;
  font-weight: 600;
  color: #8c8c8c;
}

/* 数据分析面板 */
.data-analysis-card {
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.03);
  overflow: hidden;
  margin-bottom: 12px;
}

.analysis-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 20px;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-bottom: 1px solid #e8e8e8;
}

.analysis-header .icon {
  font-size: 24px;
  color: #667eea;
}

.analysis-header h3 {
  margin: 0 !important;
  font-size: 18px;
  font-weight: 600;
  color: #262626;
}

.analysis-body {
  display: flex;
  min-height: 280px;
}

/* 左右分区 */
.analysis-section {
  flex: 1;
  padding: 16px 20px;
}

.divider {
  width: 1px;
  background: linear-gradient(180deg, transparent 0%, #e8e8e8 20%, #e8e8e8 80%, transparent 100%);
  flex-shrink: 0;
}

/* 小标题 */
.section-title-bar {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 14px;
  font-size: 14px;
  font-weight: 600;
  color: #595959;
}

.section-icon {
  font-size: 16px;
  color: #667eea;
}

/* 性别分布 */
.gender-stats {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.gender-stat-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 12px;
  border-radius: 8px;
  transition: all 0.3s;
}

.gender-stat-item:hover {
  background: #fafbfc;
  transform: translateX(4px);
}

.stat-avatar {
  font-size: 22px;
  width: 34px;
  height: 34px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  border-radius: 8px;
}

.stat-details {
  flex: 1;
}

.stat-number {
  font-size: 18px;
  font-weight: 700;
  color: #262626;
  line-height: 1.2;
}

.stat-label {
  font-size: 12px;
  color: #8c8c8c;
  margin-top: 1px;
}

.stat-percentage-badge {
  padding: 3px 10px;
  border-radius: 14px;
  font-size: 12px;
  font-weight: 700;
}

.stat-percentage-badge.male {
  background: linear-gradient(135deg, #fff0f6, #ffebf0);
  color: #f5576c;
}

.stat-percentage-badge.female {
  background: linear-gradient(135deg, #e6f7ff, #bae7ff);
  color: #1890ff;
}

.gender-progress-wrapper {
  margin-left: 44px;
}

.gender-progress-bar {
  height: 5px;
  background: #f5f5f5;
  border-radius: 2px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  border-radius: 4px;
  transition: width 1s ease;
}

.progress-fill.male {
  background: linear-gradient(90deg, #f093fb, #f5576c);
}

.progress-fill.female {
  background: linear-gradient(90deg, #4facfe, #00f2fe);
}

.total-summary {
  margin-top: 14px;
  padding: 14px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.18);
}

.summary-label {
  color: rgba(255, 255, 255, 0.85);
  font-size: 11px;
  margin-bottom: 3px;
}

.summary-value {
  font-size: 26px;
  font-weight: 700;
  color: white;
}

/* 部门排行 */
.dept-rankings {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.dept-ranking-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 8px;
  border-radius: 6px;
  transition: all 0.3s;
}

.dept-ranking-item:hover {
  background: #fafbfc;
  transform: translateX(4px);
}

.rank-badge {
  width: 24px;
  height: 24px;
  border-radius: 5px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 11px;
  flex-shrink: 0;
}

.rank-1 {
  background: linear-gradient(135deg, #ffd700, #ffed4e);
  color: #fff;
  text-shadow: 0 1px 2px rgba(0,0,0,0.2);
}

.rank-2 {
  background: linear-gradient(135deg, #e0e0e0, #f5f5f5);
  color: #666;
}

.rank-3 {
  background: linear-gradient(135deg, #d4a574, #e8c49a);
  color: #fff;
}

.rank-badge:not(.rank-1):not(.rank-2):not(.rank-3) {
  background: #f0f0f0;
  color: #999;
}

.dept-main {
  flex: 1;
  min-width: 0;
}

.dept-name-row {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-bottom: 3px;
}

.dept-emoji {
  font-size: 14px;
}

.dept-name-text {
  font-weight: 500;
  color: #262626;
  font-size: 12px;
}

.dept-mini-progress {
  height: 3px;
  background: #f5f5f5;
  border-radius: 1px;
  overflow: hidden;
}

.mini-bar {
  height: 100%;
  border-radius: 3px;
  transition: width 0.8s ease;
}

.dept-numbers {
  text-align: right;
  min-width: 36px;
}

.dept-numbers strong {
  font-size: 14px;
  font-weight: 700;
  color: #262626;
  display: block;
  line-height: 1.2;
}

.dept-numbers small {
  font-size: 9px;
  color: #8c8c8c;
}

/* 快捷操作 */
.quick-actions {
  background: white;
  padding: 16px 20px;
  border-radius: 10px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.03);
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #262626;
  margin: 0 0 14px 0 !important;
}

.section-title .anticon {
  color: #faad14;
  font-size: 20px;
}

.action-buttons {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.action-btn {
  height: 38px;
  padding: 0 20px;
  font-size: 13px;
  font-weight: 500;
  border-radius: 6px;
  transition: all 0.3s;
}

.action-btn:first-child {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  color: white;
}

.action-btn:first-child:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.35);
}

.action-btn:not(:first-child):hover {
  transform: translateY(-2px);
  border-color: #667eea;
  color: #667eea;
}

.action-btn.refresh:hover {
  border-color: #52c41a;
  color: #52c41a;
}

/* 响应式设计 */
@media (max-width: 1400px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .analysis-body {
    flex-direction: column;
  }

  .divider {
    width: 100%;
    height: 1px;
    background: linear-gradient(90deg, transparent 0%, #e8e8e8 50%, transparent 100%);
  }
}

@media (max-width: 768px) {
  .welcome-banner {
    padding: 16px;
  }

  .banner-content {
    flex-direction: column;
    text-align: center;
    gap: 12px;
  }

  .banner-text h1 {
    font-size: 18px;
  }

  .banner-icon {
    font-size: 40px;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .action-buttons {
    flex-direction: column;
  }

  .action-btn {
    width: 100%;
  }
}
</style>