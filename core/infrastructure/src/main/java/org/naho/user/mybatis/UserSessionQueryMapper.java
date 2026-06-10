package org.naho.user.mybatis;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.naho.user.type.SessionRevokedReason;

import java.time.Instant;

@Mapper
public interface UserSessionQueryMapper {
    void revokeActiveSessionsByUserIdAndDeviceId(
            @Param("userId") Long userId,
            @Param("deviceId") String deviceId,
            @Param("revokedAt") Instant revokedAt,
            @Param("reason") SessionRevokedReason reason
    );

    void revokeActiveSessionsByUserIdAndUserSessionId(
            @Param("userId") Long userId,
            @Param("userSessionId") Long userSessionId,
            @Param("revokedAt") Instant revokedAt,
            @Param("reason") SessionRevokedReason reason
    );

    void revokeAllActiveSessionsByUserId(
            @Param("userId") Long userId,
            @Param("revokedAt") Instant revokedAt,
            @Param("reason") SessionRevokedReason reason
    );
}
