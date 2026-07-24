package com.example.employee.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.employee.common.Result;
import com.example.employee.entity.SalarySlip;
import com.example.employee.entity.User;
import com.example.employee.service.CurrentUserService;
import com.example.employee.service.SalarySlipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/salary-slips")
@Tag(name = "工资条管理", description = "工资条相关接口")
public class SalarySlipController {

    @Autowired
    private SalarySlipService salarySlipService;

    @Autowired
    private CurrentUserService currentUserService;

    @GetMapping
    @Operation(summary = "分页查询工资条")
    public Result<IPage<SalarySlip>> getSalarySlipPage(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) String payMonth,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        User currentUser = currentUserService.requireUser(request);
        if (employeeId != null && !currentUserService.canAccessEmployee(currentUser, employeeId)) {
            return Result.error("没有权限查看该员工工资条");
        }
        IPage<SalarySlip> salarySlipPage = salarySlipService.getSalarySlipPage(
                employeeId, payMonth, page, pageSize, currentUserService.getAccessibleEmployeeIds(currentUser));
        return Result.success(salarySlipPage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询工资条")
    public Result<SalarySlip> getSalarySlipById(@PathVariable Long id, HttpServletRequest request) {
        SalarySlip salarySlip = salarySlipService.getSalarySlipById(id);
        if (salarySlip == null) {
            return Result.error(404, "工资条不存在");
        }
        User currentUser = currentUserService.requireUser(request);
        if (!currentUserService.canAccessEmployee(currentUser, salarySlip.getEmployeeId())) {
            return Result.error("没有权限查看该工资条");
        }
        return Result.success(salarySlip);
    }

    @PostMapping
    @Operation(summary = "创建工资条")
    public Result<SalarySlip> createSalarySlip(@RequestBody SalarySlip salarySlip, HttpServletRequest request) {
        User currentUser = currentUserService.requireUser(request);
        if (!currentUserService.isAdmin(currentUser)) {
            return Result.error("只有管理员可以创建工资条");
        }
        SalarySlip created = salarySlipService.createSalarySlip(salarySlip);
        return Result.success(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新工资条")
    public Result<SalarySlip> updateSalarySlip(@PathVariable Long id, @RequestBody SalarySlip salarySlip, HttpServletRequest request) {
        User currentUser = currentUserService.requireUser(request);
        if (!currentUserService.isAdmin(currentUser)) {
            return Result.error("只有管理员可以更新工资条");
        }
        salarySlip.setId(id);
        SalarySlip updated = salarySlipService.updateSalarySlip(salarySlip);
        return Result.success(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除工资条")
    public Result<Boolean> deleteSalarySlip(@PathVariable Long id, HttpServletRequest request) {
        User currentUser = currentUserService.requireUser(request);
        if (!currentUserService.isAdmin(currentUser)) {
            return Result.error("只有管理员可以删除工资条");
        }
        boolean deleted = salarySlipService.deleteSalarySlip(id);
        if (!deleted) {
            return Result.error(404, "工资条不存在");
        }
        return Result.success(true);
    }

    @GetMapping("/summary")
    @Operation(summary = "工资统计")
    public Result<Map<String, Object>> getSalarySummary(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) String payMonth,
            HttpServletRequest request) {
        User currentUser = currentUserService.requireUser(request);
        if (employeeId != null && !currentUserService.canAccessEmployee(currentUser, employeeId)) {
            return Result.error("没有权限查看该员工工资统计");
        }
        Map<String, Object> summary = salarySlipService.getSalarySummary(
                employeeId, payMonth, currentUserService.getAccessibleEmployeeIds(currentUser));
        return Result.success(summary);
    }
}
