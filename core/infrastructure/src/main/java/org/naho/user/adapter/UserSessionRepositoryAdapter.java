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

    @Override
    public void revokeActiveSessionsByUserIdAndDeviceId(
            Long userId,
            String deviceId,
            Instant revokedAt,
            SessionRevokedReason reason
    ) {
        userSessionQueryMapper.revokeActiveSessionsByUserIdAndDeviceId(
                userId,
                deviceId,
                revokedAt,
                reason
        );
    }

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
    public List<Long> findAllActiveSessionIdsByUserId(Long userId) {
        return userSessionQueryMapper.findAllActiveSessionIdsByUserId(userId);
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
    public UserSession findBySessionId(Long sessionId) {
        if (sessionId == null) {
            throw new InfrastructureException(
                    UserSessionErrorCode.USER_SESSION_NOT_FOUND,
                    UserSessionDetailMessageKey.USER_SESSION_ID_NULL
            );
        }

        UserSessionEntity userSessionEntity = userSessionJpaRepository
                .findById(sessionId)
                .orElseThrow(() -> new InfrastructureException(
                        UserSessionErrorCode.USER_SESSION_NOT_FOUND,
                        UserSessionDetailMessageKey.USER_SESSION_ID_NOT_FOUND,
                        sessionId
                ));
        return userSessionEntityMapper.entityToDomain(userSessionEntity);
    }

    @Override
    public void verifyUserSession(UserSession userSession, Instant now) {
        if (userSession.isExpired()) {
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
