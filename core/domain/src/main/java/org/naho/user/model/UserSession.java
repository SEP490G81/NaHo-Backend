package org.naho.user.model;

import org.naho.i18n.message.user.UserSessionDetailMessageKey;
import org.naho.shared.exception.DomainException;
import org.naho.user.exception.UserSessionDomainErrorCode;
import org.naho.user.type.SessionRevokedReason;

import java.time.Instant;

public class UserSession {

    private Long id;
    private Long userId;
    private String hashRefreshToken;
    private String deviceId;
    private String userAgent;
    private String ipAddress;
    private Instant issuedAt;
    private Instant expiresAt;
    private Instant lastUsedAt;
    private Instant revokedAt;
    private SessionRevokedReason revokedReason;

    public boolean isExpired() {
        return expiresAt != null && !expiresAt.isAfter(Instant.now());
    }

    public boolean isRevoked() {
        return revokedAt != null || revokedReason != null;
    }

    public void setRevokedAt(Instant revokedAt) {
        this.revokedAt = revokedAt;
    }

    public void setRevokedReason(SessionRevokedReason revokedReason) {
        this.revokedReason = revokedReason;
    }

    public void setLastUsedAt(Instant lastUsedAt) {
        this.lastUsedAt = lastUsedAt;
    }

    // Private constructor
    private UserSession(Builder builder) {
        this.id = builder.id;
        this.userId = builder.userId;
        this.hashRefreshToken = builder.hashRefreshToken;
        this.deviceId = builder.deviceId;
        this.userAgent = builder.userAgent;
        this.ipAddress = builder.ipAddress;
        this.issuedAt = builder.issuedAt;
        this.expiresAt = builder.expiresAt;
        this.lastUsedAt = builder.lastUsedAt;
        this.revokedAt = builder.revokedAt;
        this.revokedReason = builder.revokedReason;
    }

    // Static builder method
    public static Builder builder() {
        return new Builder();
    }

    // Builder class
    public static class Builder {
        private Long id;
        private Long userId;
        private String hashRefreshToken;
        private String deviceId;
        private String userAgent;
        private String ipAddress;
        private Instant issuedAt;
        private Instant expiresAt;
        private Instant lastUsedAt;
        private Instant revokedAt;
        private SessionRevokedReason revokedReason;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder hashRefreshToken(String hashRefreshToken) {
            this.hashRefreshToken = hashRefreshToken;
            return this;
        }

        public Builder deviceId(String deviceId) {
            this.deviceId = deviceId;
            return this;
        }

        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public Builder ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        public Builder issuedAt(Instant issuedAt) {
            this.issuedAt = issuedAt;
            return this;
        }

        public Builder expiresAt(Instant expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        public Builder lastUsedAt(Instant lastUsedAt) {
            this.lastUsedAt = lastUsedAt;
            return this;
        }

        public Builder revokedAt(Instant revokedAt) {
            this.revokedAt = revokedAt;
            return this;
        }

        public Builder revokedReason(SessionRevokedReason revokedReason) {
            this.revokedReason = revokedReason;
            return this;
        }

        public UserSession build() {
            if (this.deviceId == null || this.deviceId.isBlank()) {
                throw new DomainException(
                        UserSessionDomainErrorCode.USER_SESSION_DEVICE_ID_NOT_VALID,
                        UserSessionDetailMessageKey.USER_SESSION_DEVICE_ID_BLANK
                );
            }
            return new UserSession(this);
        }
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getHashRefreshToken() {
        return hashRefreshToken;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public Instant getIssuedAt() {
        return issuedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getLastUsedAt() {
        return lastUsedAt;
    }

    public Instant getRevokedAt() {
        return revokedAt;
    }

    public SessionRevokedReason getRevokedReason() {
        return revokedReason;
    }
}