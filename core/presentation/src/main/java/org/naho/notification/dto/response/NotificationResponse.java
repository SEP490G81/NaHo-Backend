package org.naho.notification.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.naho.notification.type.NotificationType;

import java.time.Instant;

@Getter
@Builder
public class NotificationResponse {
    private final Long id;
    private final NotificationType type;
    private final String title;
    private final String content;
    private final boolean isRead;
    private final String targetUrl;

    private final Instant createdTime;
}
