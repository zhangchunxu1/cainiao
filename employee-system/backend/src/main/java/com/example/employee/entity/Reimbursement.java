package com.example.employee.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("reimbursement")
public class Reimbursement {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long employeeId;

    private String employeeName;

    private String department;

    private String reimbursementNo;

    private String type;

    private BigDecimal amount;

    private String reason;

    private String status;

    private String applyDate;

    private String managerApproveDate;

    private String managerApprover;

    private String managerRemark;

    private String financeApproveDate;

    private String financeApprover;

    private String financeRemark;

    private String remark;

    private Integer deleted;
}