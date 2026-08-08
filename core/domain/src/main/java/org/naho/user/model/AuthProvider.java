package org.naho.user.model;

import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.shared.exception.DomainException;
import org.naho.user.exception.UserDomainErrorCode;
import org.naho.user.type.AuthProviderName;

public class AuthProvider {

    private final Long id;
    private final Long userId;
    private final String providerUserId;
    private final AuthProviderName providerName;
    private final String avatarUrl;

    private AuthProvider(Builder builder) {
        this.id = builder.id;
        this.userId = builder.userId;
        this.providerUserId = builder.providerUserId;
        this.providerName = builder.providerName;
        this.avatarUrl = builder.avatarUrl;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getProviderUserId() {
        return providerUserId;
    }

    public AuthProviderName getProviderName() {
        return providerName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public static final class Builder {

        private Long id;
        private Long userId;
        private String providerUserId;
        private AuthProviderName providerName;
        private String avatarUrl;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder providerUserId(String providerUserId) {
            this.providerUserId = providerUserId;
            return this;
        }

        public Builder providerName(AuthProviderName providerName) {
            this.providerName = providerName;
            return this;
        }

        public Builder avatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
            return this;
        }

        public AuthProvider build() {
            if (providerUserId == null || providerUserId.isBlank()) {
                throw new DomainException(
                        UserDomainErrorCode.USER_AUTH_PROVIDER_NOT_VALID,
                        UserDetailMessageKey.USER_AUTH_PROVIDER_PROVIDER_USER_ID_BLANK
                );
            }

            if (providerName == null) {
                throw new DomainException(
                        UserDomainErrorCode.USER_AUTH_PROVIDER_NOT_VALID,
                        UserDetailMessageKey.USER_AUTH_PROVIDER_PROVIDER_NAME_NULL
                );
            }

            return new AuthProvider(this);
        }
    }
}
