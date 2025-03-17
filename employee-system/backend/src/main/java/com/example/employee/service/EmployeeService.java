package com.example.employee.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.employee.entity.Employee;

public interface EmployeeService extends IService<Employee> {

    /**
     * 分页查询员工列表
     *
     * @param page     页码
     * @param pageSize 每页大小
     * @param keyword  关键字
     * @return 分页结果
     */
    IPage<Employee> getEmployeeList(Integer page, Integer pageSize, String keyword);
} 