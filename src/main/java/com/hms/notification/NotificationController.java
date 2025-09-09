package com.hms.notification;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationRepository notifications;

    public NotificationController(NotificationRepository notifications) {
        this.notifications = notifications;
    }

    @GetMapping
    public List<Notification> list(Authentication auth) {
        return notifications.findTop10ByUsernameOrderByCreatedAtDesc(auth.getName());
    }

    @GetMapping("/count")
    public long unreadCount(Authentication auth) {
        return notifications.countByUsernameAndReadIsFalse(auth.getName());
    }

    @PostMapping("/read/{id}")
    public void markRead(@PathVariable Long id, Authentication auth) {
        notifications.findById(id).ifPresent(n -> {
            if (n.getUsername().equals(auth.getName())) {
                n.setRead(true);
                notifications.save(n);
            }
        });
    }

    @PostMapping("/read-all")
    public void markAllRead(Authentication auth) {
        notifications.findTop10ByUsernameOrderByCreatedAtDesc(auth.getName()).forEach(n -> {
            if (!n.isRead()) {
                n.setRead(true);
                notifications.save(n);
            }
        });
    }
}
