package com.example.controller;

import com.example.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final AiService aiService;

    /**
     * 原有环境测试接口
     * 访问地址：http://localhost:8080/
     */
    @GetMapping("/")
    public Map<String, String> hello() {
        Map<String, String> result = new HashMap<>();
        result.put("message", "🚀 环境搭建成功！");
        result.put("status", "200");
        return result;
    }

    /**
     * AI大模型连通性测试接口
     * 访问地址：http://localhost:8080/test/ai
     * 调用后请查看 IDE 控制台输出
     */
    @GetMapping("/test/ai")
    public Map<String, String> testAi() {
        aiService.testCall();
        Map<String, String> result = new HashMap<>();
        result.put("message", "AI调用已执行，请查看控制台输出");
        result.put("status", "200");
        return result;
    }
}
