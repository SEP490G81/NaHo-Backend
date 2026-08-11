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

    /**
     * Thực hiện thu hồi toàn bộ các session đang hoạt động của người dùng
     *
     * @param userId user id
     * @param reason lí do thu hồi
     */
    @Override
    public void revokeAllActiveSessionsByUserId(Long userId, SessionRevokedReason reason) {
        // Tìm toàn bộ các session đang hoạt động của người dùng
        List<UserSession> userSessions = userSessionRepositoryPort.findAllActiveSessionsByUserId(userId);
        Instant now = Instant.now();

        // Thu hồi toàn bộ các session đang hoạt động
        userSessionRepositoryPort.revokeAllActiveSessionsByUserId(userId, now, reason);

        // Lặp qua các session và nếu access token của session đó còn hạn
        // thì sẽ thêm vào blacklist trên redis
        // khi request kèm theo AT thì sẽ phải qua bước kiểm tra xem
        // AT có trong blacklist không
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
