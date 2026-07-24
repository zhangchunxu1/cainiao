-- Production database initialization script.
-- Use this for a clean online database. It creates tables and one admin account only.
-- It does not insert demo employees, attendance records, announcements, reports, reimbursements, contracts, or salary slips.

CREATE DATABASE IF NOT EXISTS `employee_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `employee_db`;

CREATE TABLE IF NOT EXISTS `employee` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '员工ID',
  `name` varchar(50) NOT NULL COMMENT '姓名',
  `age` int(11) NOT NULL COMMENT '年龄',
  `gender` varchar(10) NOT NULL COMMENT '性别',
  `phone` varchar(20) NOT NULL COMMENT '电话',
  `email` varchar(100) NOT NULL COMMENT '邮箱',
  `department` varchar(50) NOT NULL COMMENT '部门',
  `position` varchar(50) NOT NULL COMMENT '职位',
  `manager_id` bigint(20) DEFAULT NULL COMMENT '直属上级ID',
  `hire_date` varchar(20) NOT NULL COMMENT '入职日期',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除(0-未删除，1-已删除)',
  PRIMARY KEY (`id`),
  INDEX `idx_name` (`name`),
  INDEX `idx_department` (`department`),
  INDEX `idx_position` (`position`),
  INDEX `idx_manager_id` (`manager_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工表';

CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `role` varchar(20) NOT NULL DEFAULT 'employee' COMMENT '角色（admin-管理员，employee-普通员工，manager-部门经理）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE IF NOT EXISTS `department` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '部门ID',
  `name` varchar(50) NOT NULL COMMENT '部门名称',
  `manager` varchar(50) DEFAULT NULL COMMENT '负责人',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `description` varchar(200) DEFAULT NULL COMMENT '描述',
  `employee_count` int(11) DEFAULT '0' COMMENT '员工人数',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态（0-正常，1-停用）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

CREATE TABLE IF NOT EXISTS `attendance` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '考勤ID',
  `employee_id` bigint(20) NOT NULL COMMENT '员工ID',
  `employee_name` varchar(50) NOT NULL COMMENT '员工姓名',
  `date` date NOT NULL COMMENT '日期',
  `check_in_time` time DEFAULT NULL COMMENT '签到时间',
  `check_out_time` time DEFAULT NULL COMMENT '签退时间',
  `work_hours` varchar(10) DEFAULT NULL COMMENT '工作时长（小时）',
  `status` varchar(20) DEFAULT NULL COMMENT '状态（正常、迟到、早退、缺卡）',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_employee_id` (`employee_id`),
  INDEX `idx_date` (`date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考勤表';

CREATE TABLE IF NOT EXISTS `announcement` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title` varchar(200) NOT NULL COMMENT '公告标题',
  `content` text NOT NULL COMMENT '公告内容',
  `publisher` varchar(50) DEFAULT NULL COMMENT '发布人',
  `type` varchar(20) DEFAULT '通知' COMMENT '类型（通知、公告、紧急）',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态（0-草稿，1-已发布）',
  `is_top` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否置顶',
  `view_count` int(11) DEFAULT '0' COMMENT '浏览次数',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

CREATE TABLE IF NOT EXISTS `leave_request` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '请假ID',
  `employee_id` bigint(20) NOT NULL COMMENT '员工ID',
  `employee_name` varchar(50) NOT NULL COMMENT '员工姓名',
  `department` varchar(50) DEFAULT NULL COMMENT '部门',
  `leave_type` varchar(20) NOT NULL COMMENT '请假类型（事假、病假、年假等）',
  `start_date` date NOT NULL COMMENT '开始日期',
  `end_date` date NOT NULL COMMENT '结束日期',
  `days` int(11) DEFAULT NULL COMMENT '请假天数',
  `reason` text NOT NULL COMMENT '请假原因',
  `status` varchar(20) DEFAULT '待审批' COMMENT '状态（待审批、已批准、已拒绝）',
  `approver` varchar(50) DEFAULT NULL COMMENT '审批人',
  `approval_comment` varchar(200) DEFAULT NULL COMMENT '审批意见',
  `approval_time` datetime DEFAULT NULL COMMENT '审批时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_employee_id` (`employee_id`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='请假表';

CREATE TABLE IF NOT EXISTS `daily_report` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日报ID',
  `employee_id` bigint(20) NOT NULL COMMENT '员工ID',
  `employee_name` varchar(50) NOT NULL COMMENT '员工姓名',
  `department` varchar(50) DEFAULT NULL COMMENT '部门',
  `report_date` date NOT NULL COMMENT '日报日期',
  `today_work` text NOT NULL COMMENT '今日工作内容',
  `tomorrow_work` text DEFAULT NULL COMMENT '明日工作计划',
  `issues` text DEFAULT NULL COMMENT '问题与困难',
  `status` varchar(20) DEFAULT '已提交' COMMENT '状态（已提交、已审核）',
  `reviewer` varchar(50) DEFAULT NULL COMMENT '审核人',
  `review_comment` varchar(500) DEFAULT NULL COMMENT '审核意见',
  `review_time` datetime DEFAULT NULL COMMENT '审核时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_employee_id` (`employee_id`),
  INDEX `idx_report_date` (`report_date`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='日报表';

CREATE TABLE IF NOT EXISTS `reimbursement` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '报销ID',
  `employee_id` bigint(20) COMMENT '员工ID',
  `employee_name` varchar(100) COMMENT '员工姓名',
  `department` varchar(100) COMMENT '部门',
  `reimbursement_no` varchar(50) COMMENT '报销单号',
  `type` varchar(50) COMMENT '报销类型',
  `amount` decimal(18,2) DEFAULT 0.00 COMMENT '报销金额',
  `reason` text COMMENT '报销事由',
  `status` varchar(50) DEFAULT '待审批' COMMENT '状态：待审批、待财务审批、已审批、已驳回',
  `apply_date` date COMMENT '申请日期',
  `manager_approve_date` date COMMENT '部门经理审批日期',
  `manager_approver` varchar(100) COMMENT '部门经理审批人',
  `manager_remark` varchar(500) COMMENT '部门经理审批意见',
  `finance_approve_date` date COMMENT '财务审批日期',
  `finance_approver` varchar(100) COMMENT '财务审批人',
  `finance_remark` varchar(500) COMMENT '财务审批意见',
  `remark` varchar(500) COMMENT '备注',
  `deleted` tinyint(1) DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报销表';

CREATE TABLE IF NOT EXISTS `contract` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '合同ID',
  `contract_no` varchar(64) NOT NULL COMMENT '合同编号',
  `contract_name` varchar(255) NOT NULL COMMENT '合同名称',
  `party_a` varchar(255) COMMENT '甲方',
  `party_b` varchar(255) COMMENT '乙方',
  `sign_date` date COMMENT '签订日期',
  `start_date` date COMMENT '开始日期',
  `end_date` date COMMENT '结束日期',
  `contract_amount` decimal(18,2) COMMENT '合同金额',
  `currency` varchar(10) DEFAULT 'CNY' COMMENT '货币类型',
  `contract_type` varchar(64) COMMENT '合同类型',
  `status` varchar(32) DEFAULT '生效中' COMMENT '合同状态',
  `employee_id` bigint(20) COMMENT '关联员工ID',
  `employee_name` varchar(100) COMMENT '关联员工姓名',
  `department` varchar(100) COMMENT '关联部门',
  `contract_content` text COMMENT '合同内容（HTML格式）',
  `attachments` varchar(500) COMMENT '附件路径',
  `remark` varchar(500) COMMENT '备注',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_contract_no` (`contract_no`),
  KEY `idx_contract_name` (`contract_name`),
  KEY `idx_employee_id` (`employee_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合同表';

CREATE TABLE IF NOT EXISTS `salary_slip` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '工资条ID',
  `employee_id` bigint(20) COMMENT '员工ID',
  `employee_name` varchar(100) COMMENT '员工姓名',
  `department` varchar(100) COMMENT '部门',
  `pay_month` date COMMENT '发放月份',
  `basic_salary` decimal(18,2) DEFAULT 0.00 COMMENT '基本工资',
  `performance_bonus` decimal(18,2) DEFAULT 0.00 COMMENT '绩效奖金',
  `overtime_pay` decimal(18,2) DEFAULT 0.00 COMMENT '加班工资',
  `allowance` decimal(18,2) DEFAULT 0.00 COMMENT '各项津贴',
  `total_income` decimal(18,2) DEFAULT 0.00 COMMENT '收入合计',
  `social_insurance` decimal(18,2) DEFAULT 0.00 COMMENT '社会保险',
  `housing_fund` decimal(18,2) DEFAULT 0.00 COMMENT '住房公积金',
  `tax` decimal(18,2) DEFAULT 0.00 COMMENT '个人所得税',
  `total_deduction` decimal(18,2) DEFAULT 0.00 COMMENT '扣款合计',
  `net_salary` decimal(18,2) DEFAULT 0.00 COMMENT '实发工资',
  `status` varchar(32) DEFAULT '未发放' COMMENT '状态',
  `remark` varchar(500) COMMENT '备注',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_employee_id` (`employee_id`),
  KEY `idx_pay_month` (`pay_month`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工资条表';

-- Initial administrator account. Default password is admin123.
-- Change it immediately after first login, or set APP_ADMIN_PASSWORD before backend startup.
INSERT IGNORE INTO `user` (`username`, `password`, `real_name`, `role`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 'admin');
