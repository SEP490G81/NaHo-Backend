package org.naho.notification.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.notification.dto.response.NotificationResponse;
import org.naho.notification.event.NotificationCreatedEvent;
import org.naho.user.config.UserSessionConnectionManager;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationPushAdapter {

    private final UserSessionConnectionManager userSessionConnectionManager;

    @EventListener
    public void handleNotificationCreated(NotificationCreatedEvent event) {
        try {
            NotificationResponse response = NotificationResponse.builder()
                    .id(event.getNotification().getId())
                    .type(event.getNotification().getType())
                    .title(event.getNotification().getTitle())
                    .content(event.getNotification().getContent())
                    .isRead(event.getNotification().isRead())
                    .targetUrl(event.getNotification().getTargetUrl())
                    .createdTime(event.getNotification().getCreatedTime())
                    .build();

            userSessionConnectionManager.sendEventToUser(event.getUserId(), "NOTIFICATION", response);
        } catch (Exception e) {
            log.error("Failed to push notification via SSE for user: {}", event.getUserId(), e);
        }
    }
}
