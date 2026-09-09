package com.example.repository;

import com.example.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    // 新增：按用户ID查询，按创建时间倒序
    List<Topic> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    // 新增：按标题模糊搜索（忽略大小写）
    List<Topic> findByTitleContainingIgnoreCase(String keyword);
}