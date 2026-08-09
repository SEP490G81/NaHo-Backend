package org.naho.notification.mapper;

import org.naho.notification.entity.NotificationEntity;
import org.naho.notification.model.Notification;
import org.naho.user.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class NotificationEntityMapper {

    public NotificationEntity toEntity(Notification notification) {
        if (notification == null) {
            return null;
        }

        return NotificationEntity.builder()
                .id(notification.getId())
                .user(UserEntity.builder().id(notification.getUserId()).build())
                .type(notification.getType())
                .title(notification.getTitle())
                .content(notification.getContent())
                .isRead(notification.isRead())
                .targetUrl(notification.getTargetUrl())
                .metadata(notification.getMetadata())
                // createdTime is handled by BaseEntity's @CreatedDate if mapped, 
                // but we also have it in domain. We can set it if needed, 
                // but typically BaseEntity handles it on insert.
                .build();
    }

    public Notification toDomain(NotificationEntity entity) {
        if (entity == null) {
            return null;
        }

        return Notification.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .type(entity.getType())
                .title(entity.getTitle())
                .content(entity.getContent())
                .isRead(entity.getIsRead())
                .targetUrl(entity.getTargetUrl())
                .metadata(entity.getMetadata())
                .createdTime(entity.getCreatedTime())
                .build();
    }
}
