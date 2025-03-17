package com.example.employee.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.employee.entity.Employee;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeMapper extends BaseMapper<Employee> {
} 