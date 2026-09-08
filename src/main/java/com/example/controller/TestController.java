package com.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {

    @GetMapping("/")
    public Map<String, String> hello() {
        Map<String, String> result = new HashMap<>();
        result.put("message", "🚀 环境搭建成功！");
        result.put("status", "200");
        return result;
    }
}