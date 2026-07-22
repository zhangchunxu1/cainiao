package com.example.employee.controller;

import com.example.employee.common.Result;
import com.example.employee.entity.Contract;
import com.example.employee.service.ContractService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "合同管理")
@RestController
@RequestMapping("/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;

    @ApiOperation("获取合同列表")
    @GetMapping
    public Result<Map<String, Object>> getContractList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String contractType) {

        IPage<Contract> result = contractService.getContractList(page, pageSize, keyword, status, contractType);

        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        data.put("current", result.getCurrent());
        data.put("pageSize", result.getSize());

        return Result.success(data);
    }

    @ApiOperation("根据ID获取合同详情")
    @GetMapping("/{id}")
    public Result<Contract> getContractById(@PathVariable Long id) {
        Contract contract = contractService.getById(id);
        if (contract == null) {
            return Result.error("合同不存在");
        }
        return Result.success(contract);
    }

    @ApiOperation("添加合同")
    @PostMapping
    public Result<Contract> addContract(@Valid @RequestBody Contract contract) {
        boolean success = contractService.save(contract);
        if (success) {
            return Result.success(contract);
        }
        return Result.error("添加失败");
    }

    @ApiOperation("更新合同")
    @PutMapping("/{id}")
    public Result<Contract> updateContract(@PathVariable Long id, @Valid @RequestBody Contract contract) {
        contract.setId(id);
        boolean success = contractService.updateById(contract);
        if (success) {
            return Result.success(contract);
        }
        return Result.error("更新失败");
    }

    @ApiOperation("删除合同")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteContract(@PathVariable Long id) {
        Contract contract = contractService.getById(id);
        if (contract == null) {
            return Result.error("合同不存在");
        }
        boolean success = contractService.removeById(id);
        if (success) {
            return Result.success(true);
        }
        return Result.error("删除失败");
    }
}