package com.example.employee.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.employee.entity.SalarySlip;
import com.example.employee.mapper.SalarySlipMapper;
import com.example.employee.service.SalarySlipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SalarySlipServiceImpl implements SalarySlipService {

    @Autowired
    private SalarySlipMapper salarySlipMapper;

    @Override
    public IPage<SalarySlip> getSalarySlipPage(Long employeeId, String payMonth, Integer page, Integer pageSize) {
        Page<SalarySlip> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<SalarySlip> queryWrapper = new LambdaQueryWrapper<>();
        
        if (employeeId != null) {
            queryWrapper.eq(SalarySlip::getEmployeeId, employeeId);
        }
        
        if (payMonth != null && !payMonth.isEmpty()) {
            queryWrapper.like(SalarySlip::getPayMonth, payMonth);
        }
        
        queryWrapper.orderByDesc(SalarySlip::getPayMonth);
        
        return salarySlipMapper.selectPage(pageParam, queryWrapper);
    }

    @Override
    public SalarySlip getSalarySlipById(Long id) {
        return salarySlipMapper.selectById(id);
    }

    @Override
    public SalarySlip createSalarySlip(SalarySlip salarySlip) {
        salarySlipMapper.insert(salarySlip);
        return salarySlip;
    }

    @Override
    public SalarySlip updateSalarySlip(SalarySlip salarySlip) {
        salarySlipMapper.updateById(salarySlip);
        return salarySlip;
    }

    @Override
    public boolean deleteSalarySlip(Long id) {
        return salarySlipMapper.deleteById(id) > 0;
    }

    @Override
    public Map<String, Object> getSalarySummary(Long employeeId, String payMonth) {
        Map<String, Object> summary = new HashMap<>();
        
        LambdaQueryWrapper<SalarySlip> queryWrapper = new LambdaQueryWrapper<>();
        if (employeeId != null) {
            queryWrapper.eq(SalarySlip::getEmployeeId, employeeId);
        }
        if (payMonth != null && !payMonth.isEmpty()) {
            queryWrapper.like(SalarySlip::getPayMonth, payMonth);
        }
        
        summary.put("totalCount", salarySlipMapper.selectCount(queryWrapper));
        return summary;
    }
}