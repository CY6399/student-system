package com.edu.controller;

import com.edu.common.Result;
import com.edu.common.UserContext;
import com.edu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 登录与用户信息接口
 * <p>
 * 提供登录认证、获取当前用户信息等功能。
 * 登录接口放行不校验 Token，其他接口需在请求头携带 Authorization: Bearer xxx。
 */
@RestController
@RequestMapping("/api")
public class LoginController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     * <p>
     * 请求方式：POST
     * 请求体：{"username": "admin", "password": "123456"}
     *
     * @param params 包含 username 和 password 的请求体
     * @return 登录成功返回 token 和用户信息
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        // 参数校验
        String username = params.get("username");
        String password = params.get("password");

        if (username == null || username.trim().isEmpty()) {
            return Result.error(400, "用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            return Result.error(400, "密码不能为空");
        }

        // 调用登录业务逻辑
        Map<String, Object> result = userService.login(username, password);
        return Result.success(result);
    }

    /**
     * 获取当前登录用户信息
     * <p>
     * 从 Token 中解析用户 ID，查询并返回用户基本信息。
     *
     * @return 用户基本信息（id、username、realName）
     */
    @GetMapping("/user/info")
    public Result<Map<String, Object>> getUserInfo() {
        Long userId = UserContext.getUserId();
        Map<String, Object> userInfo = userService.getUserInfo(userId);
        return Result.success(userInfo);
    }
}
