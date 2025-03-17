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