package com.example.repository;

import com.example.entity.VoteRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRecordRepository extends JpaRepository<VoteRecord, Long> {

    @Query("SELECT COUNT(v) FROM VoteRecord v WHERE v.option.id = :optionId")
    Long countByOptionId(@Param("optionId") Long optionId);

    boolean existsByTopicIdAndIpAddress(Long topicId, String ipAddress);
}