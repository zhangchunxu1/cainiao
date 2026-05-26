package com.example.employee.controller;

import com.example.employee.common.Result;
import com.example.employee.entity.User;
import com.example.employee.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Api(tags = "认证管理")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestBody User user) {
        try {
            String token = authService.login(user);
            Map<String, String> data = new HashMap<>();
            data.put("token", token);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}