package com.example.employee.service;

import com.example.employee.entity.User;

import java.util.Map;

public interface AuthService {
    String login(User user);
    Map<String, Object> loginWithInfo(User user);
}