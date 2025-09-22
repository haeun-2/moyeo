package com.mo.moyeo.domain.notification.repository;

import com.mo.moyeo.domain.notification.entity.Notification;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface NotificationRepository extends CrudRepository<Notification, Long> {

    List<Notification> findAllByUserIdOrderByReceivedAtDesc(Long userId);
}
