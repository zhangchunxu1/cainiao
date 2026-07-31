package com.example.employee.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.employee.common.Result;
import com.example.employee.entity.DailyReport;
import com.example.employee.entity.Employee;
import com.example.employee.entity.User;
import com.example.employee.service.CurrentUserService;
import com.example.employee.service.DailyReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Api(tags = "日报管理")
@RestController
@RequestMapping("/daily-reports")
@RequiredArgsConstructor
public class DailyReportController {

    private final DailyReportService dailyReportService;
    private final CurrentUserService currentUserService;

    @ApiOperation("获取日报列表")
    @GetMapping
    public Result<IPage<DailyReport>> getDailyReportList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) String status,
            HttpServletRequest request) {
        User currentUser = currentUserService.requireUser(request);
        if (employeeId != null && !currentUserService.canAccessEmployee(currentUser, employeeId)) {
            return Result.error("没有权限查看该员工日报");
        }
        IPage<DailyReport> result = dailyReportService.getDailyReportList(
                page, pageSize, keyword, employeeId, status, currentUserService.getAccessibleEmployeeIds(currentUser));
        return Result.success(result);
    }

    @ApiOperation("根据ID获取日报详情")
    @GetMapping("/{id}")
    public Result<DailyReport> getDailyReportById(@PathVariable Long id, HttpServletRequest request) {
        DailyReport dailyReport = dailyReportService.getById(id);
        if (dailyReport == null) {
            return Result.error("日报记录不存在");
        }
        User currentUser = currentUserService.requireUser(request);
        if (!currentUserService.canAccessEmployee(currentUser, dailyReport.getEmployeeId())) {
            return Result.error("没有权限查看该日报");
        }
        return Result.success(dailyReport);
    }

    @ApiOperation("提交日报")
    @PostMapping
    public Result<DailyReport> addDailyReport(@Valid @RequestBody DailyReport dailyReport, HttpServletRequest request) {
        User currentUser = currentUserService.requireUser(request);
        Employee currentEmployee = currentUserService.requireEmployee(currentUser);
        dailyReport.setEmployeeId(currentEmployee.getId());
        dailyReport.setEmployeeName(currentEmployee.getName());
        dailyReport.setDepartment(currentEmployee.getDepartment());
        dailyReport.setStatus("已提交");
        dailyReport.setReviewer(null);
        dailyReport.setReviewComment(null);
        dailyReport.setReviewTime(null);
        boolean success = dailyReportService.save(dailyReport);
        if (success) {
            return Result.success(dailyReport);
        }
        return Result.error("提交失败");
    }

    @ApiOperation("更新日报")
    @PutMapping("/{id}")
    public Result<DailyReport> updateDailyReport(@PathVariable Long id, @Valid @RequestBody DailyReport dailyReport, HttpServletRequest request) {
        DailyReport existing = dailyReportService.getById(id);
        if (existing == null) {
            return Result.error("日报记录不存在");
        }
        if (!"已提交".equals(existing.getStatus())) {
            return Result.error("只有未审核的日报可以修改");
        }
        User currentUser = currentUserService.requireUser(request);
        Employee currentEmployee = currentUserService.getEmployee(currentUser);
        boolean owner = currentEmployee != null && existing.getEmployeeId().equals(currentEmployee.getId());
        if (!currentUserService.isAdmin(currentUser) && !owner) {
            return Result.error("只有本人或管理员可以修改日报");
        }

        existing.setReportDate(dailyReport.getReportDate());
        existing.setTodayWork(dailyReport.getTodayWork());
        existing.setTomorrowWork(dailyReport.getTomorrowWork());
        existing.setIssues(dailyReport.getIssues());
        boolean success = dailyReportService.updateById(existing);
        if (success) {
            return Result.success(existing);
        }
        return Result.error("更新失败");
    }

    @ApiOperation("批量删除日报")
    @DeleteMapping("/batch")
    public Result<Integer> batchDeleteDailyReport(@RequestBody java.util.List<Long> ids, HttpServletRequest request) {
        if (ids == null || ids.isEmpty()) {
            return Result.error("请选择要删除的记录");
        }
        User currentUser = currentUserService.requireUser(request);
        int deletedCount = 0;
        java.util.List<String> errors = new java.util.ArrayList<>();
        for (Long id : ids) {
            DailyReport dailyReport = dailyReportService.getById(id);
            if (dailyReport == null) {
                continue;
            }
            if (!currentUserService.canAccessEmployee(currentUser, dailyReport.getEmployeeId())) {
                errors.add("日报" + id + "：没有权限删除");
                continue;
            }
            if (!currentUserService.isAdmin(currentUser) && !"已提交".equals(dailyReport.getStatus())) {
                errors.add("日报" + id + "：非管理员只能删除未审核的日报");
                continue;
            }
            if (dailyReportService.removeById(id)) {
                deletedCount++;
            }
        }
        if (deletedCount > 0) {
            return Result.success(deletedCount);
        }
        return Result.error(errors.isEmpty() ? "批量删除失败" : String.join("；", errors));
    }

    @ApiOperation("删除日报")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteDailyReport(@PathVariable Long id, HttpServletRequest request) {
        DailyReport dailyReport = dailyReportService.getById(id);
        if (dailyReport == null) {
            return Result.error("日报记录不存在");
        }

        User currentUser = currentUserService.requireUser(request);
        if (!currentUserService.canAccessEmployee(currentUser, dailyReport.getEmployeeId())) {
            return Result.error("只有管理员、直属上级或本人才能删除日报");
        }
        if (!currentUserService.isAdmin(currentUser) && !"已提交".equals(dailyReport.getStatus())) {
            return Result.error("非管理员只能删除未审核的日报");
        }

        boolean success = dailyReportService.removeById(id);
        if (success) {
            return Result.success(true);
        }
        return Result.error("删除失败");
    }

    @ApiOperation("审核日报")
    @PutMapping("/{id}/review")
    public Result<DailyReport> reviewDailyReport(
            @PathVariable Long id,
            @RequestBody(required = false) java.util.Map<String, String> body,
            HttpServletRequest request) {

        DailyReport dailyReport = dailyReportService.getById(id);
        if (dailyReport == null) {
            return Result.error("日报记录不存在");
        }

        if (!"已提交".equals(dailyReport.getStatus())) {
            return Result.error("当前日报状态不允许审核");
        }

        User currentUser = currentUserService.requireUser(request);
        if (!currentUserService.canApproveEmployee(currentUser, dailyReport.getEmployeeId())) {
            return Result.error("只有管理员或直属上级才能审批日报");
        }

        dailyReport.setStatus("已审核");
        if (body != null) {
            if (body.get("reviewComment") != null) {
                dailyReport.setReviewComment(body.get("reviewComment"));
            }
        }
        dailyReport.setReviewer(getDisplayName(currentUser));
        dailyReport.setReviewTime(java.time.LocalDateTime.now());

        boolean success = dailyReportService.updateById(dailyReport);
        if (success) {
            return Result.success(dailyReport);
        }
        return Result.error("审核失败");
    }

    private String getDisplayName(User user) {
        return user.getRealName() != null && !user.getRealName().isEmpty() ? user.getRealName() : user.getUsername();
    }
}
