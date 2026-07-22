package com.example.employee.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.employee.entity.Employee;
import com.example.employee.mapper.EmployeeMapper;
import com.example.employee.service.EmployeeService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    @Override
    public IPage<Employee> getEmployeeList(Integer page, Integer pageSize, String keyword) {
        Page<Employee> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        
        queryWrapper.eq(Employee::getDeleted, 0);
        
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                    .like(Employee::getName, keyword)
                    .or().like(Employee::getDepartment, keyword)
                    .or().like(Employee::getPosition, keyword));
        }
        
        queryWrapper.orderByDesc(Employee::getId);
        return baseMapper.selectPage(pageParam, queryWrapper);
    }

    @Override
    public java.util.List<Employee> searchEmployees(String keyword) {
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getDeleted, 0);
        
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like(Employee::getName, keyword);
        }
        
        queryWrapper.orderByAsc(Employee::getName);
        return baseMapper.selectList(queryWrapper);
    }
} 