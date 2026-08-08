package org.naho.notification.dto.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.notification.dto.response.NotificationResponse;
import org.naho.notification.result.NotificationResult;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationResponseMapper {

    private final ObjectMapper objectMapper;

    public NotificationResponse resultToResponse(NotificationResult result) {
        if (result == null) {
            return null;
        }

        Object metadataObj = null;
        if (result.metadata() != null && !result.metadata().isBlank()) {
            try {
                // Parse JSON string to Map so it returns as nested JSON in REST response
                metadataObj = objectMapper.readValue(result.metadata(), new TypeReference<Map<String, Object>>() {});
            } catch (JsonProcessingException e) {
                log.warn("Failed to parse metadata JSON for notification {}", result.id(), e);
                // Fallback to raw string
                metadataObj = result.metadata();
            }
        }

        return NotificationResponse.builder()
                .id(result.id())
                .type(result.type())
                .title(result.title())
                .content(result.content())
                .isRead(result.isRead())
                .targetUrl(result.targetUrl())
                .metadata(metadataObj)
                .createdTime(result.createdTime())
                .build();
    }
}
