package com.example.employee.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "通用返回结果")
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "是否成功")
    private Boolean success;

    @ApiModelProperty(value = "状态码")
    private Integer code;

    @ApiModelProperty(value = "返回消息")
    private String message;

    @ApiModelProperty(value = "返回数据")
    private T data;

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setSuccess(true);
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(ResultCode.SUCCESS.getMessage());
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error() {
        return error(ResultCode.ERROR.getMessage());
    }

    public static <T> Result<T> error(String message) {
        return error(ResultCode.ERROR.getCode(), message);
    }

    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
} 