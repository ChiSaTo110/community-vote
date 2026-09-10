package com.example.controller;

import com.example.dto.CreateTopicRequest;
import com.example.entity.Topic;
import com.example.entity.VoteOption;
import com.example.repository.VoteOptionRepository;
import com.example.repository.VoteRecordRepository;
import com.example.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
public class TopicController {

    private static final Logger log = LoggerFactory.getLogger(TopicController.class);

    private final TopicService topicService;
    private final VoteOptionRepository voteOptionRepository;
    private final VoteRecordRepository voteRecordRepository;

    /**
     * 创建投票
     * POST /api/topics
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
                response.put("code", 400);
                response.put("msg", "标题不能为空");
                return response;
            }
            if (type == null || type < 1 || type > 3) {
                response.put("code", 400);
                response.put("msg", "题型参数错误：1单选 2多选 3填空");
                return response;
            }
            // 单选/多选需要至少2个选项，填空不需要
            if (type != 3) {
                if (options == null || options.size() < 2) {
                    response.put("code", 400);
                    response.put("msg", "至少需要2个选项");
                    return response;
                }
            } else {
                // 填空类型：options 置空
                options = new ArrayList<>();
            }

            String ip = httpRequest.getRemoteAddr();
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
        return response;
    }

    /**
     * 随机话题列表
     * GET /api/topics/random?size=10
     */
    @GetMapping("/random")
    public Map<String, Object> getRandomTopics(@RequestParam(defaultValue = "10") int size) {
        Map<String, Object> response = new HashMap<>();
        List<Topic> topics = topicService.getRandomTopics(size);
        response.put("code", 200);
        response.put("data", buildTopicList(topics));
        return response;
    }

    /**
     * 搜索话题
     * GET /api/topics/search?keyword=xxx
     */
    @GetMapping("/search")
    public Map<String, Object> searchTopics(@RequestParam String keyword) {
        Map<String, Object> response = new HashMap<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            response.put("code", 400);
            response.put("msg", "搜索关键词不能为空");
            return response;
        }
        List<Topic> topics = topicService.searchTopics(keyword.trim());
        response.put("code", 200);
        response.put("data", buildTopicList(topics));
        return response;
    }

    /**
     * 热门话题
     * GET /api/topics/hot?size=10
     */
    @GetMapping("/hot")
    public Map<String, Object> getHotTopics(@RequestParam(defaultValue = "10") int size) {
        Map<String, Object> response = new HashMap<>();
        List<Topic> topics = topicService.getHotTopics(size);
        response.put("code", 200);
        response.put("data", buildTopicList(topics));
        return response;
    }

    /**
     * 组装话题列表返回数据（包含 optionCount 和 voteCount）
     */
    private List<Map<String, Object>> buildTopicList(List<Topic> topics) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Topic t : topics) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", t.getId());
            map.put("title", t.getTitle());
            map.put("description", t.getDescription());
            map.put("type", t.getType());
            map.put("status", t.getStatus());
            map.put("createdAt", t.getCreatedAt());
            map.put("userId", t.getUserId());
            map.put("optionCount", voteOptionRepository.findByTopicId(t.getId()).size());
            map.put("voteCount", voteRecordRepository.countByTopicId(t.getId()));
            result.add(map);
        }
        return result;
    }
}