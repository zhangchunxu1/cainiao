package com.example.employee.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.employee.entity.DailyReport;

import java.util.List;

public interface DailyReportService extends IService<DailyReport> {

    IPage<DailyReport> getDailyReportList(Integer page, Integer pageSize, String keyword, Long employeeId, String status, List<Long> accessibleEmployeeIds);
}
