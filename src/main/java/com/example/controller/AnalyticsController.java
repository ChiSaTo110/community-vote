package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 1. 汇总数据
    @GetMapping("/summary")
    public Map<String, Object> getSummary() {
        Map<String, Object> result = new HashMap<>();
        Long totalVotes = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM vote_record", Long.class);
        Long totalUsers = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM user", Long.class);
        Long totalTopics = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM topic", Long.class);
        result.put("totalVotes", totalVotes);
        result.put("totalUsers", totalUsers);
        result.put("totalTopics", totalTopics);
        result.put("analysisTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        return result;
    }

    // 2. 每日投票趋势
    @GetMapping("/daily-votes")
    public List<Map<String, Object>> getDailyVotes() {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
            "SELECT day, vote_count FROM ana_daily_votes ORDER BY day");
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Map<String, Object> item = new HashMap<>();
            item.put("voteDate", row.get("day"));
            item.put("voteCount", row.get("vote_count"));
            result.add(item);
        }
        return result;
    }

    // 3. 热门话题 TOP10（注意路径是 /topic-hot，不是 /hot-topics）
    @GetMapping("/topic-hot")
    public List<Map<String, Object>> getTopicHot() {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
            "SELECT title, vote_count FROM ana_topic_hot ORDER BY vote_count DESC LIMIT 10");
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Map<String, Object> item = new HashMap<>();
            item.put("topicName", row.get("title"));
            item.put("voteCount", row.get("vote_count"));
            result.add(item);
        }
        return result;
    }

        // 4. 话题类型分布
    @GetMapping("/type-dist")
    public List<Map<String, Object>> getTypeDist() {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
            "SELECT type_name, topic_count FROM ana_type_dist");
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Map<String, Object> item = new HashMap<>();
            String name = (String) row.get("type_name");
            // 英文转中文
            if ("Single".equals(name)) name = "单选题";
            else if ("Multiple".equals(name)) name = "多选题";
            else if ("Fill".equals(name)) name = "填空题";
            item.put("name", name);
            item.put("value", row.get("topic_count"));
            result.add(item);
        }
        return result;
    }

    // 5. 用户活跃度
    @GetMapping("/user-rank")
    public List<Map<String, Object>> getUserRank() {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
            "SELECT nickname, vote_count FROM ana_user_rank ORDER BY vote_count DESC LIMIT 10");
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Map<String, Object> item = new HashMap<>();
            item.put("nickname", row.get("nickname"));
            item.put("voteCount", row.get("vote_count"));
            result.add(item);
        }
        return result;
    }

    // 6. 评论情感分布（饼图格式，转成中文标签）
    @GetMapping("/sentiment")
    public List<Map<String, Object>> getSentiment() {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
            "SELECT sentiment, `count` FROM ana_sentiment");
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Map<String, Object> item = new HashMap<>();
            String s = (String) row.get("sentiment");
            String name = s;
            if ("POSITIVE".equals(s)) name = "正面";
            else if ("NEGATIVE".equals(s)) name = "负面";
            else if ("NEUTRAL".equals(s)) name = "中性";
            item.put("name", name);
            item.put("value", row.get("count"));
            result.add(item);
        }
        return result;
    }

    // 7. 时段投票分布
    @GetMapping("/hourly-votes")
    public List<Map<String, Object>> getHourlyVotes() {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
            "SELECT hour, vote_count FROM ana_hourly_votes ORDER BY hour");
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Map<String, Object> item = new HashMap<>();
            item.put("hourOfDay", row.get("hour"));
            item.put("voteCount", row.get("vote_count"));
            result.add(item);
        }
        return result;
    }
}