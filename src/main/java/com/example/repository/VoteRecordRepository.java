package com.example.repository;

import com.example.entity.VoteRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRecordRepository extends JpaRepository<VoteRecord, Long> {

    @Query("SELECT COUNT(v) FROM VoteRecord v WHERE v.option.id = :optionId")
    Long countByOptionId(@Param("optionId") Long optionId);

    boolean existsByTopicIdAndIpAddress(Long topicId, String ipAddress);

    // 新增：获取用户参与过的所有话题ID（去重）
    @Query("SELECT DISTINCT v.topic.id FROM VoteRecord v WHERE v.topic.userId = :userId")
    List<Long> findDistinctTopicIdsByUserId(@Param("userId") Long userId);

    // 新增：统计某个话题的总投票数
    @Query("SELECT COUNT(v) FROM VoteRecord v WHERE v.topic.id = :topicId")
    Long countByTopicId(@Param("topicId") Long topicId);
}