package com.example.employee.controller;

/*
  AuthController.java - 认证控制器
  ================================
  
  功能说明：
  - 处理用户认证相关的HTTP请求
  - 提供登录接口（生成JWT Token）
  - 作为系统的"大门"，验证用户身份
  
  API端点：
    POST /auth/login - 用户登录，返回JWT Token
    
  请求/响应格式：
    请求：
      POST /auth/login
      Content-Type: application/json
      
      {
        "username": "admin",
        "password": "admin123"
      }
    
    响应（成功）：
      {
        "success": true,
        "code": 200,
        "message": "操作成功",
        "data": {
          "token": "eyJhbGciOiJIUzI1NiJ9..."
        }
      }
    
    响应（失败）：
      {
        "success": false,
        "code": 500,
        "message": "用户名或密码错误",
        "data": null
      }
  
  安全性考虑：
  - 密码在传输前应该使用HTTPS加密
  - 登录失败次数限制（防止暴力破解）
  - Token有效期设置（建议30分钟-2小时）
  
  为什么单独创建AuthController？
  - 职责单一原则：只负责认证相关逻辑
  - 易于维护：认证逻辑集中管理
  - 安全配置：可以单独配置安全策略
*/

import com.example.employee.common.Result;
import com.example.employee.entity.User;
import com.example.employee.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/* 
  @Api：Swagger注解，标记这是一个API控制器
  tags = "认证管理"：在Swagger文档中显示的分组名称
  作用：方便开发人员查看和测试API
*/
@Api(tags = "认证管理")

/* 
  @RestController：组合注解 = @Controller + @ResponseBody
  @Controller：标记为Spring MVC控制器
  @ResponseBody：所有方法的返回值自动转换为JSON响应
  
  特点：
  - 不需要每个方法都加@ResponseBody
  - 返回对象自动序列化为JSON
  - 是RESTful API的标准写法
*/
@RestController

/* 
  @RequestMapping("/auth")：类级别的URL映射
  所有方法的URL都会以"/auth"为前缀
  
  例如：
    login()方法映射到 /auth/login
    如果有register()方法会映射到 /auth/register
    
  为什么用/auth？
    - 符合RESTful规范（名词复数）
    - 清晰表达这是认证相关的API
    - 与其他API区分开（如/api/employees）
*/
@RequestMapping("/auth")

/* 
  @RequiredArgsConstructor：Lombok注解
  自动生成包含所有final字段的构造函数
  
  优点：
  - 简化构造器注入代码
  - 避免手动编写大量样板代码
  - 保证依赖不可变（final）
  
  生成的代码等同于：
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
  
  使用条件：
    - 字段必须是final的
    - 需要Lombok依赖
*/
@RequiredArgsConstructor
public class AuthController {

    /*
      注入AuthService（认证服务）
      
      final关键字的作用：
      1. 必须在构造时初始化
      2. 初始化后不能修改
      3. 保证线程安全
      
      AuthService的职责：
      - 验证用户名密码是否正确
      - 调用JwtUtil生成Token
      - 处理认证业务逻辑
    */
    private final AuthService authService;

    /* 
      用户登录接口
      
      @ApiOperation("用户登录")：Swagger注解
        在API文档中显示方法描述
        
      @PostMapping("/login")：
        HTTP方法：POST（安全，数据在请求体中）
        URL路径：/auth/login（完整的URL）
        
      为什么用POST而不是GET？
        - POST更安全（参数不在URL中）
        - POST可以发送大量数据
        - GET会被浏览器缓存，POST不会
        - RESTful规范：登录是"创建Token"的操作
    */
    @ApiOperation("用户登录")
    @PostMapping("/login")
    
    /* 
      方法签名说明
      
      返回值类型：Result<Map<String, String>>
        Result: 统一响应封装类
          - success: 操作是否成功
          - code: 状态码
          - message: 提示信息
          - data: 实际数据
        Map<String, String>: 存储JWT Token
          key: "token"
          value: JWT Token字符串
      
      参数：@RequestBody User user
        @RequestBody：从HTTP请求体中读取JSON并反序列化为User对象
        User user：包含username和password字段
        
        Spring自动完成JSON → Java对象的转换
        需要jackson-databind依赖（Spring Boot默认包含）
        
        要求前端发送的数据格式：
        Content-Type: application/json
        Body: {"username":"admin","password":"admin123"}
    */
    public Result<Map<String, Object>> login(@RequestBody User user) {

        try {
            Map<String, Object> result = authService.loginWithInfo(user);

            Map<String, Object> data = new HashMap<>();
            data.put("token", result.get("token"));
            data.put("role", result.get("role"));
            data.put("realName", result.get("realName"));
            data.put("userId", result.get("userId"));

            return Result.success(data);

        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
