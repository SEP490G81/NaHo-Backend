package org.naho.notification.event;

import org.naho.notification.type.NotificationType;

public class SendNotificationEvent {

    private final Object source;
    private final Long userId;
    private final NotificationType type;
    private final String title;
    private final String content;
    private final String targetUrl;
    private final String metadata;

    public SendNotificationEvent(Object source, Long userId, NotificationType type, String title, String content, String targetUrl, String metadata) {
        this.source = source;
        this.userId = userId;
        this.type = type;
        this.title = title;
        this.content = content;
        this.targetUrl = targetUrl;
        this.metadata = metadata;
    }

    public Object getSource() {
        return source;
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

    public String getTargetUrl() {
        return targetUrl;
    }

    public String getMetadata() {
        return metadata;
    }
}
