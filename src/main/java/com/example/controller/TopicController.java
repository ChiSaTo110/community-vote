package com.example.controller;

import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/topics")
public class TopicController {

    // 1. 创建投票接口（前端C会调这个）
    @PostMapping
    public Map<String, Object> createTopic(@RequestBody Map<String, String> params) {
        // 注意：目前还没连数据库，我们直接返回假数据，让前端C能先调通
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("msg", "创建成功（模拟数据）");

        Map<String, Object> data = new HashMap<>();
        data.put("id", 1L); // 假ID，等连了数据库就变真实ID
        response.put("data", data);

        return response;
    }

    // 2. 获取投票详情接口（前端C看详情时调）
    @GetMapping("/{id}")
    public Map<String, Object> getTopic(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);

        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("title", "这是测试投票标题");
        data.put("options", new String[]{"选项A", "选项B", "选项C"});
        response.put("data", data);

        return response;
    }
}