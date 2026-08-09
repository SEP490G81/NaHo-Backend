package org.naho.notification.port.in;

import org.naho.notification.command.MarkNotificationAsReadCommand;
import org.naho.notification.result.NotificationResult;

public interface MarkNotificationAsReadInputPort {
    NotificationResult markAsRead(MarkNotificationAsReadCommand command);
}
