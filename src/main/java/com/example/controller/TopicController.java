package com.example.controller;

import com.example.dto.CreateTopicRequest;
import com.example.entity.Topic;
import com.example.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
public class TopicController {

    private static final Logger log = LoggerFactory.getLogger(TopicController.class);

    private final TopicService topicService;

    /**
     * 创建投票
     * POST /api/topics
     * Header: Authorization: Bearer <token>（可选，登录用户关联）
     * Body: { "title": "标题", "description": "描述", "type": 1, "options": ["A", "B"] }
     */
    @PostMapping
    public Map<String, Object> createTopic(@RequestBody CreateTopicRequest request,
                                           HttpServletRequest httpRequest) {
        Map<String, Object> response = new HashMap<>();
        log.info("收到创建投票请求：title={}, type={}, options={}",
                 request.getTitle(), request.getType(), request.getOptions());

        try {
            String title = request.getTitle();
            String description = request.getDescription();
            Integer type = request.getType();
            List<String> options = request.getOptions();

            // 参数校验
            if (title == null || title.trim().isEmpty()) {
                log.warn("创建投票失败：标题为空");
                response.put("code", 400);
                response.put("msg", "标题不能为空");
                return response;
            }
            if (options == null || options.size() < 2) {
                log.warn("创建投票失败：选项数量不足，options={}", options);
                response.put("code", 400);
                response.put("msg", "至少需要2个选项");
                return response;
            }
            if (type == null || type < 1 || type > 3) {
                log.warn("创建投票失败：题型参数错误，type={}", type);
                response.put("code", 400);
                response.put("msg", "题型参数错误：1单选 2多选 3填空");
                return response;
            }

            String ip = httpRequest.getRemoteAddr();
            // 获取当前用户ID（登录用户从Token解析，游客为null）
            Long userId = (Long) httpRequest.getAttribute("userId");

            Topic topic = topicService.createTopic(title, description, type, options, ip, userId);

            Map<String, Object> data = new HashMap<>();
            data.put("id", topic.getId());
            response.put("code", 200);
            response.put("msg", "创建成功");
            response.put("data", data);

            log.info("创建投票成功：topicId={}, title={}, userId={}", topic.getId(), title, userId);

        } catch (Exception e) {
            log.error("创建投票异常：", e);
            response.put("code", 500);
            response.put("msg", "创建失败：" + e.getMessage());
        }

        return response;
    }

    /**
     * 获取投票详情
     * GET /api/topics/{id}
     */
    @GetMapping("/{id}")
    public Map<String, Object> getTopic(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        log.info("获取投票详情：topicId={}", id);

        Topic topic = topicService.getTopicById(id);

        if (topic == null) {
            log.warn("投票不存在：topicId={}", id);
            response.put("code", 404);
            response.put("msg", "投票不存在");
            return response;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", topic.getId());
        data.put("title", topic.getTitle());
        data.put("description", topic.getDescription());
        data.put("type", topic.getType());
        data.put("status", topic.getStatus());
        data.put("createdAt", topic.getCreatedAt());
        data.put("userId", topic.getUserId());

        // 返回包含 id 和 optionText 的对象数组
        if (topic.getOptions() != null) {
            List<Map<String, Object>> optionList = topic.getOptions().stream()
                    .map(opt -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", opt.getId());
                        map.put("optionText", opt.getOptionText());
                        return map;
                    })
                    .toList();
            data.put("options", optionList);
        }

        response.put("code", 200);
        response.put("data", data);
        log.info("获取投票详情成功：topicId={}, optionsCount={}", id,
                 topic.getOptions() != null ? topic.getOptions().size() : 0);

        return response;
    }

    /**
     * 随机话题列表（首页推送）
     * GET /api/topics/random?size=10
     */
    @GetMapping("/random")
    public Map<String, Object> getRandomTopics(@RequestParam(defaultValue = "10") int size) {
        Map<String, Object> response = new HashMap<>();
        log.info("获取随机话题列表：size={}", size);

        List<Topic> topics = topicService.getRandomTopics(size);

        response.put("code", 200);
        response.put("data", topics);
        log.info("随机话题列表获取成功：count={}", topics.size());

        return response;
    }

    /**
     * 搜索话题
     * GET /api/topics/search?keyword=xxx
     */
    @GetMapping("/search")
    public Map<String, Object> searchTopics(@RequestParam String keyword) {
        Map<String, Object> response = new HashMap<>();
        log.info("搜索话题：keyword={}", keyword);

        if (keyword == null || keyword.trim().isEmpty()) {
            response.put("code", 400);
            response.put("msg", "搜索关键词不能为空");
            return response;
        }

        List<Topic> topics = topicService.searchTopics(keyword.trim());

        response.put("code", 200);
        response.put("data", topics);
        log.info("搜索话题完成：keyword={}, count={}", keyword, topics.size());

        return response;
    }

    /**
     * 热门话题（按参与人数排序）
     * GET /api/topics/hot?size=10
     */
    @GetMapping("/hot")
    public Map<String, Object> getHotTopics(@RequestParam(defaultValue = "10") int size) {
        Map<String, Object> response = new HashMap<>();
        log.info("获取热门话题：size={}", size);

        List<Topic> topics = topicService.getHotTopics(size);

        response.put("code", 200);
        response.put("data", topics);
        log.info("热门话题获取成功：count={}", topics.size());

        return response;
    }
}