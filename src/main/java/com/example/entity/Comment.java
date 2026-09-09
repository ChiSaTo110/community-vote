package com.example.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 所属投票主题ID */
    private Long topicId;

    /** 评论内容 */
    @Column(columnDefinition = "TEXT")
    private String content;

    /** 评论者IP地址 */
    private String ipAddress;

    /** 发布时间 */
    private LocalDateTime createTime;

    /** 情感标签：POSITIVE / NEGATIVE / NEUTRAL */
    private String sentiment;

    @PrePersist
    public void prePersist() {
        createTime = LocalDateTime.now();
    }
}

