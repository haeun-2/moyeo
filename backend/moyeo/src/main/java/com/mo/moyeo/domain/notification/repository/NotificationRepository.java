package com.mo.moyeo.domain.notification.repository;

import com.mo.moyeo.domain.notification.entity.Notification;
import org.springframework.data.repository.CrudRepository;

public interface NotificationRepository extends CrudRepository<Notification, Long>, NotificationRepositoryCustom {
}
