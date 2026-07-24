# 员工管理系统小程序

基于 **uni-app + Vue3 + TypeScript** 开发的员工管理系统小程序，与后端共用同一套 API 接口。
**支持 HBuilderX 直接运行，也支持 CLI 命令行方式运行。**

## 技术栈

- **框架**: uni-app (Vue 3)
- **语言**: TypeScript
- **样式**: SCSS
- **状态管理**: Pinia
- **构建工具**: HBuilderX / Vite (CLI)

## 项目结构

```
miniprogram/
├── api/                    # API 接口层
│   ├── auth.ts             # 认证接口
│   ├── attendance.ts       # 考勤接口
│   ├── leave.ts            # 请假接口
│   ├── reimbursement.ts    # 报销接口
│   ├── dailyReport.ts      # 日报接口
│   ├── announcement.ts     # 公告接口
│   ├── salary.ts           # 工资条接口
│   └── employee.ts         # 员工接口
├── pages/                  # 页面
│   ├── home/               # 首页
│   ├── attendance/         # 考勤
│   ├── apply/              # 申请
│   ├── mine/               # 我的
│   ├── login/              # 登录
│   ├── leave/              # 请假（申请/详情）
│   ├── reimbursement/      # 报销（申请/详情）
│   ├── dailyReport/        # 日报（提交/详情）
│   ├── announcement/       # 公告（详情）
│   └── salary/             # 工资条（详情）
├── store/                  # 状态管理
│   └── auth.ts             # 认证状态
├── styles/                 # 样式文件
│   ├── theme.scss          # 主题变量
│   └── variables.scss      # 通用变量与mixin
├── types/                  # 类型定义
│   └── index.ts
├── utils/                  # 工具函数
│   ├── request.ts          # 请求封装
│   └── index.ts            # 通用工具
├── static/                 # 静态资源
├── App.vue                 # 根组件
├── main.ts                 # 入口文件
├── app.scss                # 全局样式
├── uni.scss                # uni-app 全局 SCSS 变量（HBuilderX 自动注入）
├── pages.json              # 页面配置
├── manifest.json           # 应用配置
└── README.md
```

## 功能模块

### 首页
- 用户信息展示
- 今日考勤打卡（签到/签退）
- 快捷入口（请假、报销、日报、考勤）
- 公告通知列表
- 待审批事项

### 考勤
- 上班签到/下班签退
- 实时时钟显示
- 月度考勤统计
- 日历视图展示考勤状态

### 申请
- 请假申请与记录
- 报销申请与记录
- 日报提交与记录
- Tab 切换查看不同类型申请

### 我的
- 个人信息展示
- 常用功能入口
- 设置菜单
- 退出登录

## HBuilderX 运行方式（推荐）

### 步骤一：导入项目
1. 打开 **HBuilderX**
2. 菜单：`文件` → `打开目录`
3. 选择本项目目录：`miniprogram`
4. HBuilderX 会自动识别为 **uni-app (Vue3)** 项目

### 步骤二：运行到 H5
1. 菜单：`运行` → `运行到浏览器` → 选择浏览器（如 Chrome）
2. 首次运行会自动编译，然后打开浏览器预览

### 步骤三：运行到微信小程序
1. 先安装并打开 **微信开发者工具**
2. 在微信开发者工具中：`设置` → `安全设置` → 开启 **服务端口**
3. 回到 HBuilderX：`运行` → `运行到小程序模拟器` → `微信开发者工具`
4. 首次运行需要配置微信开发者工具路径
5. 编译完成后会自动在微信开发者工具中打开

### 步骤四：运行到手机
1. 手机连接电脑（开启 USB 调试）
2. 菜单：`运行` → `运行到手机或模拟器` → 选择对应平台

## CLI 命令行运行方式

### 安装依赖
```bash
npm install
```

### H5 开发
```bash
npm run dev:h5
```

### 微信小程序开发
```bash
npm run dev:mp-weixin
```
编译完成后，用微信开发者工具导入 `dist/dev/mp-weixin` 目录。

### 构建 H5
```bash
npm run build:h5
```

### 构建微信小程序
```bash
npm run build:mp-weixin
```

## 接口对接

小程序与后端共用同一套 API 接口。

### 开发环境
- 通过 HBuilderX 内置代理或 vite 代理
- 请求路径前缀：`/api`
- 代理目标：`http://localhost:8080`

### 生产环境
- 在 `manifest.json` 或代码中配置实际 API 地址
- 小程序需在微信公众平台配置 **服务器域名白名单**

### 认证流程
1. 用户输入用户名密码登录
2. 后端返回 Token 和用户信息
3. Token 存储在 `uni.setStorageSync('token', token)`
4. 后续请求在 Header 中携带 `Authorization: Bearer {token}`
5. 401 状态码自动清除 Token 并跳转登录页

## 默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |
| 部门领导 | manager | manager123 |
| 普通员工 | employee | employee123 |

## 注意事项

### HBuilderX 运行注意
1. 项目根目录必须包含 `manifest.json`、`pages.json`、`App.vue`、`main.ts`
2. 全局 SCSS 变量定义在 `uni.scss` 中，HBuilderX 会自动注入每个页面
3. Vue3 项目需 HBuilderX 3.0+ 版本支持
4. TypeScript 支持需安装 HBuilderX 相关插件

### 小程序发布注意
1. 在 `manifest.json` 中配置微信小程序 AppID
2. 真机预览/调试需配置服务器域名
3. 接口请求必须使用 HTTPS（开发阶段可在开发者工具中关闭校验）

### 后端接口
- 后端项目位于：`../backend/`
- 默认端口：`8080`
- 接口文档：启动后端后访问 `http://localhost:8080/swagger-ui.html`
