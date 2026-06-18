package org.naho.user.port.out;

import org.naho.user.type.SessionRevokedReason;

public interface UserSessionServicePort {
    void revokeAllSessionsByUserId(Long userId, SessionRevokedReason reason);
}
