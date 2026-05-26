package com.example.employee.config;

import com.example.employee.entity.User;
import com.example.employee.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        String username = "admin";
        String rawPassword = "admin123";
        
        // 查询是否已存在admin用户
        User existingUser = userMapper.selectById(1L);
        
        if (existingUser == null) {
            // 不存在则创建新用户
            User adminUser = new User();
            adminUser.setUsername(username);
            adminUser.setPassword(passwordEncoder.encode(rawPassword));
            adminUser.setRealName("系统管理员");
            userMapper.insert(adminUser);
            System.out.println("====================================");
            System.out.println("默认管理员账号已创建！");
            System.out.println("用户名: " + username);
            System.out.println("密码: " + rawPassword);
            System.out.println("====================================");
        } else {
            // 存在则更新密码（确保密码正确）
            existingUser.setPassword(passwordEncoder.encode(rawPassword));
            userMapper.updateById(existingUser);
            System.out.println("====================================");
            System.out.println("管理员密码已重置！");
            System.out.println("用户名: " + username);
            System.out.println("密码: " + rawPassword);
            System.out.println("====================================");
        }
    }
}