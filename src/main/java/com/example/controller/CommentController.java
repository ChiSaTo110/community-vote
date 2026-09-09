package com.example.controller;

import com.example.entity.Comment;
import com.example.repository.CommentRepository;
import com.example.service.AiService;
import com.example.utils.IpUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentRepository commentRepository;
    private final AiService aiService;

    /**
     * 发布评论
     * POST /api/comments?topicId=1&content=评论内容
     * 自动调用AI进行情感分析并打标签
     */
    @PostMapping
    public Comment addComment(@RequestParam Long topicId,
                              @RequestParam String content,
                              HttpServletRequest request) {
        Comment comment = new Comment();
        comment.setTopicId(topicId);
        comment.setContent(content);
        String ip = IpUtil.getRealIp(request);
        comment.setIpAddress(ip);


        // AI自动情感标注
        String sentiment = aiService.analyzeSentiment(content);
        comment.setSentiment(sentiment);

        return commentRepository.save(comment);
    }

    /**
     * 查询指定投票的评论列表（按时间倒序）
     * GET /api/comments/1
     */
    @GetMapping("/{topicId}")
    public List<Comment> getComments(@PathVariable Long topicId) {
        return commentRepository.findByTopicIdOrderByCreateTimeDesc(topicId);
    }
}

