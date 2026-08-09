package org.naho.notification.event;

import org.naho.notification.model.Notification;

public class NotificationCreatedEvent {

    private final Long userId;
    private final Notification notification;

    public NotificationCreatedEvent(Long userId, Notification notification) {
        this.userId = userId;
        this.notification = notification;
    }

    public Long getUserId() {
        return userId;
    }

    public Notification getNotification() {
        return notification;
    }
}
