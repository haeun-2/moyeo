package com.mo.moyeo.domain.notification.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Entity
@Builder
@AllArgsConstructor
@Table(name = "notifications")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "transaction_id", nullable = false)
    private Long transactionId;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "body", nullable = false)
    private String body;

    @Column(name = "data")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, String> data;

    @Column(name = "received_at")
    private LocalDateTime receivedAt;
}
