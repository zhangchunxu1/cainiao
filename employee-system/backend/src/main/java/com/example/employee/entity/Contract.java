package com.example.employee.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("contract")
public class Contract {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("contract_no")
    private String contractNo;

    @TableField("contract_name")
    private String contractName;

    @TableField("party_a")
    private String partyA;

    @TableField("party_b")
    private String partyB;

    @TableField("sign_date")
    private LocalDate signDate;

    @TableField("start_date")
    private LocalDate startDate;

    @TableField("end_date")
    private LocalDate endDate;

    @TableField("contract_amount")
    private BigDecimal contractAmount;

    @TableField("currency")
    private String currency;

    @TableField("contract_type")
    private String contractType;

    @TableField("status")
    private String status;

    @TableField("employee_id")
    private Long employeeId;

    @TableField("employee_name")
    private String employeeName;

    @TableField("department")
    private String department;

    @TableField("contract_content")
    private String contractContent;

    @TableField("attachments")
    private String attachments;

    @TableField("remark")
    private String remark;

    @TableField("created_time")
    private LocalDateTime createdTime;

    @TableField("updated_time")
    private LocalDateTime updatedTime;
}