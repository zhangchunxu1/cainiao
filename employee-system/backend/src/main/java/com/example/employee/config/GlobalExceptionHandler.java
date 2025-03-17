package com.example.employee.config;

import com.example.employee.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error("系统异常，请联系管理员");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        StringBuilder sb = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            sb.append(fieldError.getField()).append(": ").append(fieldError.getDefaultMessage()).append(", ");
        }
        String msg = sb.toString();
        if (msg.length() > 2) {
            msg = msg.substring(0, msg.length() - 2);
        }
        return Result.error(msg);
    }

    @ExceptionHandler(BindException.class)
    public Result<String> handleBindException(BindException e) {
        BindingResult bindingResult = e.getBindingResult();
        StringBuilder sb = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            sb.append(fieldError.getField()).append(": ").append(fieldError.getDefaultMessage()).append(", ");
        }
        String msg = sb.toString();
        if (msg.length() > 2) {
            msg = msg.substring(0, msg.length() - 2);
        }
        return Result.error(msg);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Result<String> handleConstraintViolationException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        StringBuilder sb = new StringBuilder();
        for (ConstraintViolation<?> violation : violations) {
            sb.append(violation.getMessage()).append(", ");
        }
        String msg = sb.toString();
        if (msg.length() > 2) {
            msg = msg.substring(0, msg.length() - 2);
        }
        return Result.error(msg);
    }
} 