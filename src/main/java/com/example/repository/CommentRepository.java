package com.example.repository;

import com.example.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    /** 按发布时间倒序，查询指定投票下的所有评论 */
    List<Comment> findByTopicIdOrderByCreateTimeDesc(Long topicId);
}
