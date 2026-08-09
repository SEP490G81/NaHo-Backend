package org.naho.notification.command;

public record MarkNotificationAsReadCommand(Long notificationId, Long userId) {
}
