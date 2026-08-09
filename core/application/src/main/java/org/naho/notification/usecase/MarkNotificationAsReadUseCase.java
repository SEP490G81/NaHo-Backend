package org.naho.notification.usecase;

import org.naho.i18n.message.notification.NotificationDetailMessageKey;
import org.naho.notification.command.MarkNotificationAsReadCommand;
import org.naho.notification.model.Notification;
import org.naho.notification.port.in.MarkNotificationAsReadInputPort;
import org.naho.notification.port.out.NotificationRepositoryPort;
import org.naho.notification.result.NotificationResult;
import org.naho.shared.exception.DomainException;
import org.naho.shared.exception.ErrorCode;

public class MarkNotificationAsReadUseCase implements MarkNotificationAsReadInputPort {

    private final NotificationRepositoryPort notificationRepositoryPort;

    public MarkNotificationAsReadUseCase(NotificationRepositoryPort notificationRepositoryPort) {
        this.notificationRepositoryPort = notificationRepositoryPort;
    }

    @Override
    public NotificationResult markAsRead(MarkNotificationAsReadCommand command) {
        Notification notification = notificationRepositoryPort.findById(command.notificationId());

        if (notification == null || !notification.getUserId().equals(command.userId())) {
            throw new DomainException(
                    new ErrorCode() {
                        @Override
                        public String getCode() {
                            return "NOTIFICATION_NOT_FOUND";
                        }

                        @Override
                        public String getTitleKey() {
                            return "notification.title.not-found";
                        }

                        @Override
                        public int getStatusCode() {
                            return 404;
                        }
                    },
                    NotificationDetailMessageKey.NOTIFICATION_NOT_FOUND
            );
        }

        Notification markedNotification = notification.markAsRead();
        Notification savedNotification = notificationRepositoryPort.save(markedNotification);

        return NotificationResult.fromDomain(savedNotification);
    }
}
