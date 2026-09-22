package com.example.config;

import com.example.entity.Topic;
import com.example.entity.User;
import com.example.entity.VoteOption;
import com.example.entity.VoteRecord;
import com.example.repository.TopicRepository;
import com.example.repository.UserRepository;
import com.example.repository.VoteOptionRepository;
import com.example.repository.VoteRecordRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final TopicRepository topicRepository;
    private final VoteOptionRepository voteOptionRepository;
    private final VoteRecordRepository voteRecordRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void run(String... args) {
        // 已有数据则跳过
        if (topicRepository.count() > 0) {
            log.info("数据库已有数据，跳过初始化");
            return;
        }

        log.info("开始初始化示例数据...");

        // 1. 创建测试用户
        User u1 = createUser("admin", "123456", "管理员", "admin@vote.com");
        User u2 = createUser("zhangsan", "123456", "张三", "zs@vote.com");
        User u3 = createUser("lisi", "123456", "李四", "ls@vote.com");

        // 2. 创建多样化的投票话题
        // 单选 - 编程语言
        createTopic(u1, "你最喜欢的编程语言是？", "选一个你最常用的编程语言", 1,
                new String[]{"Java", "Python", "Go", "JavaScript", "C++"},
                new int[]{15, 22, 8, 18, 5});

        // 单选 - 运动
        createTopic(u2, "你最喜欢的运动是什么？", "每周都会做的运动", 1,
                new String[]{"跑步", "篮球", "羽毛球", "游泳", "健身"},
                new int[]{12, 8, 15, 6, 10});

        // 单选 - 美食
        createTopic(u2, "你最喜欢的美食类型？", "周末最想吃的", 1,
                new String[]{"火锅", "烧烤", "日料", "西餐", "家常菜"},
                new int[]{20, 14, 9, 7, 18});

        // 多选 - 电影类型
        createTopic(u3, "你喜欢的电影类型（可多选）", "选所有你喜欢的类型", 2,
                new String[]{"科幻", "动作", "喜剧", "悬疑", "爱情", "动画"},
                new int[]{16, 13, 19, 11, 8, 14});

        // 单选 - 学习方式
        createTopic(u1, "你更喜欢哪种学习方式？", "效率更高的那种", 1,
                new String[]{"看视频", "读书", "动手实践", "跟人讨论"},
                new int[]{11, 6, 21, 9});

        // 单选 - 旅游
        createTopic(u3, "最想去的旅行地是？", "疫情后第一站", 1,
                new String[]{"云南", "西藏", "新疆", "海南", "日本", "欧洲"},
                new int[]{18, 12, 15, 10, 9, 6});

        // 多选 - 数码产品
        createTopic(u2, "你拥有的数码产品（多选）", "选你正在使用的", 2,
                new String[]{"手机", "笔记本", "平板", "耳机", "智能手表"},
                new int[]{30, 22, 15, 25, 8});

        // 单选 - 工作方式
        createTopic(u1, "你更喜欢远程办公还是办公室？", "谈谈你的偏好", 1,
                new String[]{"完全远程", "混合办公", "完全办公室"},
                new int[]{14, 20, 7});

        // 单选 - 宠物
        createTopic(u3, "你喜欢哪种宠物？", "家里有或者想养的", 1,
                new String[]{"猫", "狗", "鱼", "鸟", "仓鼠"},
                new int[]{25, 23, 6, 4, 5});

        // 单选 - 咖啡
        createTopic(u2, "你最常喝的咖啡是？", "日常续命之选", 1,
                new String[]{"美式", "拿铁", "卡布奇诺", "摩卡", "不喝咖啡"},
                new int[]{15, 20, 9, 7, 12});

        log.info("示例数据初始化完成！共创建 {} 个话题", topicRepository.count());
    }

    private User createUser(String username, String password, String nickname, String email) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setNickname(nickname);
        user.setEmail(email);
        return userRepository.save(user);
    }

    /**
     * 创建话题和选项，并模拟投票记录
     * @param voteCounts 每个选项的模拟投票数（用于生成热门数据）
     */
    private void createTopic(User creator, String title, String description, Integer type,
                             String[] optionTexts, int[] voteCounts) {
        Topic topic = new Topic();
        topic.setTitle(title);
        topic.setDescription(description);
        topic.setType(type);
        topic.setStatus(1);
        topic.setCreatorIp("127.0.0.1");
        topic.setUserId(creator.getId());
        Topic savedTopic = topicRepository.save(topic);

        List<VoteOption> options = new ArrayList<>();
        for (int i = 0; i < optionTexts.length; i++) {
            VoteOption opt = new VoteOption();
            opt.setTopic(savedTopic);
            opt.setOptionText(optionTexts[i]);
            opt.setSortOrder(i);
            options.add(voteOptionRepository.save(opt));
        }

        // 模拟投票记录（用不同的虚拟IP）
        for (int i = 0; i < options.size(); i++) {
            int count = (voteCounts != null && i < voteCounts.length) ? voteCounts[i] : 0;
            for (int j = 0; j < count; j++) {
                VoteRecord record = new VoteRecord();
                record.setTopic(savedTopic);
                record.setOption(options.get(i));
                record.setIpAddress("192.168." + (savedTopic.getId() % 256) + "." + (j + 1));
                record.setUserId(null); // 模拟匿名投票
                voteRecordRepository.save(record);
            }
        }
    }
}