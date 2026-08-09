package org.naho.notification.port.in;

public interface CountUnreadNotificationInputPort {
    long countUnreadByUserId(Long userId);
}
