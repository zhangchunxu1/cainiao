package com.example.employee.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.employee.common.Result;
import com.example.employee.entity.User;
import com.example.employee.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "账号管理")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    @ApiOperation("获取用户列表")
    @GetMapping
    public Result<IPage<User>> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        IPage<User> result = userService.getUserList(page, pageSize, keyword);
        return Result.success(result);
    }

    @ApiOperation("根据ID获取用户详情")
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setPassword(null);
        return Result.success(user);
    }

    @ApiOperation("添加用户")
    @PostMapping
    public Result<User> addUser(@Valid @RequestBody User user) {
        User existUser = userService.getUserByUsername(user.getUsername());
        if (existUser != null) {
            return Result.error("用户名已存在");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        boolean success = userService.save(user);
        if (success) {
            user.setPassword(null);
            return Result.success(user);
        }
        return Result.error("添加失败");
    }

    @ApiOperation("更新用户信息")
    @PutMapping("/{id}")
    public Result<User> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        User existingUser = userService.getById(id);
        if (existingUser == null) {
            return Result.error("用户不存在");
        }
        if (isProtectedAdmin(existingUser)) {
            if (!existingUser.getUsername().equals(user.getUsername())) {
                return Result.error("不能修改管理员账号用户名");
            }
            if (!"admin".equalsIgnoreCase(user.getRole())) {
                return Result.error("不能取消管理员账号权限");
            }
        }
        if (!existingUser.getUsername().equals(user.getUsername())) {
            User nameCheck = userService.getUserByUsername(user.getUsername());
            if (nameCheck != null) {
                return Result.error("用户名已存在");
            }
        }
        user.setId(id);
        if (user.getPassword() == null || user.getPassword().isEmpty() || user.getPassword().startsWith("$2a$")) {
            user.setPassword(existingUser.getPassword());
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        boolean success = userService.updateById(user);
        if (success) {
            user.setPassword(null);
            return Result.success(user);
        }
        return Result.error("更新失败");
    }

    @ApiOperation("批量删除用户")
    @DeleteMapping("/batch")
    public Result<Integer> batchDeleteUser(@RequestBody java.util.List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.error("请选择要删除的记录");
        }
        int deletedCount = 0;
        java.util.List<String> errors = new java.util.ArrayList<>();
        for (Long id : ids) {
            User user = userService.getById(id);
            if (user == null) {
                continue;
            }
            if (isProtectedAdmin(user)) {
                errors.add("用户" + user.getUsername() + "：不能删除管理员账号");
                continue;
            }
            if (userService.removeById(id)) {
                deletedCount++;
            }
        }
        if (deletedCount > 0) {
            return Result.success(deletedCount);
        }
        return Result.error(errors.isEmpty() ? "批量删除失败" : String.join("；", errors));
    }

    @ApiOperation("删除用户")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteUser(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        if (isProtectedAdmin(user)) {
            return Result.error("不能删除管理员账号");
        }
        boolean success = userService.removeById(id);
        if (success) {
            return Result.success(true);
        }
        return Result.error("删除失败");
    }

    @ApiOperation("重置密码")
    @PutMapping("/{id}/reset-password")
    public Result<Boolean> resetPassword(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setPassword(passwordEncoder.encode("123456"));
        boolean success = userService.updateById(user);
        if (success) {
            return Result.success(true);
        }
        return Result.error("重置失败");
    }

    private boolean isProtectedAdmin(User user) {
        return user != null
                && ("admin".equalsIgnoreCase(user.getUsername())
                || "admin".equalsIgnoreCase(user.getRole()));
    }
}
