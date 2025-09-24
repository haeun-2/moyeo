package com.mo.moyeo.domain.notification.repository;

import com.mo.moyeo.domain.notification.entity.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.CrudRepository;

public interface NotificationRepository extends CrudRepository<Notification, Long> {
    Slice<Notification> findByUserId(Long userId, Pageable pageable);
}
