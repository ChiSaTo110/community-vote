package com.example.controller;

import com.example.entity.Topic;
import com.example.entity.User;
import com.example.repository.TopicRepository;
import com.example.repository.UserRepository;
import com.example.repository.VoteRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final TopicRepository topicRepository;
    private final VoteRecordRepository voteRecordRepository;

    /**
     * 获取当前登录用户信息
     * GET /api/users/me
     * Header: Authorization: Bearer <token>
     */
    @GetMapping("/me")
    public Map<String, Object> getMe(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            response.put("code", 401);
            response.put("msg", "未登录");
            return response;
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            response.put("code", 404);
            response.put("msg", "用户不存在");
            return response;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("nickname", user.getNickname());
        data.put("avatar", user.getAvatar());
        data.put("email", user.getEmail());
        data.put("createdAt", user.getCreatedAt());

        response.put("code", 200);
        response.put("data", data);

        return response;
    }

    /**
     * 更新当前用户信息
     * PUT /api/users/me
     * Body: { "nickname": "新昵称", "avatar": "头像URL", "email": "xxx@xx.com" }
     */
    @PutMapping("/me")
    public Map<String, Object> updateMe(@RequestBody Map<String, String> params,
                                        HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            response.put("code", 401);
            response.put("msg", "未登录");
            return response;
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            response.put("code", 404);
            response.put("msg", "用户不存在");
            return response;
        }

        String nickname = params.get("nickname");
        String avatar = params.get("avatar");
        String email = params.get("email");

        if (nickname != null && !nickname.trim().isEmpty()) {
            user.setNickname(nickname.trim());
        }
        if (avatar != null && !avatar.trim().isEmpty()) {
            user.setAvatar(avatar.trim());
        }
        if (email != null && !email.trim().isEmpty()) {
            user.setEmail(email.trim());
        }

        userRepository.save(user);

        response.put("code", 200);
        response.put("msg", "更新成功");

        return response;
    }

    /**
     * 我发起的话题
     * GET /api/users/me/topics
     */
    @GetMapping("/me/topics")
    public Map<String, Object> getMyTopics(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            response.put("code", 401);
            response.put("msg", "未登录");
            return response;
        }

        List<Topic> topics = topicRepository.findByUserIdOrderByCreatedAtDesc(userId);

        response.put("code", 200);
        response.put("data", topics);

        return response;
    }

    /**
     * 我参与的话题（投过票的话题，去重）
     * GET /api/users/me/votes
     */
    @GetMapping("/me/votes")
    public Map<String, Object> getMyVotes(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            response.put("code", 401);
            response.put("msg", "未登录");
            return response;
        }

        // 查询用户投过票的话题ID（去重）
        List<Long> topicIds = voteRecordRepository.findDistinctTopicIdsByUserId(userId);
        List<Topic> topics = topicRepository.findAllById(topicIds);

        response.put("code", 200);
        response.put("data", topics);

        return response;
    }
}