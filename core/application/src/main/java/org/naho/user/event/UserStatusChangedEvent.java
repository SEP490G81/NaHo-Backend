package org.naho.user.event;

import org.naho.user.type.UserStatus;

public record UserStatusChangedEvent(
        Long userId,
        String email,
        String fullName,
        UserStatus newStatus
) {
}
