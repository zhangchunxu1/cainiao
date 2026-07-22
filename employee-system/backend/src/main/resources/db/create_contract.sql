USE employee_db;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='合同表';