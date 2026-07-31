# 全页面多选批量删除功能实现计划

## Context

当前系统的所有列表页面只有单条删除功能，用户需要逐个操作删除记录，效率低下。需要为前后端所有列表页面添加多选批量删除功能，提升操作效率。

## 后端改造（10 个 Controller）

### 通用模式

每个 Controller 新增 `DELETE /batch` 端点，接收 `@RequestBody List<Long> ids`，返回 `Result<Integer>`（删除条数）。**必须将 `/batch` 路由写在 `/{id}` 之前**，避免 Spring MVC 路径冲突。

### 分类实现

**A 类：简单硬删除** — 直接 `removeByIds(ids)`
- [AnnouncementController.java](file:///d:/xiangmu/cainiao/employee-system/backend/src/main/java/com/example/employee/controller/AnnouncementController.java)
- [DepartmentController.java](file:///d:/xiangmu/cainiao/employee-system/backend/src/main/java/com/example/employee/controller/DepartmentController.java)
- [ContractController.java](file:///d:/xiangmu/cainiao/employee-system/backend/src/main/java/com/example/employee/controller/ContractController.java)

**B 类：硬删除 + 管理员权限** — 先校验 `isAdmin`，再 `removeByIds`
- [AttendanceController.java](file:///d:/xiangmu/cainiao/employee-system/backend/src/main/java/com/example/employee/controller/AttendanceController.java)
- [SalarySlipController.java](file:///d:/xiangmu/cainiao/employee-system/backend/src/main/java/com/example/employee/controller/SalarySlipController.java)

**C 类：硬删除 + 权限 + 状态校验** — 逐条校验权限和状态，跳过不满足条件的记录
- [LeaveRequestController.java](file:///d:/xiangmu/cainiao/employee-system/backend/src/main/java/com/example/employee/controller/LeaveRequestController.java) — 非管理员只能删"待审批"
- [DailyReportController.java](file:///d:/xiangmu/cainiao/employee-system/backend/src/main/java/com/example/employee/controller/DailyReportController.java) — 非管理员只能删"已提交"
- [UserController.java](file:///d:/xiangmu/cainiao/employee-system/backend/src/main/java/com/example/employee/controller/UserController.java) — 不能删 admin 账号

**D 类：软删除 + 权限 + 状态** — 逐条设置 `deleted = 1`
- [EmployeeController.java](file:///d:/xiangmu/cainiao/employee-system/backend/src/main/java/com/example/employee/controller/EmployeeController.java) — `setDeleted(1)` + `updateById`
- [ReimbursementController.java](file:///d:/xiangmu/cainiao/employee-system/backend/src/main/java/com/example/employee/controller/ReimbursementController.java) — 软删除 + 权限 + 状态校验

## 前端改造

### API 层（10 个文件）

每个 API 文件新增 `batchDelete` 函数，使用 `api.delete('/xxx/batch', { data: ids })` 传请求体：

- [employee.js](file:///d:/xiangmu/cainiao/employee-system/frontend/src/api/employee.js) — `export const batchDeleteEmployees = (ids) => api.delete('/employees/batch', { data: ids })`
- [announcement.js](file:///d:/xiangmu/cainiao/employee-system/frontend/src/api/announcement.js) — 在对象中添加 `batchDeleteAnnouncements`
- [attendance.js](file:///d:/xiangmu/cainiao/employee-system/frontend/src/api/attendance.js) — 添加 `batchDeleteAttendance`
- [department.js](file:///d:/xiangmu/cainiao/employee-system/frontend/src/api/department.js) — 添加 `batchDeleteDepartments`
- [contract.js](file:///d:/xiangmu/cainiao/employee-system/frontend/src/api/contract.js) — 添加 `batchDeleteContracts`
- [leave.js](file:///d:/xiangmu/cainiao/employee-system/frontend/src/api/leave.js) — 添加 `batchDeleteLeaveRequests`
- [reimbursement.js](file:///d:/xiangmu/cainiao/employee-system/frontend/src/api/reimbursement.js) — 添加 `batchDeleteReimbursements`
- [dailyReport.js](file:///d:/xiangmu/cainiao/employee-system/frontend/src/api/dailyReport.js) — 添加 `batchDeleteDailyReports`
- [salary.js](file:///d:/xiangmu/cainiao/employee-system/frontend/src/api/salary.js) — 添加 `batchDeleteSalarySlips`
- [user.js](file:///d:/xiangmu/cainiao/employee-system/frontend/src/api/user.js) — 添加 `batchDeleteUsers`

### Store 层（2 个文件）

- [employee.js](file:///d:/xiangmu/cainiao/employee-system/frontend/src/store/employee.js) — 新增 `batchRemoveEmployees` action
- [user.js](file:///d:/xiangmu/cainiao/employee-system/frontend/src/store/user.js) — 新增 `batchRemoveUsers` action

### 页面层（10 个文件）

**表格页面通用改造模式**（9 个页面统一）：

1. 新增 `selectedRowKeys` 和 `batchDeleteLoading` 响应式变量
2. 定义 `rowSelection` 计算属性
3. `a-table` 添加 `:rowSelection="rowSelection"`
4. header-right 区域添加"批量删除"按钮（仅选中时显示）
5. 新增 `handleBatchDelete` 方法（Modal.confirm 确认）
6. 列表刷新后清空 `selectedRowKeys`

**9 个表格页面**：
- [EmployeeList.vue](file:///d:/xiangmu/cainiao/employee-system/frontend/src/views/employees/EmployeeList.vue) — 通过 store 调用
- [AttendanceList.vue](file:///d:/xiangmu/cainiao/employee-system/frontend/src/views/attendance/AttendanceList.vue) — 仅管理员
- [DepartmentList.vue](file:///d:/xiangmu/cainiao/employee-system/frontend/src/views/departments/DepartmentList.vue)
- [ContractList.vue](file:///d:/xiangmu/cainiao/employee-system/frontend/src/views/contracts/ContractList.vue)
- [LeaveList.vue](file:///d:/xiangmu/cainiao/employee-system/frontend/src/views/leaves/LeaveList.vue)
- [ReimbursementList.vue](file:///d:/xiangmu/cainiao/employee-system/frontend/src/views/reimbursement/ReimbursementList.vue)
- [DailyReportList.vue](file:///d:/xiangmu/cainiao/employee-system/frontend/src/views/dailyReports/DailyReportList.vue)
- [SalarySlipList.vue](file:///d:/xiangmu/cainiao/employee-system/frontend/src/views/salary/SalarySlipList.vue)
- [UserList.vue](file:///d:/xiangmu/cainiao/employee-system/frontend/src/views/users/UserList.vue) — 通过 store 调用

**1 个卡片页面特殊处理**：
- [AnnouncementList.vue](file:///d:/xiangmu/cainiao/employee-system/frontend/src/views/announcements/AnnouncementList.vue) — 卡片加 checkbox + 批量操作工具栏

## 注意事项

1. **Spring MVC 路由顺序**：`/batch` 必须在 `/{id}` 之前声明
2. **axios delete 请求体**：必须用 `api.delete('/url', { data: ids })` 而非 `api.delete('/url', ids)`
3. **分页边界**：批量删除后当前页可能无数据，需回退到上一页
4. **软删除**：Employee 和 Reimbursement 使用 `setDeleted(1)` + `updateById`，非 `removeByIds`

## 验证

1. 启动后端，测试 `DELETE /api/employees/batch` 等接口是否正常
2. 启动前端，在各列表页勾选多条记录，点击批量删除，确认弹出确认框
3. 确认删除后列表正确刷新，选中项清空
4. 测试权限：非管理员用户无法批量删除受限记录
5. 测试状态校验：非管理员只能批量删除"待审批"的请假/报销
