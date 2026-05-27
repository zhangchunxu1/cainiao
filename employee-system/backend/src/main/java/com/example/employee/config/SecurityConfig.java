package com.example.employee.config;

/*
  SecurityConfig.java - Spring Security 安全配置类
  =================================================
  
  功能说明：
  - 配置Spring Security的安全策略
  - 定义哪些URL需要认证，哪些可以公开访问
  - 配置JWT认证过滤器
  - 配置密码加密方式（BCrypt）
  - 配置CORS跨域和CSRF防护
  
  核心概念：
  - JWT (JSON Web Token)：无状态的认证令牌
  - BCrypt：安全的密码哈希算法
  - Stateless：无状态会话，不使用Session
  - Filter Chain：过滤器链，处理请求的各个阶段
  
  工作流程：
  1. 用户登录 → 获取JWT Token
  2. 后续请求携带Token → JwtAuthenticationFilter验证
  3. 验证通过 → 放行请求到Controller
  4. 验证失败 → 返回401未授权
*/

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// @Configuration：标记为配置类，Spring Boot启动时自动加载
@Configuration

// @EnableWebSecurity：启用Spring Security的Web安全功能
@EnableWebSecurity

/* 
  @EnableGlobalMethodSecurity：启用方法级别的安全控制
  prePostEnabled = true：允许使用@PreAuthorize和@PostAuthorize注解
  示例用法：
    @PreAuthorize("hasRole('ADMIN')")
    public void adminOnlyMethod() { ... }
*/
@EnableGlobalMethodSecurity(prePostEnabled = true)

/* 
  WebSecurityConfigurerAdapter：Spring Security的配置适配器
  继承此类可以自定义安全配置
  主要重写方法：
    configure(HttpSecurity http) - 配置HTTP安全策略
    configure(AuthenticationManagerBuilder auth) - 配置认证管理器
    configure(WebSecurity web) - 配置Web安全
*/
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /*
      创建JWT认证过滤器的Bean
      
      @Bean：将方法返回的对象注册为Spring容器中的Bean
      作用域：单例（默认）
      
      JwtAuthenticationFilter的作用：
      - 拦截所有HTTP请求
      - 从请求头中提取JWT Token
      - 验证Token的有效性（签名、过期时间等）
      - 将用户信息设置到SecurityContext中
      
      执行时机：在UsernamePasswordAuthenticationFilter之前执行
    */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    /*
      创建密码编码器的Bean
      
      BCryptPasswordEncoder的特点：
      - 单向哈希算法，不可逆解密
      - 自动加盐（salt），每次生成的哈希值都不同
      - 强度可调（默认10轮，最高31轮）
      - 抗彩虹表攻击
      
      使用示例：
        String encodedPwd = passwordEncoder.encode("rawPassword");
        boolean matches = passwordEncoder.matches("rawPassword", encodedPwd);
      
      为什么选择BCrypt？
      - MD5/SHA-1已被证明不安全
      - BCrypt专门为密码存储设计
      - 计算速度适中，既安全又不会太慢
    */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
      配置HTTP安全策略
      
      HttpSecurity对象用于配置Web层面的安全控制
      这是Spring Security最核心的配置方法
      
      参数说明：
        http: HttpSecurity构建器，采用链式调用风格
      异常说明：
        可能抛出Exception，表示配置错误
    */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        
        /* 
          第一步：基础配置
          
          cors(): 启用CORS（跨源资源共享）支持
            允许前端（不同域名/端口）访问后端API
            需要配合CorsConfiguration配置具体的跨域规则
            
          csrf().disable(): 禁用CSRF（跨站请求伪造）保护
            原因：我们使用JWT Token进行认证，不需要CSRF Token
            CSRF主要用于基于Cookie/Session的传统Web应用
            
          sessionManagement(): 会话管理配置
            SessionCreationPolicy.STATELESS: 不创建或使用HttpSession
            因为JWT是无状态认证，用户信息存在Token里，不在Session里
        */
        http.cors().and().csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            
            /* .and()：返回HttpSecurity对象，继续链式配置 */
            .and()
            
            /*
              第二步：授权规则配置（URL访问控制）
              
              authorizeRequests(): 开始配置URL授权规则
              
              antMatchers(): 使用Ant风格路径匹配
                * - 匹配任意单个路径段
                ** - 匹配任意多个路径段
                ? - 匹配任意单个字符
                
              permitAll(): 允许所有人访问（无需认证）
              authenticated(): 需要认证后才能访问
            */
            .authorizeRequests()
                
                /* 登录接口必须公开，否则无法获取Token */
                .antMatchers("/auth/**").permitAll()
                
                /* Swagger UI相关资源公开（开发调试用） */
                .antMatchers("/swagger-ui/**").permitAll()       // Swagger UI界面
                .antMatchers("/swagger-resources/**").permitAll() // Swagger资源配置
                .antMatchers("/v2/api-docs").permitAll()         // API文档JSON
                .antMatchers("/webjars/**").permitAll()           // 静态资源
                
                /* OPTIONS预检请求公开（CORS要求） */
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                
                /* 
                  兜底规则：其他所有请求都需要认证
                  必须放在最后，因为匹配是按顺序进行的
                */
                .anyRequest().authenticated();

        /*
          第三步：添加自定义过滤器
          
          addFilterBefore(filter, beforeFilter):
            在指定过滤器之前添加我们的JWT过滤器
            
          UsernamePasswordAuthenticationFilter:
            Spring Security的用户名密码认证过滤器
            我们要在它之前执行JWT验证
            
          过滤器执行顺序：
            1. 其他前置过滤器...
            2. JwtAuthenticationFilter (我们的JWT验证)
            3. UsernamePasswordAuthenticationFilter (用户名密码登录)
            4. 其他后置过滤器...
            
          为什么放在前面？
            - 大多数API请求都是携带Token的
            - 需要先验证Token才能确定用户身份
            - 只有登录请求才需要走用户名密码流程
        */
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
