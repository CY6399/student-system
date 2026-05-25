package com.edu.service;

import com.edu.common.JwtUtil;
import com.edu.entity.User;
import com.edu.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户业务逻辑
 * <p>
 * 处理登录认证、密码校验、登录失败锁定、JWT 令牌生成等逻辑。
 */
@Service
public class UserService {

    /** BCrypt 密码编码器，用于校验密码 */
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 明文密码
     * @return 包含 token 和用户信息的 Map
     * @throws RuntimeException 用户名/密码错误、账号锁定等业务异常
     */
    public Map<String, Object> login(String username, String password) {
        // 1. 根据用户名查询用户
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 2. 检查账号是否被禁用
        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用，请联系管理员");
        }

        // 3. 检查账号是否被锁定（锁定未过期时拒绝登录）
        if (user.getLockedUntil() != null && LocalDateTime.now().isBefore(user.getLockedUntil())) {
            throw new RuntimeException("账号已被锁定，请30分钟后重试");
        }

        // 4. 如果锁定时间已过期，自动清除锁定状态
        if (user.getLockedUntil() != null && LocalDateTime.now().isAfter(user.getLockedUntil())) {
            userMapper.resetLoginError(user.getId());
            user.setLoginErrorCount(0);
            user.setLockedUntil(null);
        }

        // 5. 校验密码（BCrypt 匹配）
        boolean passwordMatch = passwordEncoder.matches(password, user.getPassword());
        if (!passwordMatch) {
            // 密码错误：累加失败次数
            int newErrorCount = user.getLoginErrorCount() + 1;
            user.setLoginErrorCount(newErrorCount);

            if (newErrorCount >= 5) {
                // 连续错误 5 次，锁定 30 分钟
                user.setLockedUntil(LocalDateTime.now().plusMinutes(30));
            }

            userMapper.updateLoginError(user);
            throw new RuntimeException("用户名或密码错误");
        }

        // 6. 登录成功：重置错误次数
        userMapper.resetLoginError(user.getId());

        // 7. 生成 JWT Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        // 8. 组装返回结果
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("realName", user.getRealName());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userInfo", userInfo);
        return result;
    }

    /**
     * 根据用户 ID 获取用户信息（用于 /api/user/info 接口）
     *
     * @param userId 用户 ID
     * @return 用户基本信息 Map
     */
    public Map<String, Object> getUserInfo(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("realName", user.getRealName());
        return userInfo;
    }
}
