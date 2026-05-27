package com.example.employee.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.employee.entity.Department;

public interface DepartmentService extends IService<Department> {
    
    IPage<Department> getDepartmentList(Integer page, Integer pageSize, String keyword);
}