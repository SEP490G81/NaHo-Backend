package org.naho.user.result;

import org.naho.user.type.AuthProviderName;

public record AuthProviderResult(
        Long id,
        AuthProviderName providerName,
        String avatarUrl
) {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Long id;
        private AuthProviderName providerName;
        private String avatarUrl;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
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

        public AuthProviderResult build() {
            return new AuthProviderResult(
                    id,
                    providerName,
                    avatarUrl
            );
        }
    }
}
