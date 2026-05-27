# 员工管理系统 (Employee Management System)

一个功能完整的员工管理系统，采用前后端分离架构，包含员工管理、部门管理、考勤管理、公告通知和请假管理等核心业务模块。

## 技术栈

### 前端
- **Vue 3** - 渐进式JavaScript框架
- **Vite** - 下一代前端构建工具
- **Ant Design Vue** - 企业级UI组件库
- **Vue Router** - 路由管理
- **Pinia** - 状态管理
- **Axios** - HTTP客户端

### 后端
- **Spring Boot 2.7.0** - Java应用框架
- **MySQL 8.0** - 关系型数据库
- **MyBatis-Plus 3.5.2** - ORM框架
- **Spring Security** - 安全认证框架
- **JWT (jjwt)** - Token认证
- **Swagger 3.0** - API文档生成
- **Lombok** - 简化Java代码
- **Apache Commons Lang3** - 工具类库

## 功能特性

### 核心模块

#### 1. 员工管理
- ✅ 员工信息CRUD（增删改查）
- ✅ 分页查询与关键字搜索
- ✅ 按部门、职位筛选
- ✅ 员工详情查看

#### 2. 部门管理
- ✅ 部门信息维护
- ✅ 部门负责人管理
- ✅ 部门员工统计
- ✅ 部门启用/停用

#### 3. 考勤管理
- ✅ 员工签到/签退
- ✅ 考勤记录查询
- ✅ 工作时长统计
- ✅ 考勤状态标记（正常/迟到/早退/缺卡）

#### 4. 公告通知
- ✅ 公告发布与管理
- ✅ 公告分类（通知/公告/紧急）
- ✅ 公告置顶功能
- ✅ 浏览次数统计
- ✅ 公告草稿/发布状态

#### 5. 请假管理
- ✅ 在线提交请假申请
- ✅ 多种请假类型（事假/病假/年假等）
- ✅ 请假审批流程（批准/拒绝）
- ✅ 审批意见记录
- ✅ 请假历史查询

#### 6. 系统安全
- ✅ JWT Token认证
- ✅ 用户登录/登出
- ✅ 接口权限控制
- ✅ 密码加密存储（BCrypt）

## 项目结构

```
employee-system/
├── frontend/                          # 前端项目 (Vue 3)
│   ├── src/
│   │   ├── api/                       # API接口定义
│   │   │   ├── auth.js               # 认证接口
│   │   │   ├── employee.js           # 员工接口
│   │   │   ├── department.js         # 部门接口
│   │   │   ├── attendance.js         # 考勤接口
│   │   │   ├── announcement.js       # 公告接口
│   │   │   └── leave.js              # 请假接口
│   │   ├── router/                   # 路由配置
│   │   │   └── index.js              # 路由定义+权限守卫
│   │   ├── store/                    # Pinia状态管理
│   │   │   ├── auth.js              # 认证状态
│   │   │   └── employee.js          # 员工数据状态
│   │   ├── views/                    # 页面组件
│   │   │   ├── Login.vue            # 登录页面
│   │   │   ├── Layout.vue           # 主布局（侧边栏）
│   │   │   ├── Dashboard.vue        # 仪表盘
│   │   │   ├── employees/           # 员工模块
│   │   │   │   ├── EmployeeList.vue # 员工列表
│   │   │   │   └── EmployeeForm.vue # 员工表单
│   │   │   ├── departments/         # 部门模块
│   │   │   │   └── DepartmentList.vue
│   │   │   ├── attendance/          # 考勤模块
│   │   │   │   └── AttendanceList.vue
│   │   │   ├── announcements/       # 公告模块
│   │   │   │   └── AnnouncementList.vue
│   │   │   ├── leaves/              # 请假模块
│   │   │   │   └── LeaveList.vue
│   │   │   └── NotFound.vue         # 404页面
│   │   ├── App.vue                  # 根组件
│   │   └── main.js                  # 入口文件
│   ├── index.html                   # HTML模板
│   ├── package.json                 # NPM依赖配置
│   ├── vite.config.js              # Vite配置（代理设置）
│   └── yarn.lock                   # Yarn锁定文件
│
├── backend/                          # 后端项目 (Spring Boot)
│   ├── src/main/java/com/example/employee/
│   │   ├── common/                  # 通用响应类
│   │   │   ├── Result.java         # 统一返回结果
│   │   │   └── ResultCode.java     # 响应码枚举
│   │   ├── config/                  # 配置类
│   │   │   ├── SecurityConfig.java # Spring Security配置
│   │   │   ├── SwaggerConfig.java  # Swagger文档配置
│   │   │   ├── MybatisPlusConfig.java # MyBatis-Plus配置
│   │   │   ├── JwtAuthenticationFilter.java # JWT过滤器
│   │   │   ├── GlobalExceptionHandler.java # 全局异常处理
│   │   │   └── DataInitializer.java # 初始化数据
│   │   ├── controller/             # 控制器层
│   │   │   ├── AuthController.java      # 认证控制器
│   │   │   ├── EmployeeController.java  # 员工控制器
│   │   │   ├── DepartmentController.java# 部门控制器
│   │   │   ├── AttendanceController.java# 考勤控制器
│   │   │   ├── AnnouncementController.java # 公告控制器
│   │   │   └── LeaveRequestController.java # 请假控制器
│   │   ├── entity/                 # 实体类
│   │   │   ├── Employee.java       # 员工实体
│   │   │   ├── User.java           # 用户实体
│   │   │   ├── Department.java     # 部门实体
│   │   │   ├── Attendance.java     # 考勤实体
│   │   │   ├── Announcement.java   # 公告实体
│   │   │   └── LeaveRequest.java   # 请假实体
│   │   ├── mapper/                 # Mapper接口层
│   │   │   ├── EmployeeMapper.java
│   │   │   ├── UserMapper.java
│   │   │   ├── DepartmentMapper.java
│   │   │   ├── AttendanceMapper.java
│   │   │   ├── AnnouncementMapper.java
│   │   │   └── LeaveRequestMapper.java
│   │   ├── service/                # 服务层接口
│   │   │   ├── AuthService.java
│   │   │   ├── EmployeeService.java
│   │   │   ├── DepartmentService.java
│   │   │   ├── AttendanceService.java
│   │   │   ├── AnnouncementService.java
│   │   │   └── LeaveRequestService.java
│   │   ├── service/impl/           # 服务层实现
│   │   │   ├── AuthServiceImpl.java
│   │   │   ├── EmployeeServiceImpl.java
│   │   │   ├── DepartmentServiceImpl.java
│   │   │   ├── AttendanceServiceImpl.java
│   │   │   ├── AnnouncementServiceImpl.java
│   │   │   └── LeaveRequestServiceImpl.java
│   │   ├── util/                   # 工具类
│   │   │   └── JwtUtil.java        # JWT工具类
│   │   └── EmployeeApplication.java # 启动类
│   ├── src/main/resources/
│   │   ├── db/
│   │   │   └── init.sql           # 数据库初始化脚本
│   │   └── application.yml        # 应用配置文件
│   └── pom.xml                     # Maven依赖配置
│
└── .gitignore                      # Git忽略规则
```

## 快速开始

### 环境要求

- **Node.js** >= 14.0.0
- **Java** >= 8 (推荐 JDK 11 或 17)
- **Maven** >= 3.6.0
- **MySQL** >= 5.7 (推荐 8.0)

### 1️⃣ 数据库配置

#### 创建数据库并导入初始数据

```bash
mysql -u root -p < backend/src/main/resources/db/init.sql
```

或手动在MySQL中执行 `init.sql` 脚本。

#### 修改数据库连接配置

编辑 [backend/src/main/resources/application.yml](backend/src/main/resources/application.yml)：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/employee_db?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf-8
    username: root
    password: your_password  # 修改为你的密码
```

### 2️⃣ 启动后端服务

```bash
cd backend

# 方式1: 使用Maven运行
mvn spring-boot:run

# 方式2: 使用IDEA
# 导入项目 → 运行 EmployeeApplication.java
```

后端启动成功后访问：
- **API文档**: http://localhost:8080/swagger-ui/index.html
- **默认账号**: admin / admin123

### 3️⃣ 启动前端服务

```bash
cd frontend

# 安装依赖
npm install
# 或使用 yarn
yarn install

# 启动开发服务器
npm run dev
# 或
yarn dev
```

前端启动成功后访问：http://localhost:3000

---

## API接口文档

### 认证接口 `/api/auth`

| 方法 | 路径 | 说明 | 参数 |
|------|------|------|------|
| POST | `/api/auth/login` | 用户登录 | `{username, password}` |
| POST | `/api/auth/register` | 用户注册 | `{username, password, realName}` |
| GET | `/api/auth/userinfo` | 获取当前用户信息 | Header: Authorization |

### 员工接口 `/api/employees`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/employees` | 获取员工列表（分页、搜索） |
| GET | `/api/employees/{id}` | 获取员工详情 |
| POST | `/api/employees` | 添加员工 |
| PUT | `/api/employees/{id}` | 更新员工信息 |
| DELETE | `/api/employees/{id}` | 删除员工 |

**请求参数**: `page`(页码), `pageSize`(每页条数), `keyword`(搜索关键字)

### 部门接口 `/api/departments`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/departments` | 获取部门列表 |
| GET | `/api/departments/{id}` | 获取部门详情 |
| POST | `/api/departments` | 添加部门 |
| PUT | `/api/departments/{id}` | 更新部门 |
| DELETE | `/api/departments/{id}` | 删除部门 |

### 考勤接口 `/api/attendance`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/attendance` | 获取考勤列表 |
| GET | `/api/attendance/{id}` | 获取考勤详情 |
| POST | `/api/attendance/checkin` | **签到** |
| POST | `/api/attendance/checkout` | **签退** |
| PUT | `/api/attendance/{id}` | 更新考勤记录 |
| DELETE | `/api/attendance/{id}` | 删除考勤记录 |

### 公告接口 `/api/announcements`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/announcements` | 获取公告列表（置顶优先） |
| GET | `/api/announcements/{id}` | 获取公告详情 |
| POST | `/api/announcements` | 发布公告 |
| PUT | `/api/announcements/{id}` | 更新公告 |
| DELETE | `/api/announcements/{id}` | 删除公告 |

### 请假接口 `/api/leaves`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/leaves` | 获取请假列表 |
| GET | `/api/leaves/{id}` | 获取请假详情 |
| POST | `/api/leaves` | 提交请假申请 |
| PUT | `/api/leaves/{id}/approve` | **批准请假** |
| PUT | `/api/leaves/{id}/reject` | **拒绝请假** |
| DELETE | `/api/leaves/{id}` | 删除请假记录 |

---

## 数据库设计

### 表结构总览

| 表名 | 说明 | 记录数 |
|------|------|--------|
| `user` | 用户表（管理员） | 1条 |
| `employee` | 员工信息表 | 10条测试数据 |
| `department` | 部门表 | 5个部门 |
| `attendance` | 考勤记录表 | 3条测试数据 |
| `announcement` | 公告表 | 3条测试数据 |
| `leave_request` | 请假申请表 | 3条测试数据 |

### ER关系

```
user (1) ──→ (N) employee (N) ──→ (1) department
                │
                ├── (N) attendance
                └── (N) leave_request

department (1) ──→ (N) employee
```

### 默认测试账号

- **用户名**: admin
- **密码**: admin123
- **角色**: 系统管理员

---

## 开发指南

### 代码规范

- 后端遵循RESTful API设计规范
- 统一使用 `Result<T>` 包装返回结果
- 所有接口需要JWT Token认证（除登录/注册外）
- 使用Swagger注解自动生成API文档
- 前端使用Composition API编写Vue组件

### 分页查询格式

所有列表接口支持分页：

**请求参数:**
```
?page=1&pageSize=10&keyword=搜索关键字
```

**响应格式:**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [...],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```

### 错误处理

统一错误响应格式：
```json
{
  "code": 500,
  "message": "错误描述",
  "data": null
}
```

常见错误码：
- `200`: 成功
- `401`: 未认证（Token无效或过期）
- `403`: 无权限
- `404`: 资源不存在
- `500`: 服务器内部错误

---

## 项目特色

✨ **完整的业务闭环** - 从员工入职到日常管理的全流程  
🎨 **现代化UI** - 基于Ant Design Vue的企业级界面  
🔐 **安全可靠** - JWT + Spring Security双重保障  
📱 **响应式布局** - 支持多种屏幕尺寸  
⚡ **高性能** - MyBatis-Plus优化数据库操作  
📖 **完善文档** - Swagger自动生成API文档  
🧪 **测试数据完备** - 开箱即用的演示数据  

---

## 常见问题

### Q: 前端请求后端接口报跨域错误？
A: 已在 `vite.config.js` 中配置代理，确保后端运行在8080端口。

### Q: Maven依赖下载慢？
A: 在Maven的 `settings.xml` 中配置阿里云镜像源。

### Q: 数据库连接失败？
A: 检查MySQL服务是否启动，确认 `application.yml` 中的连接信息正确。

### Q: 如何重置测试数据？
A: 重新执行 `init.sql` 脚本即可（会清空现有数据）。

---

## 版本历史

### v1.1.0 (2026-05-27)
- ✨ 新增部门管理模块
- ✨ 新增考勤管理模块（签到/签退）
- ✨ 新增公告通知模块
- ✨ 新增请假管理模块（含审批流程）
- 🔧 修复前后端接口路径匹配问题
- 📝 完善API文档和项目文档

### v1.0.0 (初始版本)
- ✨ 基础员工CRUD功能
- ✨ 用户认证系统
- ✨ 前后端分离架构搭建

---

## License

MIT License

## 作者

Employee Management System Development Team
