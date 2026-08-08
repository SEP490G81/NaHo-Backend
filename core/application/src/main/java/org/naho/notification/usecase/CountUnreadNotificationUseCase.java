package org.naho.notification.usecase;

import org.naho.notification.port.in.CountUnreadNotificationInputPort;
import org.naho.notification.port.out.NotificationRepositoryPort;

public class CountUnreadNotificationUseCase implements CountUnreadNotificationInputPort {

    private final NotificationRepositoryPort notificationRepositoryPort;

    public CountUnreadNotificationUseCase(NotificationRepositoryPort notificationRepositoryPort) {
        this.notificationRepositoryPort = notificationRepositoryPort;
    }

    @Override
    public long countUnreadByUserId(Long userId) {
        return notificationRepositoryPort.countUnreadByUserId(userId);
    }
}
