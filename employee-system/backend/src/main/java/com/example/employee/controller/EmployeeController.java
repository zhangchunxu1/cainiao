package com.example.employee.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.employee.common.Result;
import com.example.employee.entity.Employee;
import com.example.employee.service.EmployeeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "员工管理")
@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @ApiOperation("获取员工列表")
    @GetMapping
    public Result<IPage<Employee>> getEmployeeList(
            @ApiParam(value = "页码", defaultValue = "1") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam(value = "每页大小", defaultValue = "10") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam(value = "关键字") @RequestParam(required = false) String keyword) {
        IPage<Employee> employeeList = employeeService.getEmployeeList(page, pageSize, keyword);
        return Result.success(employeeList);
    }

    @ApiOperation("根据ID获取员工")
    @GetMapping("/{id}")
    public Result<Employee> getEmployeeById(@ApiParam(value = "员工ID", required = true) @PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        if (employee == null) {
            return Result.error("员工不存在");
        }
        return Result.success(employee);
    }

    @ApiOperation("添加员工")
    @PostMapping
    public Result<Employee> addEmployee(@ApiParam(value = "员工信息", required = true) @Valid @RequestBody Employee employee) {
        boolean success = employeeService.save(employee);
        if (success) {
            return Result.success(employee);
        }
        return Result.error("添加员工失败");
    }

    @ApiOperation("更新员工")
    @PutMapping("/{id}")
    public Result<Employee> updateEmployee(
            @ApiParam(value = "员工ID", required = true) @PathVariable Long id,
            @ApiParam(value = "员工信息", required = true) @Valid @RequestBody Employee employee) {
        employee.setId(id);
        boolean success = employeeService.updateById(employee);
        if (success) {
            return Result.success(employee);
        }
        return Result.error("更新员工失败");
    }

    @ApiOperation("批量删除员工")
    @DeleteMapping("/batch")
    public Result<Integer> batchDeleteEmployee(@RequestBody java.util.List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.error("请选择要删除的记录");
        }
        int deletedCount = 0;
        for (Long id : ids) {
            Employee employee = employeeService.getById(id);
            if (employee == null || employee.getDeleted() == 1) {
                continue;
            }
            employee.setDeleted(1);
            if (employeeService.updateById(employee)) {
                deletedCount++;
            }
        }
        if (deletedCount > 0) {
            return Result.success(deletedCount);
        }
        return Result.error("批量删除失败");
    }

    @ApiOperation("删除员工")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteEmployee(@ApiParam(value = "员工ID", required = true) @PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        if (employee == null) {
            return Result.error("员工不存在");
        }
        employee.setDeleted(1);
        boolean success = employeeService.updateById(employee);
        if (success) {
            return Result.success(true);
        }
        return Result.error("删除员工失败");
    }

    @ApiOperation("搜索员工（用于下拉选择）")
    @GetMapping("/search")
    public Result<java.util.List<Employee>> searchEmployees(
            @ApiParam(value = "搜索关键字") @RequestParam(required = false) String keyword) {
        java.util.List<Employee> employees = employeeService.searchEmployees(keyword);
        return Result.success(employees);
    }
} 