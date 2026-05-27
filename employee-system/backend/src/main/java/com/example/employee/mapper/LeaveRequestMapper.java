package com.example.employee.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.employee.entity.LeaveRequest;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LeaveRequestMapper extends BaseMapper<LeaveRequest> {
}