package com.example.interceptor;

import com.example.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // OPTIONS 请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(401);
            return false;
        }

        String token = authHeader.substring(7);
        boolean valid = jwtUtil.validateToken(token);
        if (!valid) {
            response.setStatus(401);
            return false;
        }

        // 将 userId 存入请求属性，方便后续获取
        Long userId = jwtUtil.getUserIdFromToken(token);
        request.setAttribute("userId", userId);

        return true;
    }
}