package com.example.controller;

import com.example.entity.Comment;
import com.example.repository.CommentRepository;
import com.example.service.AiService;
import com.example.utils.IpUtil;
import com.example.utils.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentRepository commentRepository;
    private final AiService aiService;
    private final JwtUtil jwtUtil;

    /**
     * 发布评论
     * POST /api/comments?topicId=1&content=评论内容
     */
    @PostMapping
    public Map<String, Object> addComment(@RequestParam Long topicId,
                                          @RequestParam String content,
                                          HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        Comment comment = new Comment();
        comment.setTopicId(topicId);
        comment.setContent(content);
        comment.setIpAddress(IpUtil.getRealIp(request));

        // 从Token中解析用户ID（未登录则为null）
        Long userId = null;
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtUtil.validateToken(token)) {
                userId = jwtUtil.getUserIdFromToken(token);
            }
        }
        comment.setUserId(userId);

        // AI自动情感标注
        String sentiment = aiService.analyzeSentiment(content);
        comment.setSentiment(sentiment);

        Comment saved = commentRepository.save(comment);
        response.put("code", 200);
        response.put("data", saved);
        return response;
    }

    /**
     * 查询指定投票的评论列表（按时间倒序）
     * GET /api/comments/{topicId}
     */
    @GetMapping("/{topicId}")
    public Map<String, Object> getComments(@PathVariable Long topicId) {
        Map<String, Object> response = new HashMap<>();
        List<Comment> comments = commentRepository.findByTopicIdOrderByCreateTimeDesc(topicId);
        response.put("code", 200);
        response.put("data", comments);
        return response;
    }
}