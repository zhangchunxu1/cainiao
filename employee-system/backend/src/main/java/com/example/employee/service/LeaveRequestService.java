package com.example.employee.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.employee.entity.LeaveRequest;

public interface LeaveRequestService extends IService<LeaveRequest> {
    
    IPage<LeaveRequest> getLeaveRequestList(Integer page, Integer pageSize, String keyword);
}