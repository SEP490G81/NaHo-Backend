package org.naho.notification.result;

import org.naho.notification.model.Notification;
import org.naho.notification.type.NotificationType;

import java.time.Instant;

public record NotificationResult(
        Long id,
        Long userId,
        NotificationType type,
        String title,
        String content,
        boolean isRead,
        String targetUrl,
        String metadata,
        Instant createdTime
) {
    public static NotificationResult fromDomain(Notification notification) {
        return new NotificationResult(
                notification.getId(),
                notification.getUserId(),
                notification.getType(),
                notification.getTitle(),
                notification.getContent(),
                notification.isRead(),
                notification.getTargetUrl(),
                notification.getMetadata(),
                notification.getCreatedTime()
        );
    }
}
