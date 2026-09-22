package com.example.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.example.entity.Topic;
import com.example.entity.VoteOption;
import com.example.repository.TopicRepository;
import com.example.repository.VoteOptionRepository;
import com.example.repository.VoteRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AiService {

    @Value("${ai.api-key}")
    private String apiKey;

    @Value("${ai.model}")
    private String model;

    @Value("${ai.base-url}")
    private String baseUrl;

    private final TopicRepository topicRepository;
    private final VoteOptionRepository voteOptionRepository;
    // 新增：注入投票记录Repository，用于动态统计票数
    private final VoteRecordRepository voteRecordRepository;

    /**
     * 通用大模型调用方法（已修复资源泄漏警告）
     */
    private String callLLM(String prompt) {
        String url = baseUrl + "/chat/completions";
        String requestBody = JSONUtil.createObj()
                .set("model", model)
                .set("messages", JSONUtil.createArray()
                        .put(JSONUtil.createObj()
                                .set("role", "user")
                                .set("content", prompt)))
                .toString();

        try (HttpResponse response = HttpRequest.post(url)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(requestBody)
                .execute()) {

            return JSONUtil.parseObj(response.body())
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getStr("content");
        } catch (Exception e) {
            e.printStackTrace();
            return "大模型调用失败：" + e.getMessage();
        }
    }

    /**
     * 基础测试方法
     */
    public void testCall() {
        String reply = callLLM("你好，请做一个简短的自我介绍");
        System.out.println("大模型返回结果：" + reply);
    }

    /**
     * 根据投票ID生成AI分析报告
     */
    public String generateVoteReport(Long topicId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new IllegalArgumentException("投票主题不存在"));
        List<VoteOption> options = voteOptionRepository.findByTopicId(topicId);

        StringBuilder prompt = new StringBuilder();
        prompt.append("请基于以下投票数据生成一份简洁的分析报告：\n");
        prompt.append("投票主题：").append(topic.getTitle()).append("\n");
        prompt.append("各选项得票情况：\n");
        for (VoteOption option : options) {
            // 修复1：选项内容字段对应你的实体字段 optionText
            // 修复2：票数通过投票记录表动态统计
            Long voteCount = voteRecordRepository.countByOptionId(option.getId());
            prompt.append("- ").append(option.getOptionText())
                    .append("：").append(voteCount).append("票\n");
        }
        prompt.append("\n输出要求：\n1. 计算各选项得票占比\n2. 总结整体投票趋势\n3. 给出2-3条简短结论");

        return callLLM(prompt.toString());
    }

    /**
     * 评论情感倾向分析
     */
    public String analyzeSentiment(String content) {
        String prompt = "判断以下评论的情感倾向，仅返回一个单词：POSITIVE、NEGATIVE、NEUTRAL。评论内容：" + content;
        String reply = callLLM(prompt).trim().toUpperCase();

        if (reply.contains("POSITIVE")) return "POSITIVE";
        if (reply.contains("NEGATIVE")) return "NEGATIVE";
        return "NEUTRAL";
    }
}


