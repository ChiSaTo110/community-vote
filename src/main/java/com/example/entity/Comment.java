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
    @Column(name = "topic_id")
    private Long topicId;

    /** 评论者用户ID（登录用户） */
    @Column(name = "user_id")
    private Long userId;

    /** 评论内容 */
    @Column(columnDefinition = "TEXT")
    private String content;

    /** 评论者IP地址 */
    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    /** 发布时间 */
    @Column(name = "create_time")
    private LocalDateTime createTime;

    /** 情感标签：POSITIVE / NEGATIVE / NEUTRAL */
    @Column(length = 20)
    private String sentiment;

    @PrePersist
    public void prePersist() {
        createTime = LocalDateTime.now();
    }
}