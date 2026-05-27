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
@TableName("announcement")
@ApiModel(description = "公告实体")
public class Announcement implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "公告ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "公告标题")
    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200个字符")
    private String title;

    @ApiModelProperty(value = "公告内容")
    @NotBlank(message = "内容不能为空")
    private String content;

    @ApiModelProperty(value = "发布人")
    private String publisher;

    @ApiModelProperty(value = "类型（通知、公告、紧急）")
    private String type;

    @ApiModelProperty(value = "状态（0-草稿，1-已发布）")
    private Integer status;

    @ApiModelProperty(value = "是否置顶")
    private Integer isTop;

    @ApiModelProperty(value = "浏览次数")
    private Integer viewCount;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;
}