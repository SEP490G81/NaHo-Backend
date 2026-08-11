package org.naho.user.port.out;

import org.naho.user.type.SessionRevokedReason;

public interface UserSessionServicePort {
    void revokeAllActiveSessionsByUserId(Long userId, SessionRevokedReason reason);
}
