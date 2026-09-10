package com.example.service;

import com.example.entity.Topic;
import com.example.entity.VoteOption;
import com.example.entity.VoteRecord;
import com.example.repository.TopicRepository;
import com.example.repository.VoteOptionRepository;
import com.example.repository.VoteRecordRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class VoteService {

    private static final Logger log = LoggerFactory.getLogger(VoteService.class);

    private final TopicRepository topicRepository;
    private final VoteOptionRepository voteOptionRepository;
    private final VoteRecordRepository voteRecordRepository;

    @Transactional
    public Map<String, Object> vote(Long topicId, List<Long> optionIds, String fillText, String ipAddress, Long userId) {
        Map<String, Object> result = new HashMap<>();
        log.info("收到投票请求：topicId={}, optionIds={}, fillText={}, ip={}, userId={}", topicId, optionIds, fillText, ipAddress, userId);

        // 1. 查询投票是否存在
        Optional<Topic> topicOpt = topicRepository.findById(topicId);
        if (topicOpt.isEmpty()) {
            log.warn("投票不存在：topicId={}", topicId);
            result.put("success", false);
            result.put("message", "投票不存在");
            return result;
        }

        Topic topic = topicOpt.get();

        // 2. 检查投票是否已结束
        if (topic.getStatus() == 2) {
            log.warn("投票已结束：topicId={}", topicId);
            result.put("success", false);
            result.put("message", "该投票已结束");
            return result;
        }

        // 3. IP 限投检查
        boolean alreadyVoted = voteRecordRepository.existsByTopicIdAndIpAddress(topicId, ipAddress);
        if (alreadyVoted) {
            log.warn("IP {} 重复投票，topicId={}", ipAddress, topicId);
            result.put("success", false);
            result.put("message", "您已经投过票了，不能重复投票");
            return result;
        }

        Integer type = topic.getType();

        if (type == 1) { // 单选
            if (optionIds == null || optionIds.size() != 1) {
                log.warn("单选选项数量错误：{}", optionIds);
                result.put("success", false);
                result.put("message", "单选必须选择一个选项");
                return result;
            }
            Long optionId = optionIds.get(0);
            Optional<VoteOption> opt = voteOptionRepository.findById(optionId);
            if (opt.isEmpty() || !opt.get().getTopic().getId().equals(topicId)) {
                log.warn("选项不合法：optionId={}, topicId={}", optionId, topicId);
                result.put("success", false);
                result.put("message", "选项不合法");
                return result;
            }
            saveVoteRecord(topic, opt.get(), null, ipAddress, userId);
            log.info("单选投票成功：topicId={}, optionId={}, ip={}, userId={}", topicId, optionId, ipAddress, userId);

        } else if (type == 2) { // 多选
            if (optionIds == null || optionIds.isEmpty()) {
                log.warn("多选未选任何选项");
                result.put("success", false);
                result.put("message", "请至少选择一个选项");
                return result;
            }
            for (Long id : optionIds) {
                Optional<VoteOption> opt = voteOptionRepository.findById(id);
                if (opt.isEmpty() || !opt.get().getTopic().getId().equals(topicId)) {
                    log.warn("多选包含不合法选项：optionId={}, topicId={}", id, topicId);
                    result.put("success", false);
                    result.put("message", "选项不合法: " + id);
                    return result;
                }
                saveVoteRecord(topic, opt.get(), null, ipAddress, userId);
            }
            log.info("多选投票成功：topicId={}, optionIds={}, ip={}, userId={}", topicId, optionIds, ipAddress, userId);

        } else if (type == 3) { // 填空
            if (fillText == null || fillText.trim().isEmpty()) {
                log.warn("填空内容为空");
                result.put("success", false);
                result.put("message", "请填写内容");
                return result;
            }
            // 真正保存填空记录
            VoteRecord record = new VoteRecord();
            record.setTopic(topic);
            record.setOption(null);
            record.setFillContent(fillText.trim());
            record.setIpAddress(ipAddress);
            record.setUserId(userId);
            voteRecordRepository.save(record);
            log.info("填空投票成功：topicId={}, fillText={}, ip={}, userId={}", topicId, fillText, ipAddress, userId);
        }

        result.put("success", true);
        result.put("message", "投票成功");
        return result;
    }

    /**
     * 保存单条投票记录
     */
    private void saveVoteRecord(Topic topic, VoteOption option, String fillContent, String ipAddress, Long userId) {
        VoteRecord record = new VoteRecord();
        record.setTopic(topic);
        record.setOption(option);
        record.setFillContent(fillContent);
        record.setIpAddress(ipAddress);
        record.setUserId(userId);
        voteRecordRepository.save(record);
    }

    public Map<String, Object> getResults(Long topicId) {
        Map<String, Object> result = new HashMap<>();
        log.info("统计投票结果：topicId={}", topicId);

        Optional<Topic> topicOpt = topicRepository.findById(topicId);
        if (topicOpt.isEmpty()) {
            log.warn("投票不存在：topicId={}", topicId);
            result.put("success", false);
            result.put("message", "投票不存在");
            return result;
        }

        Topic topic = topicOpt.get();
        List<VoteOption> options = voteOptionRepository.findByTopicId(topicId);

        List<String> labels = new ArrayList<>();
        List<Long> values = new ArrayList<>();

        for (VoteOption option : options) {
            Long count = voteRecordRepository.countByOptionId(option.getId());
            labels.add(option.getOptionText());
            values.add(count);
        }

        long total = values.stream().mapToLong(Long::longValue).sum();
        log.info("投票结果统计完成：total={}, labels={}, values={}", total, labels, values);

        result.put("success", true);
        result.put("data", Map.of(
            "labels", labels,
            "values", values,
            "total", total
        ));

        return result;
    }
}
