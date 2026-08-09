package org.naho.notification.port.in;

import org.naho.notification.command.GetListNotificationCommand;
import org.naho.notification.result.NotificationResult;

import java.util.List;

public interface GetListNotificationByUserInputPort {
    List<NotificationResult> getListByUserId(GetListNotificationCommand command);
}
