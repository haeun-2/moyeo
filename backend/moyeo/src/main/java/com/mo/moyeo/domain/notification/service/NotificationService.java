package com.mo.moyeo.domain.notification.service;

import com.mo.moyeo.common.paging.PageResponse;
import com.mo.moyeo.domain.fcm.dto.FcmMessageDTO;
import com.mo.moyeo.domain.notification.dto.NotificationResponse;
import com.mo.moyeo.domain.notification.entity.Notification;
import com.mo.moyeo.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
                .data(message.getData())
                .receivedAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }

    public PageResponse<NotificationResponse> findAllByUserId(Long userId, Pageable pageable) {

        Slice<Notification> notifications = notificationRepository.findByUserId(userId, pageable);
        return PageResponse.from(notifications, NotificationResponse::from);
    }
}
