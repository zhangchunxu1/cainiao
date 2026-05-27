package com.example.employee.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("department")
@ApiModel(description = "部门实体")
public class Department implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "部门ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "部门名称")
    @NotBlank(message = "部门名称不能为空")
    @Size(max = 50, message = "部门名称长度不能超过50个字符")
    private String name;

    @ApiModelProperty(value = "负责人")
    private String manager;

    @ApiModelProperty(value = "联系电话")
    private String phone;

    @ApiModelProperty(value = "描述")
    @Size(max = 200, message = "描述长度不能超过200个字符")
    private String description;

    @ApiModelProperty(value = "员工人数")
    private Integer employeeCount;

    @ApiModelProperty(value = "状态（0-正常，1-停用）")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
}