USE employee_db;

ALTER TABLE employee ADD COLUMN manager_id bigint(20) DEFAULT NULL COMMENT '直属上级ID';

ALTER TABLE employee ADD INDEX idx_manager_id (manager_id);

UPDATE employee SET manager_id = 3 WHERE id IN (1, 2, 10);
UPDATE employee SET manager_id = 9 WHERE id = 4;
UPDATE employee SET manager_id = 8 WHERE id = 7;
