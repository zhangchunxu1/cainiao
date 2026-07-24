package com.example.employee.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.employee.entity.LeaveRequest;
import com.example.employee.mapper.LeaveRequestMapper;
import com.example.employee.service.LeaveRequestService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaveRequestServiceImpl extends ServiceImpl<LeaveRequestMapper, LeaveRequest> implements LeaveRequestService {

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
    public IPage<LeaveRequest> getLeaveRequestListWithPermission(Integer page, Integer pageSize, String keyword, String status, List<Long> accessibleEmployeeIds) {
        Page<LeaveRequest> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<LeaveRequest> wrapper = new LambdaQueryWrapper<>();
        
        if (accessibleEmployeeIds != null) {
            if (accessibleEmployeeIds.isEmpty()) {
                wrapper.eq(LeaveRequest::getEmployeeId, -1L);
            } else {
                wrapper.in(LeaveRequest::getEmployeeId, accessibleEmployeeIds);
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
