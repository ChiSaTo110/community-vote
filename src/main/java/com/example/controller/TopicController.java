package com.example.controller;

import com.example.dto.CreateTopicRequest;
import com.example.entity.Topic;
import com.example.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;

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
            if (options == null || options.size() < 2) {
                response.put("code", 400);
                response.put("msg", "至少需要2个选项");
                return response;
            }
            if (type == null || type < 1 || type > 3) {
                response.put("code", 400);
                response.put("msg", "题型参数错误：1单选 2多选 3填空");
                return response;
            }

            String ip = httpRequest.getRemoteAddr();
            Topic topic = topicService.createTopic(title, description, type, options, ip);

            Map<String, Object> data = new HashMap<>();
            data.put("id", topic.getId());
            response.put("code", 200);
            response.put("msg", "创建成功");
            response.put("data", data);

        } catch (Exception e) {
            response.put("code", 500);
            response.put("msg", "创建失败：" + e.getMessage());
        }

        return response;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getTopic(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

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

        if (topic.getOptions() != null) {
            List<String> optionTexts = topic.getOptions().stream()
                    .map(opt -> opt.getOptionText())
                    .toList();
            data.put("options", optionTexts);
        }

        response.put("code", 200);
        response.put("data", data);

        return response;
    }
}