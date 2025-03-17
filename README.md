# 员工管理系统

一个简单的员工CRUD（增删改查）系统，包含前端和后端。

## 技术栈

### 前端
- Vue 3
- Vite
- Ant Design Vue
- Vue Router
- Pinia
- Axios

### 后端
- Spring Boot
- MySQL
- MyBatis-Plus
- Swagger

## 功能特性

- 员工列表展示（分页、搜索）
- 添加员工
- 编辑员工
- 删除员工
- 查看员工详情

## 项目结构

```
employee-system/
├── frontend/                # 前端项目
│   ├── src/
│   │   ├── api/             # API请求
│   │   ├── assets/          # 静态资源
│   │   ├── components/      # 公共组件
│   │   ├── router/          # 路由配置
│   │   ├── store/           # 状态管理
│   │   ├── views/           # 页面组件
│   │   ├── App.vue          # 根组件
│   │   └── main.js          # 入口文件
│   ├── index.html           # HTML模板
│   ├── package.json         # 依赖配置
│   └── vite.config.js       # Vite配置
│
└── backend/                 # 后端项目
    ├── src/
    │   ├── main/
    │   │   ├── java/com/example/employee/
    │   │   │   ├── common/          # 通用类
    │   │   │   ├── config/          # 配置类
    │   │   │   ├── controller/      # 控制器
    │   │   │   ├── entity/          # 实体类
    │   │   │   ├── mapper/          # Mapper接口
    │   │   │   ├── service/         # 服务接口和实现
    │   │   │   └── EmployeeApplication.java  # 启动类
    │   │   └── resources/
    │   │       ├── db/              # 数据库脚本
    │   │       └── application.yml  # 应用配置
    └── pom.xml                      # Maven配置
```

## 快速开始

### 前端

```bash
cd frontend
npm install
npm run dev
```

### 后端

1. 创建MySQL数据库并执行`src/main/resources/db/init.sql`脚本
2. 修改`application.yml`中的数据库连接信息
3. 运行Spring Boot应用

```bash
cd backend
mvn spring-boot:run
```

## API文档

启动后端服务后，访问 http://localhost:8080/swagger-ui/index.html 查看API文档。 