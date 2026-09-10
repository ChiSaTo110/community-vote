package com.example.repository;

import com.example.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByTopicIdOrderByCreateTimeDesc(Long topicId);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.topicId = :topicId")
    Long countByTopicId(@Param("topicId") Long topicId);
}