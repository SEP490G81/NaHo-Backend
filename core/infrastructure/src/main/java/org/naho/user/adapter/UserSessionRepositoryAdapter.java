package org.naho.user.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.i18n.message.user.UserSessionDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.exception.InfrastructureException;
import org.naho.user.entity.UserEntity;
import org.naho.user.entity.UserSessionEntity;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.exception.UserSessionErrorCode;
import org.naho.user.mapper.UserSessionEntityMapper;
import org.naho.user.model.UserSession;
import org.naho.user.mybatis.UserSessionQueryMapper;
import org.naho.user.port.out.UserSessionRepositoryPort;
import org.naho.user.repository.UserJpaRepository;
import org.naho.user.repository.UserSessionJpaRepository;
import org.naho.user.type.SessionRevokedReason;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserSessionRepositoryAdapter implements UserSessionRepositoryPort {
    private final UserSessionJpaRepository userSessionJpaRepository;
    private final UserSessionEntityMapper userSessionEntityMapper;
    private final UserJpaRepository userJpaRepository;
    private final UserSessionQueryMapper userSessionQueryMapper;

    @Override
    public UserSession save(UserSession userSession) {
        UserEntity userEntity = userJpaRepository.getReferenceById(userSession.getUserId());
        UserSessionEntity userSessionEntity = userSessionEntityMapper.domainToEntity(userSession);

        userSessionEntity.setUser(userEntity);

        UserSessionEntity savedUserSessionEntity = userSessionJpaRepository.save(userSessionEntity);
        return userSessionEntityMapper.entityToDomain(savedUserSessionEntity);
    }

    /**
     * Thu hồi session của người dùng theo user id và user session id
     * Sử dụng cho việc logout (để thu hồi phiên đăng nhập hiện tại của người dùng)
     *
     * @param userId        user id
     * @param userSessionId user session id
     * @param revokedAt     thời điểm bị thu hồi (now)
     * @param reason        lí do thu hồi
     */
    @Override
    public void revokeActiveSessionsByUserIdAndUserSessionId(
            Long userId,
            Long userSessionId,
            Instant revokedAt,
            SessionRevokedReason reason
    ) {
        userSessionQueryMapper.revokeActiveSessionsByUserIdAndUserSessionId(
                userId,
                userSessionId,
                revokedAt,
                reason
        );
    }

    /**
     * Thu hồi toàn bộ các session đang hoạt động của người dùng
     * Hiện tại sử dụng cho việc khi đăng nhập trên thiết bị khác => logout toàn bộ
     * Và sử dụng khi 1 tài khoản bị UNACTIVE
     *
     * @param userId    user id
     * @param revokedAt thời điểm thu hồi (now)
     * @param reason    lí do thu hồi
     */
    @Override
    public void revokeAllActiveSessionsByUserId(
            Long userId,
            Instant revokedAt,
            SessionRevokedReason reason
    ) {
        userSessionQueryMapper.revokeAllActiveSessionsByUserId(
                userId,
                revokedAt,
                reason
        );
    }

    @Override
    public List<UserSession> findAllActiveSessionsByUserId(Long userId) {
        List<UserSessionEntity> userSessionEntities = userSessionJpaRepository
                .findAllByUser_IdAndRevokedAtIsNullAndRevokedReasonIsNull(userId);
        return userSessionEntities.stream()
                .map(userSessionEntityMapper::entityToDomain)
                .toList();
    }

    @Override
    public UserSession findByUserId(Long userId) {
        if (userId == null) {
            throw new InfrastructureException(
                    UserSessionErrorCode.USER_SESSION_USER_ID_INVALID,
                    UserSessionDetailMessageKey.USER_SESSION_USER_ID_BLANK
            );
        }
        UserSessionEntity userSessionEntity = userSessionJpaRepository.findByUser_Id(userId)
                .orElseThrow(() -> new InfrastructureException(
                                UserSessionErrorCode.USER_SESSION_NOT_FOUND,
                                UserSessionDetailMessageKey.USER_SESSION_USER_ID_NOT_FOUND,
                                userId
                        )
                );
        return userSessionEntityMapper.entityToDomain(userSessionEntity);
    }

    @Override
    public UserSession findByHashRefreshToken(String hashRefreshToken) {
        UserSessionEntity userSessionEntity = userSessionJpaRepository
                .findByHashRefreshToken(hashRefreshToken)
                .orElseThrow(() -> new InfrastructureException(
                        UserErrorCode.USER_INVALID_REFRESH_TOKEN,
                        UserDetailMessageKey.USER_REFRESH_TOKEN_NOT_FOUND
                ));
        return userSessionEntityMapper.entityToDomain(userSessionEntity);
    }

    @Override
    public void verifyUserSession(UserSession userSession, Instant now) {
        if (userSession.isRefreshTokenExpired()) {
            userSession.setRevokedAt(now);
            userSession.setRevokedReason(SessionRevokedReason.EXPIRED);
            this.save(userSession);

            throw new ApplicationException(
                    UserErrorCode.USER_INVALID_REFRESH_TOKEN,
                    UserDetailMessageKey.USER_REFRESH_TOKEN_EXPIRED
            );
        }

        if (userSession.isRevoked()) {
            userSession.setRevokedReason(SessionRevokedReason.TOKEN_REUSE_DETECTED);
            this.save(userSession);

            throw new ApplicationException(
                    UserErrorCode.USER_INVALID_REFRESH_TOKEN,
                    UserDetailMessageKey.USER_REFRESH_TOKEN_REVOKED
            );
        }
    }
}
