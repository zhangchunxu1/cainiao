package com.example.employee.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.employee.common.Result;
import com.example.employee.entity.Employee;
import com.example.employee.entity.LeaveRequest;
import com.example.employee.entity.User;
import com.example.employee.service.EmployeeService;
import com.example.employee.service.LeaveRequestService;
import com.example.employee.service.UserService;
import com.example.employee.util.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Api(tags = "请假管理")
@RestController
@RequestMapping("/leaves")
@RequiredArgsConstructor
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;
    private final UserService userService;
    private final EmployeeService employeeService;
    private final JwtUtil jwtUtil;

    @ApiOperation("获取请假列表")
    @GetMapping
    public Result<IPage<LeaveRequest>> getLeaveRequestList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            HttpServletRequest request) {
        
        String token = request.getHeader("Authorization");
        String role = "employee";
        String employeeName = null;
        
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String username = jwtUtil.getUsernameFromToken(token);
            User currentUser = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
            if (currentUser != null) {
                role = currentUser.getRole();
                employeeName = currentUser.getRealName();
            }
        }
        
        IPage<LeaveRequest> result = leaveRequestService.getLeaveRequestListWithPermission(page, pageSize, keyword, status, role, employeeName);
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
    public Result<Boolean> deleteLeaveRequest(@PathVariable Long id, HttpServletRequest request) {
        LeaveRequest leaveRequest = leaveRequestService.getById(id);
        if (leaveRequest == null) {
            return Result.error("请假记录不存在");
        }

        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.error("请先登录");
        }
        token = token.substring(7);
        
        Long currentUserId = jwtUtil.getUserIdFromToken(token);
        User currentUser = userService.getById(currentUserId);
        
        if (currentUser == null) {
            return Result.error("用户不存在");
        }
        
        boolean canDelete = false;
        
        if ("admin".equals(currentUser.getRole())) {
            canDelete = true;
        } else if (leaveRequest.getEmployeeId().equals(currentUserId)) {
            canDelete = true;
        } else {
            Employee requester = employeeService.getById(leaveRequest.getEmployeeId());
            if (requester != null && requester.getManagerId() != null && requester.getManagerId().equals(currentUserId)) {
                canDelete = true;
            }
        }
        
        if (!canDelete) {
            return Result.error("只有管理员、直属上级或本人才能删除请假记录");
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

        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.error("请先登录");
        }
        token = token.substring(7);
        
        Long currentUserId = jwtUtil.getUserIdFromToken(token);
        User currentUser = userService.getById(currentUserId);
        
        if (currentUser == null) {
            return Result.error("用户不存在");
        }
        
        boolean canApprove = false;
        
        Employee requester = employeeService.getById(leaveRequest.getEmployeeId());
        
        if ("admin".equals(currentUser.getRole()) || "manager".equals(currentUser.getRole())) {
            canApprove = true;
        } else {
            if (requester != null) {
                if (requester.getName().equals(currentUser.getRealName())) {
                    return Result.error("不能审批自己的请假申请");
                }
                LambdaQueryWrapper<Employee> empWrapper = new LambdaQueryWrapper<>();
                empWrapper.eq(Employee::getName, currentUser.getRealName());
                List<Employee> currentEmployees = employeeService.list(empWrapper);
                if (!currentEmployees.isEmpty()) {
                    Employee currentEmployee = currentEmployees.get(0);
                    if (requester.getManagerId() != null && requester.getManagerId().equals(currentEmployee.getId())) {
                        canApprove = true;
                    }
                }
            }
        }
        
        if (!canApprove) {
            return Result.error("只有管理员或直属上级才能审批请假");
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
            @RequestParam(required = false) String approvalComment,
            HttpServletRequest request) {

        LeaveRequest leaveRequest = leaveRequestService.getById(id);
        if (leaveRequest == null) {
            return Result.error("请假记录不存在");
        }

        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.error("请先登录");
        }
        token = token.substring(7);
        
        Long currentUserId = jwtUtil.getUserIdFromToken(token);
        User currentUser = userService.getById(currentUserId);
        
        if (currentUser == null) {
            return Result.error("用户不存在");
        }
        
        boolean canReject = false;
        
        Employee requester = employeeService.getById(leaveRequest.getEmployeeId());
        
        if ("admin".equals(currentUser.getRole()) || "manager".equals(currentUser.getRole())) {
            canReject = true;
        } else {
            if (requester != null) {
                if (requester.getName().equals(currentUser.getRealName())) {
                    return Result.error("不能审批自己的请假申请");
                }
                LambdaQueryWrapper<Employee> empWrapper = new LambdaQueryWrapper<>();
                empWrapper.eq(Employee::getName, currentUser.getRealName());
                List<Employee> currentEmployees = employeeService.list(empWrapper);
                if (!currentEmployees.isEmpty()) {
                    Employee currentEmployee = currentEmployees.get(0);
                    if (requester.getManagerId() != null && requester.getManagerId().equals(currentEmployee.getId())) {
                        canReject = true;
                    }
                }
            }
        }
        
        if (!canReject) {
            return Result.error("只有管理员或直属上级才能审批请假");
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