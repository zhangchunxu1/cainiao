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
@TableName("leave_request")
@ApiModel(description = "请假实体")
public class LeaveRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "请假ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "员工ID")
    private Long employeeId;

    @ApiModelProperty(value = "员工姓名")
    private String employeeName;

    @ApiModelProperty(value = "部门")
    private String department;

    @ApiModelProperty(value = "请假类型（事假、病假、年假等）")
    @NotBlank(message = "请假类型不能为空")
    private String leaveType;

    @ApiModelProperty(value = "开始日期")
    private LocalDate startDate;

    @ApiModelProperty(value = "结束日期")
    private LocalDate endDate;

    @ApiModelProperty(value = "请假天数")
    private Integer days;

    @ApiModelProperty(value = "请假原因")
    @NotBlank(message = "请假原因不能为空")
    private String reason;

    @ApiModelProperty(value = "状态（待审批、已批准、已拒绝）")
    private String status;

    @ApiModelProperty(value = "审批人")
    private String approver;

    @ApiModelProperty(value = "审批意见")
    private String approvalComment;

    @ApiModelProperty(value = "审批时间")
    private LocalDateTime approvalTime;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
}