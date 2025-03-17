package com.example.employee.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;
import java.io.Serializable;

@Data
@TableName("employee")
@ApiModel(description = "员工实体")
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "员工ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "姓名")
    @NotBlank(message = "姓名不能为空")
    @Size(max = 50, message = "姓名长度不能超过50个字符")
    private String name;

    @ApiModelProperty(value = "年龄")
    @NotNull(message = "年龄不能为空")
    @Min(value = 18, message = "年龄不能小于18岁")
    @Max(value = 100, message = "年龄不能大于100岁")
    private Integer age;

    @ApiModelProperty(value = "性别")
    @NotBlank(message = "性别不能为空")
    private String gender;

    @ApiModelProperty(value = "电话")
    @NotBlank(message = "电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号码格式不正确")
    private String phone;

    @ApiModelProperty(value = "邮箱")
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @ApiModelProperty(value = "部门")
    @NotBlank(message = "部门不能为空")
    private String department;

    @ApiModelProperty(value = "职位")
    @NotBlank(message = "职位不能为空")
    private String position;

    @ApiModelProperty(value = "入职日期")
    @NotBlank(message = "入职日期不能为空")
    private String hireDate;
} 