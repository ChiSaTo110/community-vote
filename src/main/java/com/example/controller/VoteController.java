package com.example.controller;

import com.example.dto.VoteRequest;
import com.example.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping("/vote")
    public Map<String, Object> vote(@RequestBody VoteRequest request,
                                    HttpServletRequest httpRequest) {
        Map<String, Object> response = new HashMap<>();

        try {
            Long topicId = request.getTopicId();
            List<Long> optionIds = request.getOptionIds();
            String fillText = request.getFillText();

            if (topicId == null) {
                response.put("code", 400);
                response.put("msg", "topicId 不能为空");
                return response;
            }

            String ip = httpRequest.getRemoteAddr();
            Map<String, Object> result = voteService.vote(topicId, optionIds, fillText, ip);

            boolean success = Boolean.TRUE.equals(result.get("success"));
            response.put("code", success ? 200 : 400);
            response.put("msg", result.get("message"));
            if (result.containsKey("data")) {
                response.put("data", result.get("data"));
            }

        } catch (Exception e) {
            response.put("code", 500);
            response.put("msg", "投票异常：" + e.getMessage());
        }

        return response;
    }

    @GetMapping("/topics/{id}/results")
    public Map<String, Object> getResults(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        Map<String, Object> result = voteService.getResults(id);

        boolean success = Boolean.TRUE.equals(result.get("success"));
        if (!success) {
            response.put("code", 404);
            response.put("msg", result.get("message"));
            return response;
        }

        response.put("code", 200);
        response.put("data", result.get("data"));

        return response;
    }
}