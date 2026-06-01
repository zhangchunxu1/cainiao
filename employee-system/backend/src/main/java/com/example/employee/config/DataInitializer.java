package com.example.employee.config;

import com.example.employee.entity.User;
import com.example.employee.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            jdbcTemplate.execute("ALTER TABLE `user` ADD COLUMN `role` varchar(20) NOT NULL DEFAULT 'employee' COMMENT '角色' AFTER `real_name`");
            System.out.println("已自动添加 role 字段到 user 表");
        } catch (Exception e) {
        }

        String username = "admin";
        String rawPassword = "admin123";
        
        User existingUser = userMapper.selectById(1L);
        
        if (existingUser == null) {
            User adminUser = new User();
            adminUser.setUsername(username);
            adminUser.setPassword(passwordEncoder.encode(rawPassword));
            adminUser.setRealName("系统管理员");
            adminUser.setRole("admin");
            userMapper.insert(adminUser);
            System.out.println("====================================");
            System.out.println("默认管理员账号已创建！");
            System.out.println("用户名: " + username);
            System.out.println("密码: " + rawPassword);
            System.out.println("====================================");
        } else {
            existingUser.setPassword(passwordEncoder.encode(rawPassword));
            existingUser.setRole("admin");
            userMapper.updateById(existingUser);
            System.out.println("====================================");
            System.out.println("管理员密码已重置！");
            System.out.println("用户名: " + username);
            System.out.println("密码: " + rawPassword);
            System.out.println("====================================");
        }
    }
}