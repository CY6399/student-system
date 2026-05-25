package com.edu.common;

/**
 * 用户上下文工具类
 * <p>
 * 使用 ThreadLocal 存储当前登录用户信息，在请求生命周期内共享。
 * 请求进入时由 AuthInterceptor 设置，请求结束时自动清理。
 */
public class UserContext {

    private static final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();

    private static final ThreadLocal<String> usernameHolder = new ThreadLocal<>();

    /** 设置当前用户 ID */
    public static void setUserId(Long userId) {
        userIdHolder.set(userId);
    }

    /** 获取当前用户 ID */
    public static Long getUserId() {
        return userIdHolder.get();
    }

    /** 设置当前用户名 */
    public static void setUsername(String username) {
        usernameHolder.set(username);
    }

    /** 获取当前用户名 */
    public static String getUsername() {
        return usernameHolder.get();
    }

    /** 请求结束后清除，防止内存泄漏 */
    public static void clear() {
        userIdHolder.remove();
        usernameHolder.remove();
    }
}
