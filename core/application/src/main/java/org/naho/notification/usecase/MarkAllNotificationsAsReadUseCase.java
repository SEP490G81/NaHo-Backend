package org.naho.notification.usecase;

import org.naho.notification.port.in.MarkAllNotificationsAsReadInputPort;
import org.naho.notification.port.out.NotificationRepositoryPort;

public class MarkAllNotificationsAsReadUseCase implements MarkAllNotificationsAsReadInputPort {

    private final NotificationRepositoryPort notificationRepositoryPort;

    public MarkAllNotificationsAsReadUseCase(NotificationRepositoryPort notificationRepositoryPort) {
        this.notificationRepositoryPort = notificationRepositoryPort;
    }

    @Override
    public void markAllAsRead(Long userId) {
        notificationRepositoryPort.markAllAsReadByUserId(userId);
    }
}
