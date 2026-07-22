package com.example.employee.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("daily_report")
@ApiModel(description = "日报实体")
public class DailyReport implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "日报ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "员工ID")
    private Long employeeId;

    @ApiModelProperty(value = "员工姓名")
    private String employeeName;

    @ApiModelProperty(value = "部门")
    private String department;

    @ApiModelProperty(value = "日报日期")
    private LocalDate reportDate;

    @ApiModelProperty(value = "今日工作内容")
    @NotBlank(message = "今日工作内容不能为空")
    private String todayWork;

    @ApiModelProperty(value = "明日工作计划")
    private String tomorrowWork;

    @ApiModelProperty(value = "问题与困难")
    private String issues;

    @ApiModelProperty(value = "状态（已提交、已审核）")
    private String status;

    @ApiModelProperty(value = "审核人")
    private String reviewer;

    @ApiModelProperty(value = "审核意见")
    private String reviewComment;

    @ApiModelProperty(value = "审核时间")
    private LocalDateTime reviewTime;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
}