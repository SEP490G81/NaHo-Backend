package org.naho.notification.adapter;

import lombok.extern.slf4j.Slf4j;
import org.naho.notification.event.NotificationCreatedEvent;
import org.naho.notification.model.Notification;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class SseNotificationAdapter {

    // Store active SSE connections mapped by userId
    private final Map<Long, SseEmitter> userEmitters = new ConcurrentHashMap<>();

    public SseEmitter createEmitter(Long userId) {
        // Timeout is set to 30 minutes (or could be 0/null for infinite)
        // Usually it's better to let client reconnect when it times out
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L);

        userEmitters.put(userId, emitter);

        emitter.onCompletion(() -> {
            log.debug("SSE Emitter completed for user: {}", userId);
            userEmitters.remove(userId);
        });

        emitter.onTimeout(() -> {
            log.debug("SSE Emitter timed out for user: {}", userId);
            emitter.complete();
            userEmitters.remove(userId);
        });

        emitter.onError((e) -> {
            log.debug("SSE Emitter error for user: {}", userId, e);
            emitter.completeWithError(e);
            userEmitters.remove(userId);
        });

        // Send an initial dummy event to open the connection immediately
        try {
            emitter.send(SseEmitter.event()
                    .name("INIT")
                    .data("Connected to SSE stream"));
        } catch (IOException e) {
            log.warn("Failed to send init event for user: {}", userId);
            emitter.completeWithError(e);
        }

        return emitter;
    }

    @EventListener
    public void pushNotificationToUser(NotificationCreatedEvent event) {
        Long userId = event.getUserId();
        Notification notification = event.getNotification();

        SseEmitter emitter = userEmitters.get(userId);
        if (emitter != null) {
            try {
                // You may want to convert Notification to a DTO here or let Jackson do it
                emitter.send(SseEmitter.event()
                        .name("NOTIFICATION")
                        .data(notification));
                log.info("Successfully pushed notification to user: {}", userId);
            } catch (IOException e) {
                log.warn("Failed to push notification to user: {}. Connection might be dead.", userId);
                emitter.completeWithError(e);
                userEmitters.remove(userId);
            }
        } else {
            log.debug("No active SSE connection for user: {}. Notification saved to DB only.", userId);
        }
    }
}
