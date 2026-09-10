package com.example.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "vote_record", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"topic_id", "ip_address"})
})
public class VoteRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    // 修改：允许为空（填空时没有选项）
    @ManyToOne
    @JoinColumn(name = "option_id")
    private VoteOption option;

    @Column(name = "ip_address", nullable = false, length = 50)
    private String ipAddress;

    // 新增：关联投票用户ID（未登录用户为null）
    @Column(name = "user_id")
    private Long userId;

    // 新增：填空内容
    @Column(name = "fill_content", columnDefinition = "TEXT")
    private String fillContent;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
