package com.edu.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录认证拦截器
 * <p>
 * 校验请求是否携带有效 Token，未登录或 Token 过期时返回 401。
 * 放行路径：/api/login（登录接口不需要认证）
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 请求处理前的拦截逻辑
     *
     * @return true=放行，false=拦截并返回 401
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行登录接口
        String uri = request.getRequestURI();
        if ("/api/login".equals(uri)) {
            return true;
        }

        // 从请求头获取 Token（格式: Authorization: Bearer xxx）
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(OBJECT_MAPPER.writeValueAsString(
                    Result.error(401, "未登录或会话已过期")));
            return false;
        }

        // 提取并校验 JWT Token
        String token = authHeader.substring(7);
        try {
            Claims claims = jwtUtil.parseToken(token);
            Long userId = claims.get("userId", Long.class);
            String username = claims.get("username", String.class);

            UserContext.setUserId(userId);
            UserContext.setUsername(username);
            return true;
        } catch (Exception e) {
            // Token 无效或已过期
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(OBJECT_MAPPER.writeValueAsString(
                    Result.error(401, "未登录或会话已过期")));
            return false;
        }
    }

    /**
     * 请求结束后清理用户上下文，防止内存泄漏
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.clear();
    }
}
