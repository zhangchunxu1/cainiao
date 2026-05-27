-- 创建数据库
CREATE DATABASE IF NOT EXISTS employee_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE employee_db;

-- 创建员工表
CREATE TABLE IF NOT EXISTS `employee` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '员工ID',
  `name` varchar(50) NOT NULL COMMENT '姓名',
  `age` int(11) NOT NULL COMMENT '年龄',
  `gender` varchar(10) NOT NULL COMMENT '性别',
  `phone` varchar(20) NOT NULL COMMENT '电话',
  `email` varchar(100) NOT NULL COMMENT '邮箱',
  `department` varchar(50) NOT NULL COMMENT '部门',
  `position` varchar(50) NOT NULL COMMENT '职位',
  `hire_date` varchar(20) NOT NULL COMMENT '入职日期',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除(0-未删除，1-已删除)',
  PRIMARY KEY (`id`),
  INDEX `idx_name` (`name`),
  INDEX `idx_department` (`department`),
  INDEX `idx_position` (`position`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工表';

-- 插入测试数据
INSERT INTO `employee` (`name`, `age`, `gender`, `phone`, `email`, `department`, `position`, `hire_date`, `deleted`) VALUES
('张三', 28, '男', '13800138001', 'zhangsan@example.com', '技术部', '前端开发工程师', '2020-01-15', 0),
('李四', 32, '男', '13800138002', 'lisi@example.com', '技术部', '后端开发工程师', '2019-05-20', 0),
('王五', 35, '男', '13800138003', 'wangwu@example.com', '技术部', '技术经理', '2018-03-10', 0),
('赵六', 26, '女', '13800138004', 'zhaoliu@example.com', '市场部', '市场专员', '2021-07-01', 0),
('钱七', 29, '女', '13800138005', 'qianqi@example.com', '人事部', '人事专员', '2020-09-15', 0),
('孙八', 40, '男', '13800138006', 'sunba@example.com', '财务部', '财务经理', '2015-11-01', 0),
('周九', 27, '女', '13800138007', 'zhoujiu@example.com', '销售部', '销售代表', '2022-02-18', 0),
('吴十', 33, '男', '13800138008', 'wushi@example.com', '销售部', '销售经理', '2019-08-05', 0),
('郑十一', 31, '女', '13800138009', 'zhengshiyi@example.com', '市场部', '市场经理', '2020-04-22', 0),
('王十二', 36, '男', '13800138010', 'wangshier@example.com', '技术部', '架构师', '2017-06-30', 0);

-- 创建用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 插入测试用户（密码是 admin123，使用BCrypt加密）
INSERT INTO `user` (`username`, `password`, `real_name`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员');

-- 创建部门表
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

-- 插入部门测试数据
INSERT INTO `department` (`name`, `manager`, `phone`, `description`, `employee_count`) VALUES
('技术部', '王五', '13800138003', '负责公司技术研发和产品开发工作', 4),
('市场部', '郑十一', '13800138009', '负责市场推广和品牌建设', 2),
('人事部', '钱七', '13800138005', '负责人力资源管理和员工关系维护', 1),
('财务部', '孙八', '13800138006', '负责财务管理和会计核算工作', 1),
('销售部', '吴十', '13800138008', '负责产品销售和客户拓展', 2);

-- 创建考勤表
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

-- 插入考勤测试数据
INSERT INTO `attendance` (`employee_id`, `employee_name`, `date`, `check_in_time`, `check_out_time`, `work_hours`, `status`) VALUES
(1, '张三', CURDATE(), '08:55:00', '18:05:00', '9.2', '正常'),
(2, '李四', CURDATE(), '09:05:00', '18:00:00', '8.9', '迟到'),
(3, '王五', CURDATE(), '08:45:00', '18:30:00', '9.8', '正常');

-- 创建公告表
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

-- 插入公告测试数据
INSERT INTO `announcement` (`title`, `content`, `publisher`, `type`, `status`, `is_top`, `view_count`) VALUES
('关于2024年春节放假安排的通知', '各位同事：根据国家法定节假日安排，结合公司实际情况，现将2024年春节放假安排通知如下：放假时间为2024年2月10日至2月17日，共8天。2月18日（星期日）正常上班。祝大家新春快乐！', '人事部', '通知', 1, 1, 156),
('年度优秀员工评选活动启动', '为表彰先进、树立典型，公司决定开展2023年度优秀员工评选活动。评选标准包括：工作业绩突出、团队协作能力强、创新能力显著等。请各部门于本月底前完成推荐工作。', '行政部', '公告', 1, 0, 89),
('系统升级维护通知', '为了提升系统性能和用户体验，系统将于本周六（1月27日）22:00至周日（次日）06:00进行升级维护。届时系统将暂停服务，请提前做好相关工作安排。给您带来的不便敬请谅解！', '技术部', '紧急', 1, 1, 234);

-- 创建请假表
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

-- 插入请假测试数据
INSERT INTO `leave_request` (`employee_id`, `employee_name`, `department`, `leave_type`, `start_date`, `end_date`, `days`, `reason`, `status`, `approver`, `approval_comment`, `approval_time`) VALUES
(1, '张三', '技术部', '事假', DATE_ADD(CURDATE(), INTERVAL 5 DAY), DATE_ADD(CURDATE(), INTERVAL 6 DAY), 2, '家中有急事需要处理', '已批准', '王五', '同意', NOW()),
(2, '李四', '技术部', '病假', DATE_ADD(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 2, '身体不适，需去医院检查', '待审批', NULL, NULL, NULL),
(4, '赵六', '市场部', '年假', DATE_SUB(CURDATE(), INTERVAL 3 DAY), DATE_SUB(CURDATE(), INTERVAL 1 DAY), 3, '计划外出旅游', '已批准', '郑十一', '批准休假', DATE_SUB(NOW(), INTERVAL 5 DAY));