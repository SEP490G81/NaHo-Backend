package org.naho.user.command;

import org.naho.user.type.SessionRevokedReason;

public record ForceLogoutCommand(
        Long userId,
        Long newUserSessionId,
        SessionRevokedReason reason
) {
}
