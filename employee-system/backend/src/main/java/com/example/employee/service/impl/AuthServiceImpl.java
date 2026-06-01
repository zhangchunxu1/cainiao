package com.example.employee.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.employee.entity.User;
import com.example.employee.mapper.UserMapper;
import com.example.employee.service.AuthService;
import com.example.employee.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public String login(User user) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, user.getUsername());

        User existUser = userMapper.selectOne(queryWrapper);
        if (existUser == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!passwordEncoder.matches(user.getPassword(), existUser.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        return jwtUtil.generateToken(existUser.getUsername(), existUser.getId());
    }

    @Override
    public Map<String, Object> loginWithInfo(User user) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, user.getUsername());

        User existUser = userMapper.selectOne(queryWrapper);
        if (existUser == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!passwordEncoder.matches(user.getPassword(), existUser.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        String token = jwtUtil.generateToken(existUser.getUsername(), existUser.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("role", existUser.getRole() != null ? existUser.getRole() : "employee");
        result.put("realName", existUser.getRealName());
        result.put("userId", existUser.getId());
        return result;
    }
}