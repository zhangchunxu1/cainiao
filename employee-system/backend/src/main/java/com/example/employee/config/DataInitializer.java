package com.example.employee.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.employee.entity.User;
import com.example.employee.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${app.admin.username:admin}")
    private String adminUsername;

    @Value("${app.admin.password:admin123}")
    private String adminPassword;

    @Value("${app.admin.real-name:系统管理员}")
    private String adminRealName;

    @Override
    public void run(String... args) throws Exception {
        try {
            jdbcTemplate.execute("ALTER TABLE `user` ADD COLUMN `role` varchar(20) NOT NULL DEFAULT 'employee' COMMENT '角色' AFTER `real_name`");
            System.out.println("已自动添加 role 字段到 user 表");
        } catch (Exception e) {
        }

        User existingUser = userMapper.selectOne(
                new QueryWrapper<User>().eq("username", adminUsername).last("LIMIT 1")
        );

        if (existingUser == null) {
            User adminUser = new User();
            adminUser.setUsername(adminUsername);
            adminUser.setPassword(passwordEncoder.encode(adminPassword));
            adminUser.setRealName(adminRealName);
            adminUser.setRole("admin");
            userMapper.insert(adminUser);
            System.out.println("====================================");
            System.out.println("默认管理员账号已创建！");
            System.out.println("用户名: " + adminUsername);
            System.out.println("密码: " + adminPassword);
            System.out.println("====================================");
        } else if (!"admin".equalsIgnoreCase(existingUser.getRole())) {
            existingUser.setRole("admin");
            userMapper.updateById(existingUser);
            System.out.println("====================================");
            System.out.println("已恢复管理员角色，未修改密码");
            System.out.println("用户名: " + adminUsername);
            System.out.println("====================================");
        } else {
            System.out.println("管理员账号已存在，跳过初始化");
        }
    }
}
