package com.edu.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

        // 提取 Token 字符串
        String token = authHeader.substring(7);

        // TODO: Task 13 替换为 JWT 解析，从 Token 中提取 userId 和 username
        // 当前为演示阶段，使用固定值，等任务 13 实现 JWT 后完善
        Long userId = 1L;
        String username = "admin";

        UserContext.setUserId(userId);
        UserContext.setUsername(username);

        return true;
    }

    /**
     * 请求结束后清理用户上下文，防止内存泄漏
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.clear();
    }
}
