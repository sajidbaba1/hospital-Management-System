package com.hms.notification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findTop10ByUsernameOrderByCreatedAtDesc(String username);
    long countByUsernameAndReadIsFalse(String username);
}
