-- Production demo-data cleanup script.
-- WARNING: This deletes business/demo data. Back up the database before running it.
-- It keeps the built-in admin account and removes demo users and all business records.

USE `employee_db`;

SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE `salary_slip`;
TRUNCATE TABLE `contract`;
TRUNCATE TABLE `reimbursement`;
TRUNCATE TABLE `daily_report`;
TRUNCATE TABLE `leave_request`;
TRUNCATE TABLE `announcement`;
TRUNCATE TABLE `attendance`;
TRUNCATE TABLE `department`;
TRUNCATE TABLE `employee`;

DELETE FROM `user` WHERE `username` <> 'admin';
UPDATE `user` SET `role` = 'admin', `real_name` = '系统管理员' WHERE `username` = 'admin';

SET FOREIGN_KEY_CHECKS = 1;
