package com.mo.moyeo.domain.notification.dto;

import com.mo.moyeo.domain.notification.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private Long transactionId;
    private String title;
    private String body;
    private LocalDateTime receivedAt;

    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
                .transactionId(notification.getTransactionId())
                .title(notification.getTitle())
                .body(notification.getBody())
                .receivedAt(notification.getReceivedAt())
                .build();
    }
}
