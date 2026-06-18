package org.naho.user.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.user.model.UserSession;
import org.naho.user.port.out.UserSessionRepositoryPort;
import org.naho.user.port.out.UserSessionServicePort;
import org.naho.user.type.SessionRevokedReason;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserSessionServiceAdapter implements UserSessionServicePort {
    public static final String REDIS_KEY_PREFIX = "auth:user_session:";
    private final UserSessionRepositoryPort userSessionRepositoryPort;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void revokeAllSessionsByUserId(Long userId, SessionRevokedReason reason) {
        List<UserSession> userSessions = userSessionRepositoryPort.findAllActiveSessionsByUserId(userId);
        Instant now = Instant.now();

        userSessionRepositoryPort.revokeAllActiveSessionsByUserId(userId, now, reason);

        for (UserSession userSession : userSessions) {
            Duration ttl = Duration.between(now, userSession.getAccessTokenExpiresAt());

            if (ttl.isNegative() || ttl.isZero()) {
                continue;
            }

            String redisKey = REDIS_KEY_PREFIX + userSession.getId();

            redisTemplate.opsForValue().set(redisKey, reason.name(), ttl);
        }
    }
}
