package org.naho.notification.command;

public record GetListNotificationCommand(Long userId, int limit, int offset) {
}
