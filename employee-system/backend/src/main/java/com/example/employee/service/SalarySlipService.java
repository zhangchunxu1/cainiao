package com.example.employee.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.employee.entity.SalarySlip;

import java.util.Map;

public interface SalarySlipService {
    IPage<SalarySlip> getSalarySlipPage(Long employeeId, String payMonth, Integer page, Integer pageSize);
    SalarySlip getSalarySlipById(Long id);
    SalarySlip createSalarySlip(SalarySlip salarySlip);
    SalarySlip updateSalarySlip(SalarySlip salarySlip);
    boolean deleteSalarySlip(Long id);
    Map<String, Object> getSalarySummary(Long employeeId, String payMonth);
}