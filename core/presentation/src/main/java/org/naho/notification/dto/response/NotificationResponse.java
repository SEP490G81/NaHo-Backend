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
    // We send metadata as an Object so Jackson serializes it as nested JSON instead of a string
    // if it's already a string in the domain, we might need to parse it in the mapper, 
    // or just let the client parse it. For simplicity, we send it as a raw string if we didn't parse.
    // Ideally it's mapped to a Map<String, Object> or JsonNode. We will return it as Object.
    private final Object metadata;
    private final Instant createdTime;
}
