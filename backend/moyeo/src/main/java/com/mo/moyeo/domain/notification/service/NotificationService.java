package com.mo.moyeo.domain.notification.service;

import com.mo.moyeo.domain.fcm.dto.FcmMessageDTO;
import com.mo.moyeo.domain.notification.dto.NotificationResponse;
import com.mo.moyeo.domain.notification.entity.Notification;
import com.mo.moyeo.domain.notification.repository.NotificationRepository;
import com.mo.moyeo.domain.transaction.history.entity.BoxHistory;
import com.mo.moyeo.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void createNotification(Long userId, Long transactionId, FcmMessageDTO message) {

        Notification notification = Notification.builder()
                .userId(userId)
                .transactionId(transactionId)
                .title(message.getTitle())
                .body(message.getBody())
                .receivedAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }

    public List<NotificationResponse> findAllByUserId(Long userId) {
        return notificationRepository.findAllByUserId(userId).stream()
                .map(NotificationResponse::from)
                .toList();
    }
}
