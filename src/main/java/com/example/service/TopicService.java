package com.example.service;

import com.example.entity.Topic;
import com.example.entity.VoteOption;
import com.example.repository.TopicRepository;
import com.example.repository.VoteOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;
    private final VoteOptionRepository voteOptionRepository;

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
                             List<String> optionTexts, String creatorIp) {
        // 1. 创建 Topic 对象
        Topic topic = new Topic();
        topic.setTitle(title);
        topic.setDescription(description);
        topic.setType(type);
        topic.setStatus(1); // 默认进行中
        topic.setCreatorIp(creatorIp);

        // 2. 先保存 Topic（生成ID）
        Topic savedTopic = topicRepository.save(topic);

        // 3. 创建选项列表并关联到 Topic
        if (optionTexts != null && !optionTexts.isEmpty()) {
            for (int i = 0; i < optionTexts.size(); i++) {
                VoteOption option = new VoteOption();
                option.setTopic(savedTopic);
                option.setOptionText(optionTexts.get(i));
                option.setSortOrder(i);
                voteOptionRepository.save(option);
            }
        }

        // 4. 返回完整的 Topic（包含ID和关联的选项）
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
}