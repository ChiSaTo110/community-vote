package com.example.service;

import com.example.entity.Topic;
import com.example.entity.VoteOption;
import com.example.repository.TopicRepository;
import com.example.repository.VoteOptionRepository;
import com.example.repository.VoteRecordRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;
    private final VoteOptionRepository voteOptionRepository;
    private final VoteRecordRepository voteRecordRepository;

    /**
     * 创建投票
     * @param title 标题
     * @param description 描述（可选）
     * @param type 题型：1单选 2多选 3填空
     * @param optionTexts 选项文本列表
     * @param creatorIp 创建者IP
     * @return 创建后的投票对象（含ID）
     */
    @Transactional
public Topic createTopic(String title, String description, Integer type, 
                         List<String> optionTexts, String creatorIp, Long userId) {
    Topic topic = new Topic();
    topic.setTitle(title);
    topic.setDescription(description);
    topic.setType(type);
    topic.setStatus(1);
    topic.setCreatorIp(creatorIp);
    topic.setUserId(userId);  // 新增：关联用户

    Topic savedTopic = topicRepository.save(topic);

    if (optionTexts != null && !optionTexts.isEmpty()) {
        for (int i = 0; i < optionTexts.size(); i++) {
            VoteOption option = new VoteOption();
            option.setTopic(savedTopic);
            option.setOptionText(optionTexts.get(i));
            option.setSortOrder(i);
            voteOptionRepository.save(option);
        }
    }

    return topicRepository.findById(savedTopic.getId()).orElse(savedTopic);
}

    /**
     * 根据ID获取投票详情（含选项列表）
     * @param id 投票ID
     * @return Topic 对象，如果不存在返回 null
     */
    public Topic getTopicById(Long id) {
        return topicRepository.findById(id).orElse(null);
    }

    /**
 * 随机获取N个话题
 */
public List<Topic> getRandomTopics(int size) {
    List<Topic> all = topicRepository.findAll();
    if (all.size() <= size) {
        return all;
    }
    Collections.shuffle(all);
    return all.subList(0, size);
}

/**
 * 按标题搜索话题
 */
public List<Topic> searchTopics(String keyword) {
    return topicRepository.findByTitleContainingIgnoreCase(keyword);
}

/**
 * 获取热门话题（按投票记录数排序）
 */
public List<Topic> getHotTopics(int size) {
    List<Topic> all = topicRepository.findAll();
    all.sort((t1, t2) -> {
        long count1 = voteRecordRepository.countByTopicId(t1.getId());
        long count2 = voteRecordRepository.countByTopicId(t2.getId());
        return Long.compare(count2, count1);
    });
    if (all.size() <= size) {
        return all;
    }
    return all.subList(0, size);
}
}