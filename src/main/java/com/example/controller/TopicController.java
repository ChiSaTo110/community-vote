package com.example.controller;

import com.example.dto.CreateTopicRequest;
import com.example.entity.Topic;
import com.example.entity.User;
import com.example.repository.CommentRepository;
import com.example.repository.UserRepository;
import com.example.repository.VoteOptionRepository;
import com.example.repository.VoteRecordRepository;
import com.example.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
public class TopicController {

    private static final Logger log = LoggerFactory.getLogger(TopicController.class);

    private final TopicService topicService;
    private final VoteOptionRepository voteOptionRepository;
    private final VoteRecordRepository voteRecordRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    /**
     * 创建投票
     */
    @PostMapping
    public Map<String, Object> createTopic(@RequestBody CreateTopicRequest request,
                                           HttpServletRequest httpRequest) {
        Map<String, Object> response = new HashMap<>();
        try {
            String title = request.getTitle();
            String description = request.getDescription();
            Integer type = request.getType();
            List<String> options = request.getOptions();

            if (title == null || title.trim().isEmpty()) {
                response.put("code", 400);
                response.put("msg", "标题不能为空");
                return response;
            }
            if (type == null || type < 1 || type > 3) {
                response.put("code", 400);
                response.put("msg", "题型参数错误");
                return response;
            }
            if (type != 3) {
                if (options == null || options.size() < 2) {
                    response.put("code", 400);
                    response.put("msg", "至少需要2个选项");
                    return response;
                }
            } else {
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
        } catch (Exception e) {
            log.error("创建投票异常：", e);
            response.put("code", 500);
            response.put("msg", "创建失败：" + e.getMessage());
        }
        return response;
    }

    /**
     * 获取投票详情
     */
    @GetMapping("/{id}")
    public Map<String, Object> getTopic(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        Topic topic = topicService.getTopicById(id);
        if (topic == null) {
            response.put("code", 404);
            response.put("msg", "投票不存在");
            return response;
        }

        Map<String, Object> data = toDetailMap(topic);
        response.put("code", 200);
        response.put("data", data);
        return response;
    }

    /**
     * 随机话题列表
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
     * 组装话题列表（含发起人信息、选项数、票数、评论数）
     */
    private List<Map<String, Object>> buildTopicList(List<Topic> topics) {
        if (topics == null || topics.isEmpty()) return new ArrayList<>();

        // 批量查询发起人（避免 N+1）
        List<Long> userIds = topics.stream()
                .map(Topic::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, User> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

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
            map.put("commentCount", commentRepository.countByTopicId(t.getId()));
            map.put("likeCount", 0);

            // 发起人信息
            User creator = t.getUserId() != null ? userMap.get(t.getUserId()) : null;
            if (creator != null) {
                map.put("creatorNickname", creator.getNickname() != null ? creator.getNickname() : creator.getUsername());
                map.put("creatorUsername", creator.getUsername());
                map.put("creatorAvatar", creator.getAvatar());
            } else {
                map.put("creatorNickname", "匿名用户");
                map.put("creatorUsername", "anonymous");
                map.put("creatorAvatar", null);
            }

            result.add(map);
        }
        return result;
    }

    /**
     * 组装话题详情
     */
    private Map<String, Object> toDetailMap(Topic topic) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", topic.getId());
        data.put("title", topic.getTitle());
        data.put("description", topic.getDescription());
        data.put("type", topic.getType());
        data.put("status", topic.getStatus());
        data.put("createdAt", topic.getCreatedAt());
        data.put("userId", topic.getUserId());
        data.put("voteCount", voteRecordRepository.countByTopicId(topic.getId()));
        data.put("commentCount", commentRepository.countByTopicId(topic.getId()));
        data.put("likeCount", 0);

        // 发起人信息
        User creator = topic.getUserId() != null ? userRepository.findById(topic.getUserId()).orElse(null) : null;
        if (creator != null) {
            data.put("creatorNickname", creator.getNickname() != null ? creator.getNickname() : creator.getUsername());
            data.put("creatorUsername", creator.getUsername());
            data.put("creatorAvatar", creator.getAvatar());
        } else {
            data.put("creatorNickname", "匿名用户");
            data.put("creatorUsername", "anonymous");
            data.put("creatorAvatar", null);
        }

        // 选项
        if (topic.getOptions() != null) {
            List<Map<String, Object>> optionList = topic.getOptions().stream()
                    .map(opt -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", opt.getId());
                        map.put("optionText", opt.getOptionText());
                        return map;
                    })
                    .collect(Collectors.toList());
            data.put("options", optionList);
        }

        return data;
    }
}