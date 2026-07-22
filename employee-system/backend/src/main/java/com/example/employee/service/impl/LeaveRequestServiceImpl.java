package com.example.employee.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.employee.entity.Employee;
import com.example.employee.entity.LeaveRequest;
import com.example.employee.mapper.LeaveRequestMapper;
import com.example.employee.service.EmployeeService;
import com.example.employee.service.LeaveRequestService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaveRequestServiceImpl extends ServiceImpl<LeaveRequestMapper, LeaveRequest> implements LeaveRequestService {

    @Resource
    private EmployeeService employeeService;

    @Override
    public IPage<LeaveRequest> getLeaveRequestList(Integer page, Integer pageSize, String keyword) {
        Page<LeaveRequest> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<LeaveRequest> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.and(w -> w
                .like(LeaveRequest::getEmployeeName, keyword)
                .or()
                .like(LeaveRequest::getDepartment, keyword)
                .or()
                .like(LeaveRequest::getStatus, keyword)
            );
        }
        
        wrapper.orderByDesc(LeaveRequest::getCreateTime);
        return this.page(pageParam, wrapper);
    }

    @Override
    public IPage<LeaveRequest> getLeaveRequestListWithPermission(Integer page, Integer pageSize, String keyword, String status, String role, String employeeName) {
        Page<LeaveRequest> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<LeaveRequest> wrapper = new LambdaQueryWrapper<>();
        
        if ("admin".equals(role)) {
        } else {
            LambdaQueryWrapper<Employee> empWrapper = new LambdaQueryWrapper<>();
            empWrapper.eq(Employee::getName, employeeName);
            List<Employee> employees = employeeService.list(empWrapper);
            
            if (employees != null && !employees.isEmpty()) {
                Employee currentEmployee = employees.get(0);
                LambdaQueryWrapper<Employee> subWrapper = new LambdaQueryWrapper<>();
                subWrapper.eq(Employee::getManagerId, currentEmployee.getId());
                List<Employee> subordinates = employeeService.list(subWrapper);
                
                List<String> subordinateNames = subordinates.stream()
                    .map(Employee::getName)
                    .collect(Collectors.toList());
                subordinateNames.add(currentEmployee.getName());
                
                wrapper.in(LeaveRequest::getEmployeeName, subordinateNames);
            } else {
                wrapper.eq(LeaveRequest::getEmployeeName, employeeName);
            }
        }
        
        if (StringUtils.isNotBlank(status)) {
            wrapper.eq(LeaveRequest::getStatus, status);
        }
        
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.and(w -> w
                .like(LeaveRequest::getEmployeeName, keyword)
                .or()
                .like(LeaveRequest::getDepartment, keyword)
                .or()
                .like(LeaveRequest::getStatus, keyword)
            );
        }
        
        wrapper.orderByDesc(LeaveRequest::getCreateTime);
        return this.page(pageParam, wrapper);
    }
}