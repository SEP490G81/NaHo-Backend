package org.naho.notification.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.notification.event.NotificationCreatedEvent;
import org.naho.notification.event.SendNotificationEvent;
import org.naho.notification.model.Notification;
import org.naho.notification.port.out.NotificationRepositoryPort;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationRepositoryPort notificationRepositoryPort;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Async
    @EventListener
    public void handleSendNotificationEvent(SendNotificationEvent event) {
        log.info("Received SendNotificationEvent for user: {}", event.getUserId());
        
        try {
            // 1. Create and save notification to DB
            Notification notification = Notification.builder()
                    .userId(event.getUserId())
                    .type(event.getType())
                    .title(event.getTitle())
                    .content(event.getContent())
                    .targetUrl(event.getTargetUrl())
                    .metadata(event.getMetadata())
                    .isRead(false)
                    .build();
                    
            Notification savedNotification = notificationRepositoryPort.save(notification);
            
            // 2. Publish NotificationCreatedEvent to notify SSE adapter
            applicationEventPublisher.publishEvent(new NotificationCreatedEvent(event.getUserId(), savedNotification));
            
        } catch (Exception e) {
            log.error("Error processing SendNotificationEvent for user: {}", event.getUserId(), e);
        }
    }
}
