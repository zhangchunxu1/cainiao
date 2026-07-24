package com.example.employee.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.employee.entity.Employee;
import com.example.employee.entity.User;
import com.example.employee.util.JwtUtil;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CurrentUserService {

    private final UserService userService;
    private final EmployeeService employeeService;
    private final JwtUtil jwtUtil;

    public CurrentUserService(UserService userService, EmployeeService employeeService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.employeeService = employeeService;
        this.jwtUtil = jwtUtil;
    }

    public User requireUser(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new IllegalArgumentException("请先登录");
        }

        Long userId = jwtUtil.getUserIdFromToken(token.substring(7));
        User user = userService.getById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        return user;
    }

    public Employee getEmployee(User user) {
        if (user == null || user.getRealName() == null) {
            return null;
        }

        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getName, user.getRealName())
               .eq(Employee::getDeleted, 0)
               .last("LIMIT 1");
        return employeeService.getOne(wrapper);
    }

    public Employee requireEmployee(User user) {
        Employee employee = getEmployee(user);
        if (employee == null) {
            throw new IllegalArgumentException("当前账号未绑定员工，无法操作该业务");
        }
        return employee;
    }

    public boolean isAdmin(User user) {
        return user != null && "admin".equalsIgnoreCase(user.getRole());
    }

    public boolean canAccessEmployee(User user, Long employeeId) {
        if (isAdmin(user)) {
            return true;
        }
        Employee currentEmployee = getEmployee(user);
        if (currentEmployee == null || employeeId == null) {
            return false;
        }
        if (employeeId.equals(currentEmployee.getId())) {
            return true;
        }
        Employee targetEmployee = employeeService.getById(employeeId);
        return targetEmployee != null
                && targetEmployee.getManagerId() != null
                && targetEmployee.getManagerId().equals(currentEmployee.getId());
    }

    public boolean canApproveEmployee(User user, Long employeeId) {
        if (isAdmin(user)) {
            return true;
        }
        Employee currentEmployee = getEmployee(user);
        if (currentEmployee == null || employeeId == null || employeeId.equals(currentEmployee.getId())) {
            return false;
        }
        Employee targetEmployee = employeeService.getById(employeeId);
        return targetEmployee != null
                && targetEmployee.getManagerId() != null
                && targetEmployee.getManagerId().equals(currentEmployee.getId());
    }

    public List<Long> getAccessibleEmployeeIds(User user) {
        if (isAdmin(user)) {
            return null;
        }

        Employee currentEmployee = getEmployee(user);
        if (currentEmployee == null) {
            return new ArrayList<>();
        }

        List<Long> employeeIds = new ArrayList<>();
        employeeIds.add(currentEmployee.getId());

        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getManagerId, currentEmployee.getId())
               .eq(Employee::getDeleted, 0);
        employeeIds.addAll(employeeService.list(wrapper).stream()
                .map(Employee::getId)
                .collect(Collectors.toList()));

        return employeeIds;
    }
}
