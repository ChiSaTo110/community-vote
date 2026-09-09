package com.example.controller;

import com.example.dto.VoteRequest;
import com.example.service.VoteService;
import com.example.utils.IpUtil;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VoteController {

    private static final Logger log = LoggerFactory.getLogger(VoteController.class);
    private final VoteService voteService;

    @PostMapping("/vote")
    public Map<String, Object> vote(@RequestBody VoteRequest request,
                                    HttpServletRequest httpRequest) {
        Map<String, Object> response = new HashMap<>();
        log.info("收到投票请求：topicId={}, optionIds={}, fillText={}", 
                 request.getTopicId(), request.getOptionIds(), request.getFillText());

        try {
            Long topicId = request.getTopicId();
            List<Long> optionIds = request.getOptionIds();
            String fillText = request.getFillText();

            if (topicId == null) {
                log.warn("投票失败：topicId为空");
                response.put("code", 400);
                response.put("msg", "topicId 不能为空");
                return response;
            }

            String ip = IpUtil.getRealIp(httpRequest);
            Map<String, Object> result = voteService.vote(topicId, optionIds, fillText, ip);

            boolean success = Boolean.TRUE.equals(result.get("success"));
            response.put("code", success ? 200 : 400);
            response.put("msg", result.get("message"));
            if (result.containsKey("data")) {
                response.put("data", result.get("data"));
            }

            if (success) {
                log.info("投票成功：topicId={}, ip={}", topicId, ip);
            } else {
                log.warn("投票失败：topicId={}, ip={}, reason={}", topicId, ip, result.get("message"));
            }

        } catch (Exception e) {
            log.error("投票异常：", e);
            response.put("code", 500);
            response.put("msg", "投票异常：" + e.getMessage());
        }

        return response;
    }

    @GetMapping("/topics/{id}/results")
    public Map<String, Object> getResults(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        log.info("获取投票结果：topicId={}", id);

        Map<String, Object> result = voteService.getResults(id);

        boolean success = Boolean.TRUE.equals(result.get("success"));
        if (!success) {
            log.warn("获取投票结果失败：topicId={}, reason={}", id, result.get("message"));
            response.put("code", 404);
            response.put("msg", result.get("message"));
            return response;
        }

        response.put("code", 200);
        response.put("data", result.get("data"));
        log.info("获取投票结果成功：topicId={}", id);

        return response;
    }
}