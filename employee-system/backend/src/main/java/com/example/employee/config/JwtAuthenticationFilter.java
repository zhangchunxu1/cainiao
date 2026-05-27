package com.example.employee.config;

/*
  JwtAuthenticationFilter.java - JWT认证过滤器
  ================================================
  
  功能说明：
  - 拦截所有HTTP请求
  - 从请求头中提取JWT Token
  - 验证Token的有效性（签名、过期时间、用户名）
  - 将验证通过的用户信息设置到Security上下文
  
  继承关系：
  OncePerRequestFilter → JwtAuthenticationFilter
  
  OncePerRequestFilter的特点：
  - 确保每个请求只执行一次过滤逻辑
  - 避免在请求转发时重复执行
  - 是Spring Security推荐的过滤器基类
  
  执行流程：
  1. 客户端发送请求（携带Authorization: Bearer xxx）
  2. 该过滤器拦截请求
  3. 从Header中提取JWT Token
  4. 调用JwtUtil解析Token获取用户名
  5. 验证Token是否有效
  6. 将用户信息设置到SecurityContext
  7. 放行请求，继续后续处理
  
  注意事项：
  - 登录接口(/auth/**)不需要Token，已在SecurityConfig中配置为permitAll
  - Token过期或无效会返回401错误
  - Token不存在时，该过滤器不做任何处理（放行到后续过滤器）
*/

import com.example.employee.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/* 
  JWT认证过滤器类
  
  不添加@Component注解！
  原因：在SecurityConfig中通过@Bean方法注册，避免重复Bean问题
*/
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /*
      注入JWT工具类
      
      @Autowired：自动装配，Spring容器会自动注入JwtUtil的实例
      
      JwtUtil的作用：
      - 从Token中提取用户名
      - 验证Token是否过期
      - 验证Token签名是否正确
    */
    @Autowired
    private JwtUtil jwtUtil;

    /*
      核心过滤方法：处理每个HTTP请求
      
      方法参数：
        request: HTTP请求对象，包含请求头、参数等信息
        response: HTTP响应对象，用于返回响应数据
        chain: 过滤器链，用于将请求传递给下一个过滤器
        
      异常说明：
        ServletException: Servlet相关异常
        IOException: IO操作异常
        
      执行时机：
        每个请求到达时自动调用（由OncePerRequestFilter保证只执行一次）
    */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request, 
            HttpServletResponse response, 
            FilterChain chain)
            throws ServletException, IOException {

        /* 
          第一步：从请求头中提取Authorization字段
          
          JWT Token的标准格式：
            Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
            
          Header名称：Authorization（标准HTTP头）
          Bearer：Token类型标识符（OAuth 2.0标准）
          
          为什么使用Header而不是URL参数？
          - 更安全（不会出现在日志/历史记录中）
          - 符合RESTful规范
          - 避免浏览器缓存问题
        */
        final String requestTokenHeader = request.getHeader("Authorization");

        // 初始化变量，用于存储提取的用户名和Token
        String username = null;   // 从Token中解析出的用户名
        String jwtToken = null;   // 提取出的原始Token字符串

        /* 
          第二步：检查并提取JWT Token
          
          条件判断：
            1. requestTokenHeader != null → Header存在
            2. startsWith("Bearer ") → 格式正确
            
          substring(7)：
            "Bearer eyJhbG..." 
            ↓ 去掉前7个字符("Bearer ")
            "eyJhbG..."
        */
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            
            // 提取纯Token字符串（去掉"Bearer "前缀）
            jwtToken = requestTokenHeader.substring(7);
            
            try {
                /* 
                  调用JwtUtil解析Token，获取用户名
                  
                  内部实现：
                  1. 解析JWT的三部分（Header.Payload.Signature）
                  2. Base64解码Payload
                  3. 从Claims中提取subject（我们存储的是username）
                  
                  可能抛出的异常：
                  - ExpiredJwtException: Token已过期
                  - MalformedJwtException: Token格式错误
                  - SignatureException: 签名不匹配
                  - IllegalArgumentException: Token为空或null
                */
                username = jwtUtil.getUsernameFromToken(jwtToken);
                
            } catch (Exception e) {
                // 记录错误日志，但不中断请求（让请求继续，后续会因无认证而失败）
                logger.error("无法解析JWT Token", e);
            }
        }

        /* 
          第三步：验证Token并设置用户认证信息
          
          条件判断：
            1. username != null → 成功从Token中提取出用户名
            2. SecurityContextHolder.getContext().getAuthentication() == null
               → 当前请求尚未进行身份认证（避免重复认证）
               
          为什么需要第二个条件？
            - 一个请求可能经过多个过滤器
            - 如果已经认证过，就不需要重复认证
            - 提高性能，避免不必要的Token验证
        */
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            /* 
              验证Token的有效性
              
              validateToken内部检查：
              1. Token是否过期（exp claim）
              2. Token是否在生效时间之前（nbf claim）
              3. Token签名是否正确
              4. Token中的用户名是否匹配
              
              返回值：
                true - Token有效
                false - Token无效或已过期
            */
            if (jwtUtil.validateToken(jwtToken, username)) {
                
                /* 
                  创建认证令牌（Authentication Token）
                  
                  UsernamePasswordAuthenticationToken构造参数：
                    principal: 用户标识（这里是username）
                    credentials: 凭证（密码，因为已经验证过Token，所以传null）
                    authorities: 权限列表（这里简化处理，传null）
                    
                  为什么传null？
                    - JWT Token已经证明了用户身份
                    - 不需要再次验证密码
                    - 权限可以在需要时从数据库加载
                */
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(username, null, null);
                
                /* 
                  设置认证详情
                  
                  WebAuthenticationDetailsSource包含：
                    remoteAddress: 客户端IP地址
                    sessionId: 会话ID（如果有的话）
                  
                  作用：
                    - 用于审计日志（记录哪个IP登录）
                    - 用于安全策略（限制IP访问等）
                */
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                /* 
                  将认证信息设置到SecurityContext
                  
                  SecurityContextHolder：
                    - Spring Security的核心类
                    - 使用ThreadLocal存储当前线程的安全上下文
                    - 后续的Controller/Service可以通过它获取当前用户信息
                    
                  获取方式：
                    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                    String currentUser = auth.getName();
                    
                  重要提示：
                    - 必须在每个请求结束时清理Context（防止内存泄漏）
                    - Spring Security会自动处理（通过SecurityContextPersistenceFilter）
                */
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        /* 
          第四步：放行请求，继续过滤器链
          
          chain.doFilter()的作用：
            - 将请求传递给下一个过滤器
            - 如果没有下一个过滤器，则到达目标Servlet/Controller
            
          什么时候调用？
            - 无论Token验证成功与否都要调用
            - Token无效的情况：未设置认证信息，后续安全过滤器会拒绝访问
            - Token有效的情况：已设置认证信息，正常放行
            
          不调用的后果：
            - 请求会被"卡住"，不会到达Controller
            - 客户端会一直等待直到超时
        */
        chain.doFilter(request, response);
    }
}
