package com.mo.moyeo.domain.notification.repository;

import com.mo.moyeo.domain.notification.dto.NotificationSearchCondition;
import com.mo.moyeo.domain.notification.entity.Notification;
import org.springframework.data.domain.Slice;

public interface NotificationRepositoryCustom {
    Slice<Notification> search(Long userId, NotificationSearchCondition condition);
}