package com.example.employee.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.employee.common.Result;
import com.example.employee.entity.DailyReport;
import com.example.employee.entity.Employee;
import com.example.employee.entity.User;
import com.example.employee.service.DailyReportService;
import com.example.employee.service.EmployeeService;
import com.example.employee.service.UserService;
import com.example.employee.util.JwtUtil;
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
    private final EmployeeService employeeService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @ApiOperation("获取日报列表")
    @GetMapping
    public Result<IPage<DailyReport>> getDailyReportList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) String status) {
        
        IPage<DailyReport> result = dailyReportService.getDailyReportList(page, pageSize, keyword, employeeId, status);
        return Result.success(result);
    }

    @ApiOperation("根据ID获取日报详情")
    @GetMapping("/{id}")
    public Result<DailyReport> getDailyReportById(@PathVariable Long id) {
        DailyReport dailyReport = dailyReportService.getById(id);
        if (dailyReport == null) {
            return Result.error("日报记录不存在");
        }
        return Result.success(dailyReport);
    }

    @ApiOperation("提交日报")
    @PostMapping
    public Result<DailyReport> addDailyReport(@Valid @RequestBody DailyReport dailyReport) {
        dailyReport.setStatus("已提交");
        boolean success = dailyReportService.save(dailyReport);
        if (success) {
            return Result.success(dailyReport);
        }
        return Result.error("提交失败");
    }

    @ApiOperation("更新日报")
    @PutMapping("/{id}")
    public Result<DailyReport> updateDailyReport(@PathVariable Long id, @Valid @RequestBody DailyReport dailyReport) {
        dailyReport.setId(id);
        boolean success = dailyReportService.updateById(dailyReport);
        if (success) {
            return Result.success(dailyReport);
        }
        return Result.error("更新失败");
    }

    @ApiOperation("删除日报")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteDailyReport(@PathVariable Long id, HttpServletRequest request) {
        DailyReport dailyReport = dailyReportService.getById(id);
        if (dailyReport == null) {
            return Result.error("日报记录不存在");
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
        } else if (dailyReport.getEmployeeId().equals(currentUserId)) {
            canDelete = true;
        } else {
            Employee reporter = employeeService.getById(dailyReport.getEmployeeId());
            if (reporter != null && reporter.getManagerId() != null && reporter.getManagerId().equals(currentUserId)) {
                canDelete = true;
            }
        }
        
        if (!canDelete) {
            return Result.error("只有管理员、直属上级或本人才能删除日报");
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
        
        Employee reporter = employeeService.getById(dailyReport.getEmployeeId());
        
        boolean canReview = false;
        
        if ("admin".equals(currentUser.getRole())) {
            canReview = true;
        } else {
            if (reporter != null && reporter.getManagerId() != null) {
                canReview = reporter.getManagerId().equals(currentUserId);
            }
        }
        
        if (!canReview) {
            return Result.error("只有管理员或直属上级才能审批日报");
        }

        dailyReport.setStatus("已审核");
        if (body != null) {
            if (body.get("reviewer") != null) {
                dailyReport.setReviewer(body.get("reviewer"));
            }
            if (body.get("reviewComment") != null) {
                dailyReport.setReviewComment(body.get("reviewComment"));
            }
        }
        dailyReport.setReviewTime(java.time.LocalDateTime.now());

        boolean success = dailyReportService.updateById(dailyReport);
        if (success) {
            return Result.success(dailyReport);
        }
        return Result.error("审核失败");
    }
}