USE employee_db;

INSERT IGNORE INTO `user` (`username`, `password`, `real_name`, `role`) VALUES
('zhangsan', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '张三', 'employee'),
('lisi', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李四', 'employee'),
('wangwu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '王五', 'employee'),
('zhaoliu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '赵六', 'employee'),
('qianqi', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '钱七', 'employee'),
('sunba', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '孙八', 'employee'),
('zhoujiu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '周九', 'employee'),
('wushi', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '吴十', 'employee'),
('zhengshiyi', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '郑十一', 'employee'),
('wangshier', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '王十二', 'employee');

UPDATE `user` SET `real_name` = '张三' WHERE `username` = 'zhangsan';
UPDATE `user` SET `real_name` = '李四' WHERE `username` = 'lisi';
UPDATE `user` SET `real_name` = '王五' WHERE `username` = 'wangwu';
UPDATE `user` SET `real_name` = '赵六' WHERE `username` = 'zhaoliu';
UPDATE `user` SET `real_name` = '钱七' WHERE `username` = 'qianqi';
UPDATE `user` SET `real_name` = '孙八' WHERE `username` = 'sunba';
UPDATE `user` SET `real_name` = '周九' WHERE `username` = 'zhoujiu';
UPDATE `user` SET `real_name` = '吴十' WHERE `username` = 'wushi';
UPDATE `user` SET `real_name` = '郑十一' WHERE `username` = 'zhengshiyi';
UPDATE `user` SET `real_name` = '王十二' WHERE `username` = 'wangshier';
