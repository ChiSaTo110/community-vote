package com.example.controller;

import com.example.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    /**
     * 根据投票ID生成AI分析报告
     * GET /api/ai/report/{id}
     */
    @GetMapping("/report/{id}")
    public Map<String, Object> getVoteReport(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            String report = aiService.generateVoteReport(id);
            response.put("code", 200);
            response.put("data", report);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("msg", "AI报告生成失败：" + e.getMessage());
        }
        return response;
    }
}