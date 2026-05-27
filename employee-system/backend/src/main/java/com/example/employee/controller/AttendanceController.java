package com.example.employee.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.employee.common.Result;
import com.example.employee.entity.Attendance;
import com.example.employee.service.AttendanceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
            @RequestParam(required = false) String keyword) {
        
        IPage<Attendance> result = attendanceService.getAttendanceList(page, pageSize, keyword);
        return Result.success(result);
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
        if (success) {
            return Result.success(attendance);
        }
        return Result.error("添加失败");
    }

    @ApiOperation("更新考勤记录")
    @PutMapping("/{id}")
    public Result<Attendance> updateAttendance(@PathVariable Long id, @Valid @RequestBody Attendance attendance) {
        attendance.setId(id);
        boolean success = attendanceService.updateById(attendance);
        if (success) {
            return Result.success(attendance);
        }
        return Result.error("更新失败");
    }

    @ApiOperation("删除考勤记录")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteAttendance(@PathVariable Long id) {
        boolean success = attendanceService.removeById(id);
        if (success) {
            return Result.success(true);
        }
        return Result.error("删除失败");
    }

    @ApiOperation("签到")
    @PostMapping("/checkin")
    public Result<Attendance> checkIn(@RequestBody Attendance attendance) {
        attendance.setCheckInTime(java.time.LocalTime.now());
        attendance.setStatus("正常");
        boolean success = attendanceService.save(attendance);
        if (success) {
            return Result.success(attendance);
        }
        return Result.error("签到失败");
    }

    @ApiOperation("签退")
    @PostMapping("/checkout")
    public Result<Attendance> checkOut(@RequestBody Attendance attendance) {
        attendance.setCheckOutTime(java.time.LocalTime.now());
        boolean success = attendanceService.updateById(attendance);
        if (success) {
            return Result.success(attendance);
        }
        return Result.error("签退失败");
    }
}