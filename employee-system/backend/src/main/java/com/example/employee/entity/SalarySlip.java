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
@TableName("salary_slip")
public class SalarySlip {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("employee_id")
    private Long employeeId;

    @TableField("employee_name")
    private String employeeName;

    @TableField("department")
    private String department;

    @TableField("pay_month")
    private LocalDate payMonth;

    @TableField("basic_salary")
    private BigDecimal basicSalary;

    @TableField("performance_bonus")
    private BigDecimal performanceBonus;

    @TableField("overtime_pay")
    private BigDecimal overtimePay;

    @TableField("allowance")
    private BigDecimal allowance;

    @TableField("total_income")
    private BigDecimal totalIncome;

    @TableField("social_insurance")
    private BigDecimal socialInsurance;

    @TableField("housing_fund")
    private BigDecimal housingFund;

    @TableField("tax")
    private BigDecimal tax;

    @TableField("total_deduction")
    private BigDecimal totalDeduction;

    @TableField("net_salary")
    private BigDecimal netSalary;

    @TableField("status")
    private String status;

    @TableField("remark")
    private String remark;

    @TableField("created_time")
    private LocalDateTime createdTime;

    @TableField("updated_time")
    private LocalDateTime updatedTime;
}