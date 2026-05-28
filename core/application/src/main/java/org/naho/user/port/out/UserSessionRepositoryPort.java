package org.naho.user.port.out;

import org.naho.user.model.UserSession;
import org.naho.user.type.SessionRevokedReason;

import java.time.Instant;

public interface UserSessionRepositoryPort {
    UserSession save(UserSession userSession);

    void revokeActiveSessionsByUserIdAndDeviceId(
            Long userId,
            String deviceId,
            Instant revokedAt,
            SessionRevokedReason reason
    );

    void revokeActiveSessionsByUserIdAndUserSessionId(
            Long userId,
            Long userSessionId,
            Instant revokedAt,
            SessionRevokedReason reason
    );
}
