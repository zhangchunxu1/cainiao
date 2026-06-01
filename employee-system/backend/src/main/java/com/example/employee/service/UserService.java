package com.example.employee.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.employee.entity.User;

public interface UserService extends IService<User> {
    IPage<User> getUserList(Integer page, Integer pageSize, String keyword);
    User getUserByUsername(String username);
}
