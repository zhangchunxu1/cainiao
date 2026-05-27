package com.example.employee.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.employee.common.Result;
import com.example.employee.entity.Department;
import com.example.employee.service.DepartmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "部门管理")
@RestController
@RequestMapping("/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @ApiOperation("获取部门列表")
    @GetMapping
    public Result<IPage<Department>> getDepartmentList(
            @ApiParam(value = "页码", defaultValue = "1") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam(value = "每页大小", defaultValue = "10") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam(value = "搜索关键字") @RequestParam(required = false) String keyword) {
        
        IPage<Department> result = departmentService.getDepartmentList(page, pageSize, keyword);
        return Result.success(result);
    }

    @ApiOperation("根据ID获取部门详情")
    @GetMapping("/{id}")
    public Result<Department> getDepartmentById(@PathVariable Long id) {
        Department department = departmentService.getById(id);
        if (department == null) {
            return Result.error("部门不存在");
        }
        return Result.success(department);
    }

    @ApiOperation("添加部门")
    @PostMapping
    public Result<Department> addDepartment(@Valid @RequestBody Department department) {
        boolean success = departmentService.save(department);
        if (success) {
            return Result.success(department);
        }
        return Result.error("添加失败");
    }

    @ApiOperation("更新部门信息")
    @PutMapping("/{id}")
    public Result<Department> updateDepartment(@PathVariable Long id, @Valid @RequestBody Department department) {
        department.setId(id);
        boolean success = departmentService.updateById(department);
        if (success) {
            return Result.success(department);
        }
        return Result.error("更新失败");
    }

    @ApiOperation("删除部门")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteDepartment(@PathVariable Long id) {
        boolean success = departmentService.removeById(id);
        if (success) {
            return Result.success(true);
        }
        return Result.error("删除失败");
    }
}