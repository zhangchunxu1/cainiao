package com.example.employee.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.employee.entity.Attendance;

public interface AttendanceService extends IService<Attendance> {
    
    IPage<Attendance> getAttendanceList(Integer page, Integer pageSize, String keyword);
}