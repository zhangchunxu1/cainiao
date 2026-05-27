package com.example.employee.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@TableName("attendance")
@ApiModel(description = "考勤实体")
public class Attendance implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "考勤ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "员工ID")
    private Long employeeId;

    @ApiModelProperty(value = "员工姓名")
    private String employeeName;

    @ApiModelProperty(value = "日期")
    private LocalDate date;

    @ApiModelProperty(value = "签到时间")
    private LocalTime checkInTime;

    @ApiModelProperty(value = "签退时间")
    private LocalTime checkOutTime;

    @ApiModelProperty(value = "工作时长（小时）")
    private String workHours;

    @ApiModelProperty(value = "状态（正常、迟到、早退、缺卡）")
    private String status;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
}