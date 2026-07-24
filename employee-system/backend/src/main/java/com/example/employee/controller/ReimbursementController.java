package com.example.employee.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.employee.common.Result;
import com.example.employee.entity.Employee;
import com.example.employee.entity.Reimbursement;
import com.example.employee.entity.User;
import com.example.employee.service.CurrentUserService;
import com.example.employee.service.ReimbursementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Map;

@Api(tags = "报销管理")
@RestController
@RequestMapping("/reimbursements")
public class ReimbursementController {

    @Resource
    private ReimbursementService reimbursementService;

    @Resource
    private CurrentUserService currentUserService;

    @ApiOperation("获取报销列表")
    @GetMapping
    public Result<IPage<Reimbursement>> getReimbursementList(
            @ApiParam(value = "页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam(value = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam(value = "搜索关键字") @RequestParam(required = false) String keyword,
            HttpServletRequest request) {
        User currentUser = currentUserService.requireUser(request);
        IPage<Reimbursement> reimbursementPage = reimbursementService.getReimbursementList(
                page, pageSize, keyword, currentUserService.getAccessibleEmployeeIds(currentUser));
        return Result.success(reimbursementPage);
    }

    @ApiOperation("获取报销详情")
    @GetMapping("/{id}")
    public Result<Reimbursement> getReimbursementById(
            @ApiParam(value = "报销ID", required = true) @PathVariable Long id,
            HttpServletRequest request) {
        Reimbursement reimbursement = reimbursementService.getById(id);
        if (reimbursement == null) {
            return Result.error(404, "报销不存在");
        }
        User currentUser = currentUserService.requireUser(request);
        if (!currentUserService.canAccessEmployee(currentUser, reimbursement.getEmployeeId())) {
            return Result.error("没有权限查看该报销");
        }
        return Result.success(reimbursement);
    }

    @ApiOperation("创建报销")
    @PostMapping
    public Result<Reimbursement> createReimbursement(
            @ApiParam(value = "报销信息", required = true) @RequestBody Reimbursement reimbursement,
            HttpServletRequest request) {
        User currentUser = currentUserService.requireUser(request);
        Employee currentEmployee = currentUserService.requireEmployee(currentUser);
        reimbursement.setEmployeeId(currentEmployee.getId());
        reimbursement.setEmployeeName(currentEmployee.getName());
        reimbursement.setDepartment(currentEmployee.getDepartment());
        reimbursement.setStatus("待审批");
        reimbursement.setApplyDate(LocalDate.now().toString());
        reimbursement.setDeleted(0);
        reimbursement.setManagerApproveDate(null);
        reimbursement.setManagerApprover(null);
        reimbursement.setManagerRemark(null);
        reimbursement.setFinanceApproveDate(null);
        reimbursement.setFinanceApprover(null);
        reimbursement.setFinanceRemark(null);
        if (reimbursement.getReimbursementNo() == null || reimbursement.getReimbursementNo().isEmpty()) {
            reimbursement.setReimbursementNo("BX-" + System.currentTimeMillis());
        }
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
            @ApiParam(value = "报销信息", required = true) @RequestBody Reimbursement reimbursement,
            HttpServletRequest request) {
        Reimbursement existing = reimbursementService.getById(id);
        if (existing == null) {
            return Result.error(404, "报销不存在");
        }
        if (!"待审批".equals(existing.getStatus())) {
            return Result.error("只有待审批的报销可以修改");
        }
        User currentUser = currentUserService.requireUser(request);
        Employee currentEmployee = currentUserService.getEmployee(currentUser);
        boolean owner = currentEmployee != null && existing.getEmployeeId().equals(currentEmployee.getId());
        if (!currentUserService.isAdmin(currentUser) && !owner) {
            return Result.error("只有本人或管理员可以修改报销");
        }
        existing.setType(reimbursement.getType());
        existing.setAmount(reimbursement.getAmount());
        existing.setReason(reimbursement.getReason());
        existing.setRemark(reimbursement.getRemark());
        boolean success = reimbursementService.updateById(existing);
        if (success) {
            return Result.success(existing);
        }
        return Result.error("更新报销失败");
    }

    @ApiOperation("删除报销")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteReimbursement(
            @ApiParam(value = "报销ID", required = true) @PathVariable Long id,
            HttpServletRequest request) {
        Reimbursement reimbursement = reimbursementService.getById(id);
        if (reimbursement == null) {
            return Result.error("报销不存在");
        }
        User currentUser = currentUserService.requireUser(request);
        if (!currentUserService.canAccessEmployee(currentUser, reimbursement.getEmployeeId())) {
            return Result.error("没有权限删除该报销");
        }
        if (!currentUserService.isAdmin(currentUser) && !"待审批".equals(reimbursement.getStatus())) {
            return Result.error("非管理员只能删除待审批的报销");
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
            @ApiParam(value = "审批信息", required = true) @RequestBody Map<String, String> body,
            HttpServletRequest request) {
        try {
            Reimbursement existing = reimbursementService.getById(id);
            if (existing == null) {
                return Result.error("报销不存在");
            }
            User currentUser = currentUserService.requireUser(request);
            if (!currentUserService.canApproveEmployee(currentUser, existing.getEmployeeId())) {
                return Result.error("只有管理员或直属上级才能审批报销");
            }
            String remark = body == null ? null : body.get("remark");
            Reimbursement reimbursement = reimbursementService.managerApprove(id, getDisplayName(currentUser), remark);
            return Result.success(reimbursement);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("部门经理驳回")
    @PostMapping("/{id}/manager-reject")
    public Result<Reimbursement> managerReject(
            @ApiParam(value = "报销ID", required = true) @PathVariable Long id,
            @ApiParam(value = "审批信息", required = true) @RequestBody Map<String, String> body,
            HttpServletRequest request) {
        try {
            Reimbursement existing = reimbursementService.getById(id);
            if (existing == null) {
                return Result.error("报销不存在");
            }
            User currentUser = currentUserService.requireUser(request);
            if (!currentUserService.canApproveEmployee(currentUser, existing.getEmployeeId())) {
                return Result.error("只有管理员或直属上级才能驳回报销");
            }
            String remark = body == null ? null : body.get("remark");
            Reimbursement reimbursement = reimbursementService.managerReject(id, getDisplayName(currentUser), remark);
            return Result.success(reimbursement);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("财务审批通过")
    @PostMapping("/{id}/finance-approve")
    public Result<Reimbursement> financeApprove(
            @ApiParam(value = "报销ID", required = true) @PathVariable Long id,
            @ApiParam(value = "审批信息", required = true) @RequestBody Map<String, String> body,
            HttpServletRequest request) {
        try {
            User currentUser = currentUserService.requireUser(request);
            if (!currentUserService.isAdmin(currentUser) && !"finance".equalsIgnoreCase(currentUser.getRole())) {
                return Result.error("只有管理员或财务人员才能进行财务审批");
            }
            String remark = body == null ? null : body.get("remark");
            Reimbursement reimbursement = reimbursementService.financeApprove(id, getDisplayName(currentUser), remark);
            return Result.success(reimbursement);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("财务驳回")
    @PostMapping("/{id}/finance-reject")
    public Result<Reimbursement> financeReject(
            @ApiParam(value = "报销ID", required = true) @PathVariable Long id,
            @ApiParam(value = "审批信息", required = true) @RequestBody Map<String, String> body,
            HttpServletRequest request) {
        try {
            User currentUser = currentUserService.requireUser(request);
            if (!currentUserService.isAdmin(currentUser) && !"finance".equalsIgnoreCase(currentUser.getRole())) {
                return Result.error("只有管理员或财务人员才能进行财务驳回");
            }
            String remark = body == null ? null : body.get("remark");
            Reimbursement reimbursement = reimbursementService.financeReject(id, getDisplayName(currentUser), remark);
            return Result.success(reimbursement);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    private String getDisplayName(User user) {
        return user.getRealName() != null && !user.getRealName().isEmpty() ? user.getRealName() : user.getUsername();
    }
}
