package com.example.employee.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.employee.entity.SalarySlip;

import java.util.Map;
import java.util.List;

public interface SalarySlipService {
    IPage<SalarySlip> getSalarySlipPage(Long employeeId, String payMonth, Integer page, Integer pageSize, List<Long> accessibleEmployeeIds);
    SalarySlip getSalarySlipById(Long id);
    SalarySlip createSalarySlip(SalarySlip salarySlip);
    SalarySlip updateSalarySlip(SalarySlip salarySlip);
    boolean deleteSalarySlip(Long id);
    Map<String, Object> getSalarySummary(Long employeeId, String payMonth, List<Long> accessibleEmployeeIds);
}
