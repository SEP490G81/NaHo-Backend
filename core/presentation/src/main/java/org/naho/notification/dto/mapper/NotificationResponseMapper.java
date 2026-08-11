package org.naho.notification.dto.mapper;

import org.naho.notification.dto.response.NotificationResponse;
import org.naho.notification.result.NotificationResult;
import org.springframework.stereotype.Component;

@Component
public class NotificationResponseMapper {

    public NotificationResponse resultToResponse(NotificationResult result) {
        if (result == null) {
            return null;
        }


        return NotificationResponse.builder()
                .id(result.id())
                .type(result.type())
                .title(result.title())
                .content(result.content())
                .isRead(result.isRead())
                .targetUrl(result.targetUrl())

                .createdTime(result.createdTime())
                .build();
    }
}
