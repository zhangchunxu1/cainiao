<!--
  Dashboard.vue - 系统首页仪表盘组件
  =====================================
  
  功能说明：
  - 展示系统欢迎信息和当前日期
  - 显示员工统计数据（总数、男女比例、部门数量）
  - 提供性别分布可视化（进度条形式）
  - 显示部门人员排行（按人数排序）
  - 提供快捷操作按钮（添加员工、查看列表、刷新数据）

  使用场景：
  - 用户登录后看到的首页
  - 快速了解公司人员概况
  - 作为系统导航的起点

  数据来源：
  - 员工数据：从 employeeStore 获取
  - 用户信息：从 authStore 获取
-->

<template>
  <div class="dashboard">
    <!-- 
      欢迎横幅区域
      - 显示用户名和问候语
      - 显示当前日期
      - 使用渐变背景增强视觉效果
    -->
    <div class="welcome-banner">
      <div class="banner-content">
        <div class="banner-text">
          <!-- 动态显示用户名，如果未登录则显示"管理员" -->
          <h1>欢迎回来，{{ authStore.username || '管理员' }} 👋</h1>
          <!-- 使用 dayjs 格式化当前日期为：2026年05月26日 星期二 -->
          <p>今天是 {{ currentDate }}，祝您工作愉快！</p>
        </div>
        <!-- 右侧装饰图标，使用浮动动画增加活力 -->
        <div class="banner-icon">
          <TeamOutlined />
        </div>
      </div>
    </div>

    <!-- 
      统计卡片网格区域
      - 使用 CSS Grid 布局，4列等宽排列
      - 每个卡片展示一个关键指标
      - 鼠标悬停时有上浮动画效果
    -->
    <div class="stats-grid">
      <!-- 卡片1：员工总数 -->
      <div class="stat-card primary">
        <div class="stat-icon">
          <TeamOutlined />
        </div>
        <div class="stat-info">
          <h3>{{ employeeTotal }}</h3>  <!-- 从计算属性获取总数 -->
          <p>员工总数</p>
        </div>
        <div class="stat-trend up">
          <CaretUpOutlined />  <!-- 上升趋势箭头 -->
        </div>
      </div>

      <!-- 卡片2：男性员工数量及占比 -->
      <div class="stat-card male">
        <div class="stat-icon">
          <ManOutlined />
        </div>
        <div class="stat-info">
          <h3>{{ maleCount }}</h3>  <!-- 计算得出男性人数 -->
          <p>男性员工</p>
        </div>
        <div class="stat-percentage">
          {{ malePercentage }}%  <!-- 占总员工的百分比 -->
        </div>
      </div>

      <!-- 卡片3：女性员工数量及占比 -->
      <div class="stat-card female">
        <div class="stat-icon">
          <WomanOutlined />
        </div>
        <div class="stat-info">
          <h3>{{ femaleCount }}</h3>  <!-- 计算得出女性人数 -->
          <p>女性员工</p>
        </div>
        <div class="stat-percentage">
          {{ femalePercentage }}%  <!-- 占总员工的百分比 -->
        </div>
      </div>

      <!-- 卡片4：部门数量统计 -->
      <div class="stat-card success">
        <div class="stat-icon">
          <HomeOutlined />
        </div>
        <div class="stat-info">
          <h3>{{ departmentCount }}</h3>  <!-- 使用Set去重后得到部门数 -->
          <p>部门数量</p>
        </div>
      </div>
    </div>

    <!-- 
      数据分析面板
      - 左右分栏布局：左侧性别分布，右侧部门排行
      - 使用Flexbox实现自适应宽度
      - 包含丰富的可视化元素
    -->
    <div class="data-analysis-card">
      <!-- 面板头部：标题和数据量标签 -->
      <div class="analysis-header">
        <div class="header-left">
          <BarChartOutlined class="icon" />  <!-- 图表图标 -->
          <h3>数据分析</h3>
        </div>
        <div class="header-right">
          <!-- 蓝色标签显示总数据条数 -->
          <a-tag color="blue">{{ employeeTotal }} 条数据</a-tag>
        </div>
      </div>

      <!-- 面板主体：左右分栏内容区 -->
      <div class="analysis-body">
        
        <!-- ==================== 左侧：性别分布模块 ==================== -->
        <div class="analysis-section gender">
          <!-- 小标题栏 -->
          <div class="section-title-bar">
            <PieChartOutlined class="section-icon" />  <!-- 饼图图标 -->
            <span>性别分布</span>
          </div>

          <!-- 性别统计数据列表 -->
          <div class="gender-stats">
            
            <!-- 男性员工统计项 -->
            <div class="gender-stat-item male">
              <div class="stat-avatar">👨</div>  <!-- 性别emoji头像 -->
              <div class="stat-details">
                <div class="stat-number">{{ maleCount }}</div>  <!-- 男性数量 -->
                <div class="stat-label">男性员工</div>
              </div>
              <!-- 百分比徽章，使用粉红色系 -->
              <div class="stat-percentage-badge male">
                {{ malePercentage }}%
              </div>
            </div>

            <!-- 男性占比进度条 -->
            <div class="gender-progress-wrapper">
              <div class="gender-progress-bar">
                <!-- 动态绑定宽度，实现可视化效果 -->
                <div
                  class="progress-fill male"
                  :style="{ width: malePercentage + '%' }"
                ></div>
              </div>
            </div>

            <!-- 女性员工统计项 -->
            <div class="gender-stat-item female">
              <div class="stat-avatar">👩</div>  <!-- 性别emoji头像 -->
              <div class="stat-details">
                <div class="stat-number">{{ femaleCount }}</div>  <!-- 女性数量 -->
                <div class="stat-label">女性员工</div>
              </div>
              <!-- 百分比徽章，使用蓝色系 -->
              <div class="stat-percentage-badge female">
                {{ femalePercentage }}%
              </div>
            </div>

            <!-- 女性占比进度条 -->
            <div class="gender-progress-wrapper">
              <div class="gender-progress-bar">
                <div
                  class="progress-fill female"
                  :style="{ width: femalePercentage + '%' }"
                ></div>
              </div>
            </div>
          </div>

          <!-- 总计汇总卡片 -->
          <div class="total-summary">
            <div class="summary-label">总计</div>
            <div class="summary-value">{{ employeeTotal }}</div>  <!-- 大号字体显示总数 -->
          </div>
        </div>

        <!-- 左右分隔线，使用渐变效果更美观 -->
        <div class="divider"></div>

        <!-- ==================== 右侧：部门排行模块 ==================== -->
        <div class="analysis-section department">
          <!-- 小标题栏 -->
          <div class="section-title-bar">
            <ApartmentOutlined class="section-icon" />  <!-- 建筑物图标代表部门 -->
            <span>部门排行</span>
          </div>

          <!-- 部门排名列表 -->
          <div class="dept-rankings">
            <!-- 
              v-for 循环遍历所有部门
              :key 使用部门名称作为唯一标识
              按 count 降序排列（人数多的在前）
            -->
            <div
              v-for="(dept, index) in departmentStats"
              :key="dept.name"
              class="dept-ranking-item"
            >
              <!-- 排名徽章：1-3名有特殊颜色样式 -->
              <div class="rank-badge" :class="'rank-' + (index + 1)">
                {{ index + 1 }}
              </div>
              
              <!-- 部门主要信息 -->
              <div class="dept-main">
                <div class="dept-name-row">
                  <!-- 根据部门名称返回对应的emoji图标 -->
                  <span class="dept-emoji">{{ getDepartmentIcon(dept.name) }}</span>
                  <span class="dept-name-text">{{ dept.name }}</span>
                </div>
                <!-- 迷你进度条，显示该部门占总人数的比例 -->
                <div class="dept-mini-progress">
                  <div
                    class="mini-bar"
                    :style="{ width: (dept.count / employeeTotal * 100) + '%', background: dept.color }"
                  ></div>
                </div>
              </div>
              
              <!-- 部门人数显示 -->
              <div class="dept-numbers">
                <strong>{{ dept.count }}</strong>  <!-- 数字 -->
                <small>人</small>  <!-- 单位 -->
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 
      快捷操作区域
      - 提供常用功能的快速入口
      - 减少用户点击次数，提升效率
    -->
    <div class="quick-actions">
      <h3 class="section-title">
        <ThunderboltOutlined /> 快捷操作  <!-- 闪电图标表示快捷 -->
      </h3>
      <div class="action-buttons">
        <!-- 按钮1：跳转到添加员工页面 -->
        <a-button
          type="primary"
          size="large"
          class="action-btn"
          @click="$router.push('/employees/add')"
        >
          <UserAddOutlined />
          添加员工
        </a-button>
        
        <!-- 按钮2：跳转到员工列表页面 -->
        <a-button
          size="large"
          class="action-btn"
          @click="$router.push('/employees')"
        >
          <UnorderedListOutlined />
          查看列表
        </a-button>
        
        <!-- 按钮3：刷新当前页面数据，带loading状态 -->
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
/*
  ====================================================
  JavaScript 逻辑部分 - Vue 3 Composition API
  ====================================================
  
  功能模块：
  1. 导入依赖和组件
  2. 定义响应式数据和状态管理
  3. 创建计算属性进行数据处理
  4. 定义方法处理业务逻辑
  5. 生命周期钩子初始化数据
*/

// 导入Vue核心API：ref(响应式引用), computed(计算属性), onMounted(挂载钩子)
import { ref, computed, onMounted } from 'vue';

// 导入Pinia状态管理Store
import { useAuthStore } from '../store/auth';       // 用户认证Store
import { useEmployeeStore } from '../store/employee'; // 员工数据Store

// 导入dayjs日期处理库，用于格式化日期
import dayjs from 'dayjs';

// 导入Ant Design Vue图标组件
// 这些图标用于UI展示，提升视觉效果
import {
  TeamOutlined,         // 团队图标 - 用于欢迎横幅和员工总数
  ManOutlined,           // 男性图标 - 用于男性员工统计
  WomanOutlined,         // 女性图标 - 用于女性员工统计
  HomeOutlined,          // 主页/房屋图标 - 用于部门数量
  PieChartOutlined,      // 饼图图标 - 用于性别分布标题
  BarChartOutlined,      // 柱状图图标 - 用于数据分析面板
  ApartmentOutlined,     // 建筑物图标 - 用于部门排行标题
  ThunderboltOutlined,   // 闪电图标 - 用于快捷操作
  UserAddOutlined,       // 添加用户图标 - 用于添加员工按钮
  UnorderedListOutlined, // 列表图标 - 用于查看列表按钮
  ReloadOutlined,        // 刷新图标 - 用于刷新按钮
  CaretUpOutlined        // 上箭头图标 - 用于趋势指示
} from '@ant-design/icons-vue';

// 实例化Store对象，用于访问全局状态
const authStore = useAuthStore();      // 认证状态：包含username、token等
const employeeStore = useEmployeeStore(); // 员工状态：包含employees列表、loading状态

// 定义计算属性：获取加载状态
// 当employeeStore.loading变化时自动更新
const loading = computed(() => employeeStore.loading);

// 使用dayjs格式化当前日期
// 输出格式示例：2026年05月26日 星期二
const currentDate = dayjs().format('YYYY年MM月DD日 dddd');

/* ===================================================
   计算属性：员工数据处理
   这些属性会根据员工数据自动计算并缓存结果
   只有依赖的数据变化时才会重新计算
================================================== */

// 获取员工记录数组（从Store中提取records字段）
const employeeRecords = computed(() => employeeStore.employees.records);

// 获取员工总数（从Store中提取total字段）
const employeeTotal = computed(() => employeeStore.employees.total);

// 计算男性员工数量
// 使用Array.filter()过滤出gender等于'男'的员工
const maleCount = computed(() => {
  return employeeRecords.value.filter(emp => emp.gender === '男').length;
});

// 计算女性员工数量
// 使用Array.filter()过滤出gender等于'女'的员工
const femaleCount = computed(() => {
  return employeeRecords.value.filter(emp => emp.gender === '女').length;
});

// 计算部门数量
// 使用Set数据结构去重，然后获取size
// Set会自动去除重复值
const departmentCount = computed(() => {
  const departments = new Set(employeeRecords.value.map(emp => emp.department));
  return departments.size;
});

// 计算男性员工占比（百分比）
// 四舍五入取整，避免小数点
const malePercentage = computed(() => {
  if (employeeTotal.value === 0) return 0;  // 避免除以0的错误
  return Math.round((maleCount.value / employeeTotal.value) * 100);
});

// 计算女性员工占比（百分比）
const femalePercentage = computed(() => {
  if (employeeTotal.value === 0) return 0;  // 避免除以0的错误
  return Math.round((femaleCount.value / employeeTotal.value) * 100);
});

/* ===================================================
   部门统计分析
   统计每个部门的员工数量，并按人数降序排列
   为每个部门分配不同的颜色用于可视化
================================================== */
const departmentStats = computed(() => {
  // 创建空对象用于存储部门统计信息
  const deptMap = {};
  
  // 遍历所有员工，按部门分组计数
  employeeRecords.value.forEach(emp => {
    // 如果该部门还没有在map中，则初始化
    if (!deptMap[emp.department]) {
      deptMap[emp.department] = { name: emp.department, count: 0 };
    }
    // 该部门人数+1
    deptMap[emp.department].count++;
  });

  // 定义颜色数组，循环使用这些颜色
  const colors = ['#667eea', '#f093fb', '#4facfe', '#43e97b', '#fa709a'];
  
  // 将对象转换为数组，并：
  // 1. 按count降序排列（人数多的在前）
  // 2. 为每个部门分配颜色（使用模运算循环颜色数组）
  return Object.values(deptMap)
    .sort((a, b) => b.count - a.count)  // 降序排序
    .map((dept, index) => ({
      ...dept,                           // 保留原有属性(name, count)
      color: colors[index % colors.length] // 分配颜色
    }));
});

/* ===================================================
   工具函数
   根据部门名称返回对应的emoji图标
   增强视觉识别度
================================================== */
const getDepartmentIcon = (dept) => {
  // 部门名称到图标的映射表
  const icons = {
    '技术部': '💻',   // 电脑 - 代表技术开发
    '人事部': '👥',   // 人物群组 - 代表人力资源管理
    '财务部': '💰',   // 钱袋 - 代表财务管理
    '市场部': '📈',   // 上升趋势图 - 代表市场推广
    '销售部': '🎯'    // 靶心 - 代表销售目标
  };
  // 如果找到对应图标就返回，否则返回默认建筑图标
  return icons[dept] || '🏢';
};

/* ===================================================
   生命周期钩子
   onMounted：组件挂载完成后执行
   相当于Vue 2中的created/mounted钩子
================================================== */
onMounted(async () => {
  // 页面加载时立即获取数据
  await fetchData();
});

/* ===================================================
   方法定义
   fetchData：获取员工数据的方法
   调用employeeStore的方法从后端API获取数据
================================================== */
const fetchData = async () => {
  try {
    // 调用Store的fetchEmployees方法
    // 参数说明：
    //   page: 1 - 第1页
    //   pageSize: 100 - 每页100条（确保能获取所有数据用于统计）
    await employeeStore.fetchEmployees({
      page: 1,
      pageSize: 100
    });
  } catch (error) {
    // 错误处理：打印错误日志
    console.error('获取数据失败');
  }
};
</script>

<style scoped>
/*
  ====================================================
  CSS样式部分
  scoped：样式只作用于当前组件，不会影响其他组件
  
  设计原则：
  1. 使用现代CSS特性（Grid、Flexbox、渐变、动画）
  2. 响应式设计，适配不同屏幕尺寸
  3. 微交互效果（hover、过渡动画）
  4. 统一的配色方案和圆角规范
  ====================================================
*/

.dashboard {
  /* 入场动画：淡入上移效果 */
  animation: fadeInUp 0.5s ease-out;
}

/* 定义fadeInUp关键帧动画 */
@keyframes fadeInUp {
  from {
    opacity: 0;               /* 初始透明度为0（不可见） */
    transform: translateY(20px); /* 初始位置向下偏移20px */
  }
  to {
    opacity: 1;               /* 最终完全可见 */
    transform: translateY(0);  /* 最终回到正常位置 */
  }
}

/* ====== 欢迎横幅样式 ====== */
.welcome-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); /* 紫色渐变背景 */
  border-radius: 10px;         /* 圆角 */
  padding: 20px 24px;          /* 内边距 */
  margin-bottom: 12px;         /* 底部间距 */
  box-shadow: 0 3px 14px rgba(102, 126, 234, 0.22); /* 阴影效果 */
}

.banner-content {
  display: flex;               /* Flex布局 */
  justify-content: space-between; /* 两端对齐 */
  align-items: center;         /* 垂直居中 */
}

.banner-text h1 {
  color: white;                /* 白色文字 */
  font-size: 22px;             /* 字号 */
  font-weight: 600;            /* 字重 */
  margin: 0 0 6px 0 !important; /* 清除默认margin */
}

.banner-text p {
  color: rgba(255, 255, 255, 0.9); /* 半透明白色 */
  margin: 0 !important;
  font-size: 13px;             /* 较小的字号 */
}

.banner-icon {
  font-size: 50px;             /* 图标大小 */
  color: rgba(255, 255, 255, 0.2); /* 很淡的白色 */
  animation: float 3s ease-in-out infinite; /* 应用浮动动画 */
}

/* 定义float关键帧动画：上下浮动效果 */
@keyframes float {
  0%, 100% { transform: translateY(0); }     /* 起始和结束位置 */
  50% { transform: translateY(-10px); }      /* 中间向上移动10px */
}

/* ====== 统计卡片网格 ====== */
.stats-grid {
  display: grid;                 /* Grid布局 */
  grid-template-columns: repeat(4, 1fr); /* 4列等宽 */
  gap: 12px;                     /* 卡片间距 */
  margin-bottom: 12px;           /* 底部间距 */
}

/* 单个统计卡片基础样式 */
.stat-card {
  background: white;             /* 白色背景 */
  padding: 16px 18px;            /* 内边距 */
  border-radius: 10px;           /* 圆角 */
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.03); /* 轻微阴影 */
  display: flex;                 /* Flex布局 */
  align-items: center;           /* 垂直居中 */
  gap: 12px;                     /* 子元素间距 */
  transition: all 0.3s ease;    /* 过渡动画 */
  position: relative;            /* 相对定位（用于伪元素） */
  overflow: hidden;              /* 隐藏溢出内容 */
}

/* 顶部彩色装饰条（使用::before伪元素） */
.stat-card::before {
  content: '';                   /* 必须设置content */
  position: absolute;            /* 绝对定位 */
  top: 0; left: 0; right: 0;    /* 贴合顶部 */
  height: 4px;                  /* 高度4px */
}

/* 不同类型卡片的顶部颜色 */
.stat-card.primary::before { background: linear-gradient(90deg, #667eea, #764ba2); }  /* 紫色 */
.stat-card.male::before { background: linear-gradient(90deg, #f093fb, #f5576c); }      /* 粉红 */
.stat-card.female::before { background: linear-gradient(90deg, #4facfe, #00f2fe); }    /* 蓝色 */
.stat-card.success::before { background: linear-gradient(90deg, #43e97b, #38f9d7); }    /* 绿色 */

/* 卡片悬停效果：上浮 + 加深阴影 */
.stat-card:hover {
  transform: translateY(-6px);  /* 向上移动6px */
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.12); /* 更深的阴影 */
}

/* 卡片内图标容器 */
.stat-icon {
  width: 44px;                   /* 正方形 */
  height: 44px;
  border-radius: 10px;           /* 圆角 */
  display: flex;                 /* Flex居中 */
  align-items: center;
  justify-content: center;
  font-size: 20px;              /* 图标大小 */
  flex-shrink: 0;               /* 不允许收缩 */
}

/* 不同类型图标的背景渐变色 */
.primary .stat-icon { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; }
.male .stat-icon { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); color: white; }
.female .stat-icon { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); color: white; }
.success .stat-icon { background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); color: white; }

/* 统计数字和信息文本 */
.stat-info h3 {
  font-size: 26px;              /* 大号数字 */
  font-weight: 700;             /* 粗体 */
  margin: 0 0 2px 0 !important;
  color: #262626;               /* 深灰色 */
}

.stat-info p {
  margin: 0 !important;
  color: #8c8c8c;               /* 浅灰色标签 */
  font-size: 12px;
  font-weight: 500;
}

/* 上升趋势箭头（绝对定位在右上角） */
.stat-trend {
  position: absolute;
  top: 16px; right: 16px;
  color: #52c41a;               /* 绿色 */
  font-size: 20px;
}

/* 百分比数字（绝对定位在右上角） */
.stat-percentage {
  position: absolute;
  top: 16px; right: 20px;
  font-size: 18px;
  font-weight: 600;
  color: #8c8c8c;
}

/* ====== 数据分析面板 ====== */
.data-analysis-card {
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.03);
  overflow: hidden;              /* 隐藏溢出（用于圆角裁剪） */
  margin-bottom: 12px;
}

/* 面板头部 */
.analysis-header {
  display: flex;
  justify-content: space-between; /* 两端对齐 */
  align-items: center;
  padding: 14px 20px;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%); /* 浅灰渐变 */
  border-bottom: 1px solid #e8e8e8; /* 底部分隔线 */
}

.analysis-header .icon {
  font-size: 24px;
  color: #667eea;               /* 紫色图标 */
}

.analysis-header h3 {
  margin: 0 !important;
  font-size: 18px;
  font-weight: 600;
  color: #262626;
}

/* 面板主体：左右分栏 */
.analysis-body {
  display: flex;                 /* Flex横向排列 */
  min-height: 280px;            /* 最小高度 */
}

/* 左右分区通用样式 */
.analysis-section {
  flex: 1;                      /* 各占一半宽度 */
  padding: 16px 20px;
}

/* 中间分隔线 */
.divider {
  width: 1px;                    /* 细线 */
  background: linear-gradient(180deg, transparent 0%, #e8e8e8 20%, #e8e8e8 80%, transparent 100%);
  flex-shrink: 0;               /* 不允许收缩 */
}

/* 小标题栏 */
.section-title-bar {
  display: flex;
  align-items: center;
  gap: 6px;                      /* 图标与文字间距 */
  margin-bottom: 14px;
  font-size: 14px;
  font-weight: 600;
  color: #595959;
}

.section-icon {
  font-size: 16px;
  color: #667eea;               /* 紫色图标 */
}

/* ====== 性别分布模块 ====== */
.gender-stats {
  display: flex;
  flex-direction: column;       /* 纵向排列 */
  gap: 10px;                    /* 项目间距 */
}

/* 单个性别统计项 */
.gender-stat-item {
  display: flex;
  align-items: center;           /* 垂直居中 */
  gap: 10px;
  padding: 8px 12px;
  border-radius: 8px;
  transition: all 0.3s;         /* 过渡动画 */
}

/* 悬停效果：浅灰背景 + 右移 */
.gender-stat-item:hover {
  background: #fafbfc;
  transform: translateX(4px);    /* 向右移动4px */
}

/* emoji头像容器 */
.stat-avatar {
  font-size: 22px;
  width: 34px; height: 34px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;           /* 浅灰背景 */
  border-radius: 8px;
}

/* 数字详情区域 */
.stat-details {
  flex: 1;                      /* 占据剩余空间 */
}

.stat-number {
  font-size: 18px;
  font-weight: 700;             /* 粗体数字 */
  color: #262626;
  line-height: 1.2;
}

.stat-label {
  font-size: 12px;
  color: #8c8c8c;
  margin-top: 1px;
}

/* 百分比徽章样式 */
.stat-percentage-badge {
  padding: 3px 10px;
  border-radius: 14px;           /* 胶囊形状 */
  font-size: 12px;
  font-weight: 700;
}

/* 男性徽章：粉红色系 */
.stat-percentage-badge.male {
  background: linear-gradient(135deg, #fff0f6, #ffebf0);
  color: #f5576c;
}

/* 女性徽章：蓝色系 */
.stat-percentage-badge.female {
  background: linear-gradient(135deg, #e6f7ff, #bae7ff);
  color: #1890ff;
}

/* 进度条外层容器（左缩进对齐） */
.gender-progress-wrapper {
  margin-left: 44px;             /* 与头像对齐 */
}

/* 进度条轨道 */
.gender-progress-bar {
  height: 5px;                   /* 细长型 */
  background: #f5f5f5;           /* 浅灰色背景 */
  border-radius: 2px;
  overflow: hidden;              /* 隐藏超出部分 */
}

/* 进度条填充层 */
.progress-fill {
  height: 100%;
  border-radius: 4px;
  transition: width 1s ease;     /* 宽度变化时的平滑过渡 */
}

.progress-fill.male {
  background: linear-gradient(90deg, #f093fb, #f5576c); /* 粉红渐变 */
}

.progress-fill.female {
  background: linear-gradient(90deg, #4facfe, #00f2fe); /* 蓝色渐变 */
}

/* 总计汇总卡片 */
.total-summary {
  margin-top: 14px;
  padding: 14px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); /* 紫色渐变 */
  border-radius: 8px;
  text-align: center;            /* 文字居中 */
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.18);
}

.summary-label {
  color: rgba(255, 255, 255, 0.85); /* 半透明白色 */
  font-size: 11px;
  margin-bottom: 3px;
}

.summary-value {
  font-size: 26px;              /* 大号数字 */
  font-weight: 700;
  color: white;
}

/* ====== 部门排行模块 ====== */
.dept-rankings {
  display: flex;
  flex-direction: column;       /* 纵向排列 */
  gap: 6px;                      /* 紧凑间距 */
}

/* 单个排名项 */
.dept-ranking-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 8px;
  border-radius: 6px;
  transition: all 0.3s;
}

/* 悬停效果 */
.dept-ranking-item:hover {
  background: #fafbfc;
  transform: translateX(4px);    /* 向右移动 */
}

/* 排名徽章 */
.rank-badge {
  width: 24px; height: 24px;    /* 正方形 */
  border-radius: 5px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 11px;
  flex-shrink: 0;               /* 不允许收缩 */
}

/* 第1名：金色 */
.rank-1 {
  background: linear-gradient(135deg, #ffd700, #ffed4e);
  color: #fff;
  text-shadow: 0 1px 2px rgba(0,0,0,0.2);
}

/* 第2名：银色 */
.rank-2 {
  background: linear-gradient(135deg, #e0e0e0, #f5f5f5);
  color: #666;
}

/* 第3名：铜色 */
.rank-3 {
  background: linear-gradient(135deg, #d4a574, #e8c49a);
  color: #fff;
}

/* 其他名次：灰色 */
.rank-badge:not(.rank-1):not(.rank-2):not(.rank-3) {
  background: #f0f0f0;
  color: #999;
}

/* 部门主要信息区 */
.dept-main {
  flex: 1;                      /* 占据剩余空间 */
  min-width: 0;                 /* 允许收缩（防止溢出） */
}

/* 部门名称行 */
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

/* 迷你进度条 */
.dept-mini-progress {
  height: 3px;                   /* 更细 */
  background: #f5f5f5;
  border-radius: 1px;
  overflow: hidden;
}

.mini-bar {
  height: 100%;
  border-radius: 3px;
  transition: width 0.8s ease;  /* 平滑过渡 */
}

/* 人数显示区 */
.dept-numbers {
  text-align: right;            /* 右对齐 */
  min-width: 36px;              /* 最小宽度 */
}

.dept-numbers strong {
  font-size: 14px;
  font-weight: 700;
  color: #262626;
  display: block;               /* 块级元素换行 */
  line-height: 1.2;
}

.dept-numbers small {
  font-size: 9px;               /* 很小的字号 */
  color: #8c8c8c;
}

/* ====== 快捷操作区域 ====== */
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
  color: #faad14;               /* 黄色闪电图标 */
  font-size: 20px;
}

/* 按钮组 */
.action-buttons {
  display: flex;
  gap: 10px;                    /* 按钮间距 */
  flex-wrap: wrap;              /* 允许换行 */
}

/* 按钮通用样式 */
.action-btn {
  height: 38px;                 /* 固定高度 */
  padding: 0 20px;              /* 内边距 */
  font-size: 13px;
  font-weight: 500;
  border-radius: 6px;
  transition: all 0.3s;
}

/* 第一个按钮（主按钮）：紫色渐变 */
.action-btn:first-child {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  color: white;
}

/* 主按钮悬停效果 */
.action-btn:first-child:hover {
  transform: translateY(-2px);  /* 上浮 */
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.35); /* 彩色阴影 */
}

/* 其他按钮悬停效果 */
.action-btn:not(:first-child):hover {
  transform: translateY(-2px);
  border-color: #667eea;        /* 紫色边框 */
  color: #667eea;               /* 紫色文字 */
}

/* 刷新按钮特殊悬停效果：绿色 */
.action-btn.refresh:hover {
  border-color: #52c41a;
  color: #52c41a;
}

/* ====== 响应式设计 ====== */

/* 大屏幕（<1400px）：统计卡片改为2列 */
@media (max-width: 1400px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

/* 中屏幕（<1200px）：分析面板改为上下排列 */
@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .analysis-body {
    flex-direction: column;     /* 改为纵向排列 */
  }

  .divider {
    width: 100%;                /* 分隔线变为横线 */
    height: 1px;
    background: linear-gradient(90deg, transparent 0%, #e8e8e8 50%, transparent 100%);
  }
}

/* 小屏幕（<768px）：移动端适配 */
@media (max-width: 768px) {
  .welcome-banner {
    padding: 16px;              /* 减少内边距 */
  }

  .banner-content {
    flex-direction: column;     /* 改为纵向排列 */
    text-align: center;         /* 文字居中 */
    gap: 12px;
  }

  .banner-text h1 {
    font-size: 18px;            /* 缩小字号 */
  }

  .banner-icon {
    font-size: 40px;            /* 缩小图标 */
  }

  .stats-grid {
    grid-template-columns: 1fr; /* 单列显示 */
  }

  .action-buttons {
    flex-direction: column;     /* 按钮纵向排列 */
  }

  .action-btn {
    width: 100%;                /* 按钮占满宽度 */
  }
}
</style>
