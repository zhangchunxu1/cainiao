package com.example.employee.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.employee.entity.DailyReport;
import com.example.employee.mapper.DailyReportMapper;
import com.example.employee.service.DailyReportService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DailyReportServiceImpl extends ServiceImpl<DailyReportMapper, DailyReport> implements DailyReportService {

    @Override
    public IPage<DailyReport> getDailyReportList(Integer page, Integer pageSize, String keyword, Long employeeId, String status, List<Long> accessibleEmployeeIds) {
        Page<DailyReport> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<DailyReport> wrapper = new LambdaQueryWrapper<>();

        if (accessibleEmployeeIds != null) {
            if (accessibleEmployeeIds.isEmpty()) {
                wrapper.eq(DailyReport::getEmployeeId, -1L);
            } else {
                wrapper.in(DailyReport::getEmployeeId, accessibleEmployeeIds);
            }
        }
        
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.and(w -> w
                .like(DailyReport::getEmployeeName, keyword)
                .or()
                .like(DailyReport::getDepartment, keyword)
            );
        }
        
        if (employeeId != null) {
            wrapper.eq(DailyReport::getEmployeeId, employeeId);
        }
        
        if (StringUtils.isNotBlank(status)) {
            wrapper.eq(DailyReport::getStatus, status);
        }
        
        wrapper.orderByDesc(DailyReport::getReportDate);
        wrapper.orderByDesc(DailyReport::getCreateTime);
        return this.page(pageParam, wrapper);
    }
}
