package com.example.controller;

import com.example.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    /**
     * 根据投票ID生成AI分析报告
     * 访问地址：http://localhost:8080/api/ai/report/1
     */
    @GetMapping("/report/{id}")
    public String getVoteReport(@PathVariable Long id) {
        return aiService.generateVoteReport(id);
    }
}

