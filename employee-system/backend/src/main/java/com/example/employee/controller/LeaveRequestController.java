package com.example.employee.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.employee.common.Result;
import com.example.employee.entity.Employee;
import com.example.employee.entity.LeaveRequest;
import com.example.employee.entity.User;
import com.example.employee.service.CurrentUserService;
import com.example.employee.service.LeaveRequestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.temporal.ChronoUnit;

@Api(tags = "请假管理")
@RestController
@RequestMapping("/leaves")
@RequiredArgsConstructor
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;
    private final CurrentUserService currentUserService;

    @ApiOperation("获取请假列表")
    @GetMapping
    public Result<IPage<LeaveRequest>> getLeaveRequestList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            HttpServletRequest request) {
        User currentUser = currentUserService.requireUser(request);
        IPage<LeaveRequest> result = leaveRequestService.getLeaveRequestListWithPermission(
                page, pageSize, keyword, status, currentUserService.getAccessibleEmployeeIds(currentUser));
        return Result.success(result);
    }

    @ApiOperation("根据ID获取请假详情")
    @GetMapping("/{id}")
    public Result<LeaveRequest> getLeaveRequestById(@PathVariable Long id, HttpServletRequest request) {
        LeaveRequest leaveRequest = leaveRequestService.getById(id);
        if (leaveRequest == null) {
            return Result.error("请假记录不存在");
        }
        User currentUser = currentUserService.requireUser(request);
        if (!currentUserService.canAccessEmployee(currentUser, leaveRequest.getEmployeeId())) {
            return Result.error("没有权限查看该请假记录");
        }
        return Result.success(leaveRequest);
    }

    @ApiOperation("提交请假申请")
    @PostMapping
    public Result<LeaveRequest> addLeaveRequest(@Valid @RequestBody LeaveRequest leaveRequest, HttpServletRequest request) {
        User currentUser = currentUserService.requireUser(request);
        Employee currentEmployee = currentUserService.requireEmployee(currentUser);
        fillRequester(leaveRequest, currentEmployee);
        leaveRequest.setStatus("待审批");
        leaveRequest.setApprover(null);
        leaveRequest.setApprovalComment(null);
        leaveRequest.setApprovalTime(null);
        fillLeaveDays(leaveRequest);
        boolean success = leaveRequestService.save(leaveRequest);
        if (success) {
            return Result.success(leaveRequest);
        }
        return Result.error("提交失败");
    }

    @ApiOperation("更新请假记录")
    @PutMapping("/{id}")
    public Result<LeaveRequest> updateLeaveRequest(@PathVariable Long id, @Valid @RequestBody LeaveRequest leaveRequest, HttpServletRequest request) {
        LeaveRequest existing = leaveRequestService.getById(id);
        if (existing == null) {
            return Result.error("请假记录不存在");
        }
        if (!"待审批".equals(existing.getStatus())) {
            return Result.error("只有待审批的请假申请可以修改");
        }
        User currentUser = currentUserService.requireUser(request);
        Employee currentEmployee = currentUserService.getEmployee(currentUser);
        boolean owner = currentEmployee != null && existing.getEmployeeId().equals(currentEmployee.getId());
        if (!currentUserService.isAdmin(currentUser) && !owner) {
            return Result.error("只有本人或管理员可以修改请假申请");
        }

        existing.setLeaveType(leaveRequest.getLeaveType());
        existing.setStartDate(leaveRequest.getStartDate());
        existing.setEndDate(leaveRequest.getEndDate());
        existing.setDays(leaveRequest.getDays());
        existing.setReason(leaveRequest.getReason());
        fillLeaveDays(existing);
        boolean success = leaveRequestService.updateById(existing);
        if (success) {
            return Result.success(existing);
        }
        return Result.error("更新失败");
    }

    @ApiOperation("删除请假记录")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteLeaveRequest(@PathVariable Long id, HttpServletRequest request) {
        LeaveRequest leaveRequest = leaveRequestService.getById(id);
        if (leaveRequest == null) {
            return Result.error("请假记录不存在");
        }

        User currentUser = currentUserService.requireUser(request);
        if (!currentUserService.canAccessEmployee(currentUser, leaveRequest.getEmployeeId())) {
            return Result.error("只有管理员、直属上级或本人才能删除请假记录");
        }
        if (!currentUserService.isAdmin(currentUser) && !"待审批".equals(leaveRequest.getStatus())) {
            return Result.error("非管理员只能删除待审批的请假记录");
        }

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
            @RequestParam(required = false) String approvalComment,
            HttpServletRequest request) {

        LeaveRequest leaveRequest = leaveRequestService.getById(id);
        if (leaveRequest == null) {
            return Result.error("请假记录不存在");
        }

        if (!"待审批".equals(leaveRequest.getStatus())) {
            return Result.error("当前请假状态不允许审批");
        }

        User currentUser = currentUserService.requireUser(request);
        if (!currentUserService.canApproveEmployee(currentUser, leaveRequest.getEmployeeId())) {
            return Result.error("只有管理员或直属上级才能审批请假");
        }

        leaveRequest.setStatus("已批准");
        leaveRequest.setApprover(getDisplayName(currentUser));
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
            @RequestParam(required = false) String approvalComment,
            HttpServletRequest request) {

        LeaveRequest leaveRequest = leaveRequestService.getById(id);
        if (leaveRequest == null) {
            return Result.error("请假记录不存在");
        }

        if (!"待审批".equals(leaveRequest.getStatus())) {
            return Result.error("当前请假状态不允许审批");
        }

        User currentUser = currentUserService.requireUser(request);
        if (!currentUserService.canApproveEmployee(currentUser, leaveRequest.getEmployeeId())) {
            return Result.error("只有管理员或直属上级才能审批请假");
        }

        leaveRequest.setStatus("已拒绝");
        leaveRequest.setApprover(getDisplayName(currentUser));
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

    private void fillRequester(LeaveRequest leaveRequest, Employee employee) {
        leaveRequest.setEmployeeId(employee.getId());
        leaveRequest.setEmployeeName(employee.getName());
        leaveRequest.setDepartment(employee.getDepartment());
    }

    private void fillLeaveDays(LeaveRequest leaveRequest) {
        if (leaveRequest.getStartDate() != null && leaveRequest.getEndDate() != null) {
            if (leaveRequest.getEndDate().isBefore(leaveRequest.getStartDate())) {
                throw new IllegalArgumentException("结束日期不能早于开始日期");
            }
            leaveRequest.setDays((int) ChronoUnit.DAYS.between(leaveRequest.getStartDate(), leaveRequest.getEndDate()) + 1);
        }
    }

    private String getDisplayName(User user) {
        return user.getRealName() != null && !user.getRealName().isEmpty() ? user.getRealName() : user.getUsername();
    }
}
