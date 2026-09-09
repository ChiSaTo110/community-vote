package com.example.controller;

import com.example.entity.User;
import com.example.repository.UserRepository;
import com.example.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 用户注册
     * POST /api/auth/register
     * Body: { "username": "user", "password": "123", "nickname": "昵称", "email": "xxx@xx.com" }
     */
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, String> params) {
        Map<String, Object> response = new HashMap<>();

        String username = params.get("username");
        String password = params.get("password");
        String nickname = params.get("nickname");
        String email = params.get("email");

        // 参数校验
        if (username == null || username.trim().isEmpty()) {
            response.put("code", 400);
            response.put("msg", "用户名不能为空");
            return response;
        }
        if (password == null || password.trim().isEmpty()) {
            response.put("code", 400);
            response.put("msg", "密码不能为空");
            return response;
        }
        if (password.length() < 3) {
            response.put("code", 400);
            response.put("msg", "密码至少3位");
            return response;
        }

        // 用户名唯一性校验
        if (userRepository.existsByUsername(username)) {
            response.put("code", 400);
            response.put("msg", "用户名已存在");
            return response;
        }

        // 创建用户
        User user = new User();
        user.setUsername(username.trim());
        user.setPassword(passwordEncoder.encode(password.trim()));
        user.setNickname(nickname != null ? nickname.trim() : username.trim());
        user.setEmail(email != null ? email.trim() : null);

        User saved = userRepository.save(user);

        Map<String, Object> data = new HashMap<>();
        data.put("id", saved.getId());
        data.put("username", saved.getUsername());
        data.put("nickname", saved.getNickname());

        response.put("code", 200);
        response.put("msg", "注册成功");
        response.put("data", data);

        return response;
    }

    /**
     * 用户登录
     * POST /api/auth/login
     * Body: { "username": "user", "password": "123" }
     */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> params) {
        Map<String, Object> response = new HashMap<>();

        String username = params.get("username");
        String password = params.get("password");

        if (username == null || username.trim().isEmpty()) {
            response.put("code", 400);
            response.put("msg", "用户名不能为空");
            return response;
        }
        if (password == null || password.trim().isEmpty()) {
            response.put("code", 400);
            response.put("msg", "密码不能为空");
            return response;
        }

        // 查询用户
        User user = userRepository.findByUsername(username.trim()).orElse(null);
        if (user == null) {
            response.put("code", 400);
            response.put("msg", "用户名或密码错误");
            return response;
        }

        // 验证密码
        if (!passwordEncoder.matches(password.trim(), user.getPassword())) {
            response.put("code", 400);
            response.put("msg", "用户名或密码错误");
            return response;
        }

        // 生成 JWT Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        data.put("nickname", user.getNickname());
        data.put("avatar", user.getAvatar());

        response.put("code", 200);
        response.put("msg", "登录成功");
        response.put("data", data);

        return response;
    }
}