package org.naho.notification.usecase;

import org.naho.notification.command.GetListNotificationCommand;
import org.naho.notification.model.Notification;
import org.naho.notification.port.in.GetListNotificationByUserInputPort;
import org.naho.notification.port.out.NotificationRepositoryPort;
import org.naho.notification.result.NotificationResult;

import java.util.List;

public class GetListNotificationUseCase implements GetListNotificationByUserInputPort {

    private final NotificationRepositoryPort notificationRepositoryPort;

    public GetListNotificationUseCase(NotificationRepositoryPort notificationRepositoryPort) {
        this.notificationRepositoryPort = notificationRepositoryPort;
    }

    @Override
    public List<NotificationResult> getListByUserId(GetListNotificationCommand command) {
        List<Notification> notifications = notificationRepositoryPort.findByUserId(
                command.userId(),
                command.limit(),
                command.offset()
        );
        return notifications.stream()
                .map(NotificationResult::fromDomain)
                .toList();
    }
}
