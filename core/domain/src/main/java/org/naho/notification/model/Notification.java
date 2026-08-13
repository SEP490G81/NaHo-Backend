package org.naho.notification.model;

import org.naho.i18n.message.notification.NotificationDetailMessageKey;
import org.naho.notification.exception.NotificationDomainErrorCode;
import org.naho.notification.type.NotificationType;
import org.naho.shared.exception.DomainException;

import java.time.Instant;

public class Notification {
    private final Long id;
    private final Long userId;
    private final NotificationType type;
    private final String title;
    private final String content;
    private final boolean isRead;
    private final String targetUrl;

    private final Instant createdTime;

    private Notification(Builder builder) {
        this.id = builder.id;
        this.userId = builder.userId;
        this.type = builder.type;
        this.title = builder.title;
        this.content = builder.content;
        this.isRead = builder.isRead;
        this.targetUrl = builder.targetUrl;

        this.createdTime = builder.createdTime;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public NotificationType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public boolean isRead() {
        return isRead;
    }

    public String getTargetUrl() {
        return targetUrl;
    }


    public Instant getCreatedTime() {
        return createdTime;
    }

    public Notification markAsRead() {
        return builder()
                .id(this.id)
                .userId(this.userId)
                .type(this.type)
                .title(this.title)
                .content(this.content)
                .isRead(true)
                .targetUrl(this.targetUrl)

                .createdTime(this.createdTime)
                .build();
    }

    public static class Builder {
        private Long id;
        private Long userId;
        private NotificationType type;
        private String title;
        private String content;
        private boolean isRead;
        private String targetUrl;

        private Instant createdTime;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder type(NotificationType type) {
            this.type = type;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder isRead(boolean isRead) {
            this.isRead = isRead;
            return this;
        }

        public Builder targetUrl(String targetUrl) {
            this.targetUrl = targetUrl;
            return this;
        }


        public Builder createdTime(Instant createdTime) {
            this.createdTime = createdTime;
            return this;
        }

        public Notification build() {
            if (userId == null) {
                throw new DomainException(
                        NotificationDomainErrorCode.NOTIFICATION_USER_ID_NOT_VALID,
                        NotificationDetailMessageKey.NOTIFICATION_USER_ID_BLANK
                );
            }

            if (type == null) {
                throw new DomainException(
                        NotificationDomainErrorCode.NOTIFICATION_TYPE_NOT_VALID,
                        NotificationDetailMessageKey.NOTIFICATION_TYPE_BLANK
                );
            }

            if (title == null || title.isBlank()) {
                throw new DomainException(
                        NotificationDomainErrorCode.NOTIFICATION_TITLE_NOT_VALID,
                        NotificationDetailMessageKey.NOTIFICATION_TITLE_BLANK
                );
            }

            if (content == null || content.isBlank()) {
                throw new DomainException(
                        NotificationDomainErrorCode.NOTIFICATION_CONTENT_NOT_VALID,
                        NotificationDetailMessageKey.NOTIFICATION_CONTENT_BLANK
                );
            }

            if (createdTime == null) {
                this.createdTime = Instant.now();
            }

            return new Notification(this);
        }
    }
}
