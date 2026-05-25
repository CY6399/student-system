package com.edu.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 令牌工具类
 * <p>
 * 负责生成和解析登录令牌，使用 HMAC-SHA256 签名算法。
 * 密钥通过配置项 jwt.secret 注入，有效期通过 jwt.expiration 配置。
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * 生成登录 Token
     *
     * @param userId   用户 ID（存入 claims）
     * @param username 用户名（存入 claims）
     * @return JWT 字符串
     */
    public String generateToken(Long userId, String username) {
        return Jwts.builder()
                .claim("userId", userId)
                .claim("username", username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 解析 Token，提取 claims
     *
     * @param token JWT 字符串
     * @return claims（含 userId、username 等）
     * @throws io.jsonwebtoken.JwtException Token 无效或已过期
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /** 从 Token 中提取用户 ID */
    public Long getUserId(String token) {
        return parseToken(token).get("userId", Long.class);
    }

    /** 从 Token 中提取用户名 */
    public String getUsername(String token) {
        return parseToken(token).get("username", String.class);
    }

    /**
     * 根据密钥字符串生成 HMAC-SHA256 签名密钥
     * 要求密钥至少 32 个字节（256 位）
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
