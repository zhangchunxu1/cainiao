package com.example.employee.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.employee.common.Result;
import com.example.employee.entity.Reimbursement;
import com.example.employee.service.ReimbursementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@Api(tags = "报销管理")
@RestController
@RequestMapping("/reimbursements")
public class ReimbursementController {

    @Resource
    private ReimbursementService reimbursementService;

    @ApiOperation("获取报销列表")
    @GetMapping
    public Result<IPage<Reimbursement>> getReimbursementList(
            @ApiParam(value = "页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam(value = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam(value = "搜索关键字") @RequestParam(required = false) String keyword) {
        IPage<Reimbursement> reimbursementPage = reimbursementService.getReimbursementList(page, pageSize, keyword);
        return Result.success(reimbursementPage);
    }

    @ApiOperation("获取报销详情")
    @GetMapping("/{id}")
    public Result<Reimbursement> getReimbursementById(
            @ApiParam(value = "报销ID", required = true) @PathVariable Long id) {
        Reimbursement reimbursement = reimbursementService.getById(id);
        if (reimbursement == null) {
            return Result.error(404, "报销不存在");
        }
        return Result.success(reimbursement);
    }

    @ApiOperation("创建报销")
    @PostMapping
    public Result<Reimbursement> createReimbursement(
            @ApiParam(value = "报销信息", required = true) @RequestBody Reimbursement reimbursement) {
        boolean success = reimbursementService.save(reimbursement);
        if (success) {
            return Result.success(reimbursement);
        }
        return Result.error("创建报销失败");
    }

    @ApiOperation("更新报销")
    @PutMapping("/{id}")
    public Result<Reimbursement> updateReimbursement(
            @ApiParam(value = "报销ID", required = true) @PathVariable Long id,
            @ApiParam(value = "报销信息", required = true) @RequestBody Reimbursement reimbursement) {
        Reimbursement existing = reimbursementService.getById(id);
        if (existing == null) {
            return Result.error(404, "报销不存在");
        }
        reimbursement.setId(id);
        boolean success = reimbursementService.updateById(reimbursement);
        if (success) {
            return Result.success(reimbursement);
        }
        return Result.error("更新报销失败");
    }

    @ApiOperation("删除报销")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteReimbursement(
            @ApiParam(value = "报销ID", required = true) @PathVariable Long id) {
        Reimbursement reimbursement = reimbursementService.getById(id);
        if (reimbursement == null) {
            return Result.error("报销不存在");
        }
        reimbursement.setDeleted(1);
        boolean success = reimbursementService.updateById(reimbursement);
        if (success) {
            return Result.success(true);
        }
        return Result.error("删除报销失败");
    }

    @ApiOperation("部门经理审批通过")
    @PostMapping("/{id}/manager-approve")
    public Result<Reimbursement> managerApprove(
            @ApiParam(value = "报销ID", required = true) @PathVariable Long id,
            @ApiParam(value = "审批信息", required = true) @RequestBody Map<String, String> request) {
        try {
            String approver = request.get("approver");
            String remark = request.get("remark");
            Reimbursement reimbursement = reimbursementService.managerApprove(id, approver, remark);
            return Result.success(reimbursement);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("部门经理驳回")
    @PostMapping("/{id}/manager-reject")
    public Result<Reimbursement> managerReject(
            @ApiParam(value = "报销ID", required = true) @PathVariable Long id,
            @ApiParam(value = "审批信息", required = true) @RequestBody Map<String, String> request) {
        try {
            String approver = request.get("approver");
            String remark = request.get("remark");
            Reimbursement reimbursement = reimbursementService.managerReject(id, approver, remark);
            return Result.success(reimbursement);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("财务审批通过")
    @PostMapping("/{id}/finance-approve")
    public Result<Reimbursement> financeApprove(
            @ApiParam(value = "报销ID", required = true) @PathVariable Long id,
            @ApiParam(value = "审批信息", required = true) @RequestBody Map<String, String> request) {
        try {
            String approver = request.get("approver");
            String remark = request.get("remark");
            Reimbursement reimbursement = reimbursementService.financeApprove(id, approver, remark);
            return Result.success(reimbursement);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("财务驳回")
    @PostMapping("/{id}/finance-reject")
    public Result<Reimbursement> financeReject(
            @ApiParam(value = "报销ID", required = true) @PathVariable Long id,
            @ApiParam(value = "审批信息", required = true) @RequestBody Map<String, String> request) {
        try {
            String approver = request.get("approver");
            String remark = request.get("remark");
            Reimbursement reimbursement = reimbursementService.financeReject(id, approver, remark);
            return Result.success(reimbursement);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}