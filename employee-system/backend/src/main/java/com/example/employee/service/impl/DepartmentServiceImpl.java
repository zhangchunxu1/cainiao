package com.example.employee.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.employee.entity.Department;
import com.example.employee.mapper.DepartmentMapper;
import com.example.employee.service.DepartmentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {

    @Override
    public IPage<Department> getDepartmentList(Integer page, Integer pageSize, String keyword) {
        Page<Department> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.and(w -> w
                .like(Department::getName, keyword)
                .or()
                .like(Department::getManager, keyword)
            );
        }
        
        wrapper.orderByDesc(Department::getCreateTime);
        return this.page(pageParam, wrapper);
    }
}