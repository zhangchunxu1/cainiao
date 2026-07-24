package com.example.employee.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.employee.entity.Attendance;
import com.example.employee.mapper.AttendanceMapper;
import com.example.employee.service.AttendanceService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttendanceServiceImpl extends ServiceImpl<AttendanceMapper, Attendance> implements AttendanceService {

    @Override
    public IPage<Attendance> getAttendanceList(Integer page, Integer pageSize, String keyword, Long employeeId, String month, List<Long> accessibleEmployeeIds) {
        Page<Attendance> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Attendance> wrapper = new LambdaQueryWrapper<>();

        if (accessibleEmployeeIds != null) {
            if (accessibleEmployeeIds.isEmpty()) {
                wrapper.eq(Attendance::getEmployeeId, -1L);
            } else {
                wrapper.in(Attendance::getEmployeeId, accessibleEmployeeIds);
            }
        }

        if (employeeId != null) {
            wrapper.eq(Attendance::getEmployeeId, employeeId);
        }

        if (StringUtils.isNotBlank(month)) {
            wrapper.apply("DATE_FORMAT(date, '%Y-%m') = {0}", month);
        }
        
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.and(w -> w
                .like(Attendance::getEmployeeName, keyword)
                .or()
                .like(Attendance::getStatus, keyword)
            );
        }
        
        wrapper.orderByDesc(Attendance::getDate);
        return this.page(pageParam, wrapper);
    }
}
