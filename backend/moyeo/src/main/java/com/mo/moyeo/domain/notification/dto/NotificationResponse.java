package com.mo.moyeo.domain.notification.dto;

import com.mo.moyeo.domain.notification.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private Long notificationId;
    private Long transactionId;
    private String title;
    private String body;
    private Map<String, String> data;
    private LocalDateTime receivedAt;

    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
                .notificationId(notification.getId())
                .transactionId(notification.getTransactionId())
                .title(notification.getTitle())
                .body(notification.getBody())
                .data(notification.getData())
                .receivedAt(notification.getReceivedAt())
                .build();
    }
}
