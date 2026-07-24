package com.example.employee.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.employee.common.Result;
import com.example.employee.entity.Attendance;
import com.example.employee.service.AttendanceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

@Api(tags = "考勤管理")
@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @ApiOperation("获取考勤列表")
    @GetMapping
    public Result<IPage<Attendance>> getAttendanceList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer current,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) String month) {

        Integer actualPage = current != null ? current : page;
        Integer actualSize = size != null ? size : pageSize;
        IPage<Attendance> result = attendanceService.getAttendanceList(actualPage, actualSize, keyword, employeeId, month);
        return Result.success(result);
    }

    @ApiOperation("获取今日考勤")
    @GetMapping("/today")
    public Result<Attendance> getTodayAttendance(@RequestParam Long employeeId) {
        Attendance attendance = getTodayByEmployeeId(employeeId);
        return Result.success(attendance);
    }

    @ApiOperation("根据ID获取考勤详情")
    @GetMapping("/{id}")
    public Result<Attendance> getAttendanceById(@PathVariable Long id) {
        Attendance attendance = attendanceService.getById(id);
        if (attendance == null) {
            return Result.error("考勤记录不存在");
        }
        return Result.success(attendance);
    }

    @ApiOperation("添加考勤记录")
    @PostMapping
    public Result<Attendance> addAttendance(@Valid @RequestBody Attendance attendance) {
        boolean success = attendanceService.save(attendance);
        return success ? Result.success(attendance) : Result.error("添加失败");
    }

    @ApiOperation("更新考勤记录")
    @PutMapping("/{id}")
    public Result<Attendance> updateAttendance(@PathVariable Long id, @Valid @RequestBody Attendance attendance) {
        attendance.setId(id);
        boolean success = attendanceService.updateById(attendance);
        return success ? Result.success(attendance) : Result.error("更新失败");
    }

    @ApiOperation("删除考勤记录")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteAttendance(@PathVariable Long id) {
        boolean success = attendanceService.removeById(id);
        return success ? Result.success(true) : Result.error("删除失败");
    }

    @ApiOperation("清理所有考勤数据")
    @DeleteMapping("/clear")
    public Result<Boolean> clearAllAttendance() {
        attendanceService.remove(null);
        return Result.success(true);
    }

    @ApiOperation("签到")
    @PostMapping("/checkin")
    public Result<Attendance> checkIn(@RequestBody(required = false) Attendance attendance) {
        if (attendance == null || attendance.getEmployeeId() == null) {
            return Result.error("缺少员工信息，无法签到");
        }

        Attendance existing = getTodayByEmployeeId(attendance.getEmployeeId());
        if (existing != null) {
            return Result.success(existing);
        }

        LocalTime now = LocalTime.now();
        attendance.setDate(LocalDate.now());
        attendance.setCheckInTime(now);
        attendance.setStatus(now.isAfter(LocalTime.of(9, 0)) ? "LATE" : "NORMAL");

        boolean success = attendanceService.save(attendance);
        return success ? Result.success(attendance) : Result.error("签到失败");
    }

    @ApiOperation("签退")
    @PostMapping("/checkout")
    public Result<Attendance> checkOut(@RequestBody(required = false) Attendance attendance) {
        if (attendance == null || attendance.getEmployeeId() == null) {
            return Result.error("缺少员工信息，无法签退");
        }

        Attendance existing = getTodayByEmployeeId(attendance.getEmployeeId());
        if (existing == null) {
            return Result.error("未找到今日签到记录");
        }

        LocalTime now = LocalTime.now();
        String status = existing.getStatus();
        if (now.isBefore(LocalTime.of(18, 0))) {
            status = "NORMAL".equals(status) ? "EARLY" : "LATE";
        }

        LocalTime checkIn = existing.getCheckInTime();
        if (checkIn != null) {
            long minutes = Duration.between(checkIn, now).toMinutes();
            existing.setWorkHours(String.format("%.2f", minutes / 60.0));
        }

        existing.setCheckOutTime(now);
        existing.setStatus(status);

        boolean success = attendanceService.updateById(existing);
        return success ? Result.success(existing) : Result.error("签退失败");
    }

    private Attendance getTodayByEmployeeId(Long employeeId) {
        LambdaQueryWrapper<Attendance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Attendance::getEmployeeId, employeeId)
               .eq(Attendance::getDate, LocalDate.now());
        return attendanceService.getOne(wrapper);
    }
}
