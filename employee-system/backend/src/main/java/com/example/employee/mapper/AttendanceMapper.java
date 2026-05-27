package com.example.employee.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.employee.entity.Attendance;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AttendanceMapper extends BaseMapper<Attendance> {
}