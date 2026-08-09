package org.naho.notification.port.out;

import org.naho.notification.model.Notification;

import java.util.List;

public interface NotificationRepositoryPort {
    Notification save(Notification notification);

    List<Notification> findByUserId(Long userId, int limit, int offset);

    long countUnreadByUserId(Long userId);

    Notification findById(Long id);

    void markAllAsReadByUserId(Long userId);
}
