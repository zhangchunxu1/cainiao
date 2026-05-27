package com.example.employee.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.employee.common.Result;
import com.example.employee.entity.LeaveRequest;
import com.example.employee.service.LeaveRequestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "请假管理")
@RestController
@RequestMapping("/leaves")
@RequiredArgsConstructor
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    @ApiOperation("获取请假列表")
    @GetMapping
    public Result<IPage<LeaveRequest>> getLeaveRequestList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        
        IPage<LeaveRequest> result = leaveRequestService.getLeaveRequestList(page, pageSize, keyword);
        return Result.success(result);
    }

    @ApiOperation("根据ID获取请假详情")
    @GetMapping("/{id}")
    public Result<LeaveRequest> getLeaveRequestById(@PathVariable Long id) {
        LeaveRequest leaveRequest = leaveRequestService.getById(id);
        if (leaveRequest == null) {
            return Result.error("请假记录不存在");
        }
        return Result.success(leaveRequest);
    }

    @ApiOperation("提交请假申请")
    @PostMapping
    public Result<LeaveRequest> addLeaveRequest(@Valid @RequestBody LeaveRequest leaveRequest) {
        boolean success = leaveRequestService.save(leaveRequest);
        if (success) {
            return Result.success(leaveRequest);
        }
        return Result.error("提交失败");
    }

    @ApiOperation("更新请假记录")
    @PutMapping("/{id}")
    public Result<LeaveRequest> updateLeaveRequest(@PathVariable Long id, @Valid @RequestBody LeaveRequest leaveRequest) {
        leaveRequest.setId(id);
        boolean success = leaveRequestService.updateById(leaveRequest);
        if (success) {
            return Result.success(leaveRequest);
        }
        return Result.error("更新失败");
    }

    @ApiOperation("删除请假记录")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteLeaveRequest(@PathVariable Long id) {
        boolean success = leaveRequestService.removeById(id);
        if (success) {
            return Result.success(true);
        }
        return Result.error("删除失败");
    }

    @ApiOperation("审批请假申请")
    @PutMapping("/{id}/approve")
    public Result<LeaveRequest> approveLeaveRequest(
            @PathVariable Long id,
            @RequestParam(required = false) String approver,
            @RequestParam(required = false) String approvalComment) {

        LeaveRequest leaveRequest = leaveRequestService.getById(id);
        if (leaveRequest == null) {
            return Result.error("请假记录不存在");
        }

        leaveRequest.setStatus("已批准");
        if (approver != null) {
            leaveRequest.setApprover(approver);
        }
        if (approvalComment != null) {
            leaveRequest.setApprovalComment(approvalComment);
        }
        leaveRequest.setApprovalTime(java.time.LocalDateTime.now());

        boolean success = leaveRequestService.updateById(leaveRequest);
        if (success) {
            return Result.success(leaveRequest);
        }
        return Result.error("审批失败");
    }

    @ApiOperation("拒绝请假申请")
    @PutMapping("/{id}/reject")
    public Result<LeaveRequest> rejectLeaveRequest(
            @PathVariable Long id,
            @RequestParam(required = false) String approver,
            @RequestParam(required = false) String approvalComment) {

        LeaveRequest leaveRequest = leaveRequestService.getById(id);
        if (leaveRequest == null) {
            return Result.error("请假记录不存在");
        }

        leaveRequest.setStatus("已拒绝");
        if (approver != null) {
            leaveRequest.setApprover(approver);
        }
        if (approvalComment != null) {
            leaveRequest.setApprovalComment(approvalComment);
        }
        leaveRequest.setApprovalTime(java.time.LocalDateTime.now());

        boolean success = leaveRequestService.updateById(leaveRequest);
        if (success) {
            return Result.success(leaveRequest);
        }
        return Result.error("拒绝失败");
    }
}