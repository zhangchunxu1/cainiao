ALTER TABLE `reimbursement` ADD COLUMN `manager_approve_date` date COMMENT '部门经理审批日期' AFTER `apply_date`;
ALTER TABLE `reimbursement` ADD COLUMN `manager_approver` varchar(100) COMMENT '部门经理审批人' AFTER `manager_approve_date`;
ALTER TABLE `reimbursement` ADD COLUMN `manager_remark` varchar(500) COMMENT '部门经理审批意见' AFTER `manager_approver`;
ALTER TABLE `reimbursement` ADD COLUMN `finance_approve_date` date COMMENT '财务审批日期' AFTER `manager_remark`;
ALTER TABLE `reimbursement` ADD COLUMN `finance_approver` varchar(100) COMMENT '财务审批人' AFTER `finance_approve_date`;
ALTER TABLE `reimbursement` ADD COLUMN `finance_remark` varchar(500) COMMENT '财务审批意见' AFTER `finance_approver`;