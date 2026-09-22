package com.example.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // 密钥（生产环境应放在配置文件中）
    private static final String SECRET = "community-vote-secret-key-2024-spring-boot-project";
    private static final long EXPIRE_TIME = 1000 * 60 * 60 * 24; // 24小时

    private Key getKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    /**
     * 生成 Token
     */
    public String generateToken(Long userId, String username) {
        Date now = new Date();
        Date expire = new Date(now.getTime() + EXPIRE_TIME);

        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .setIssuedAt(now)
                .setExpiration(expire)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析 Token，获取 Claims
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从 Token 中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        if (claims == null) return null;
        return claims.get("userId", Long.class);
    }

    /**
     * 从 Token 中获取用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        if (claims == null) return null;
        return claims.getSubject();
    }

    /**
     * 验证 Token 是否有效
     */
    public boolean validateToken(String token) {
        Claims claims = parseToken(token);
        if (claims == null) return false;
        return !claims.getExpiration().before(new Date());
    }
}