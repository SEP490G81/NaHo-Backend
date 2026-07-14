package org.naho.user.result;

import org.naho.user.type.OAuthProviderName;

public record OAuthProviderResult(
        Long id,
        OAuthProviderName providerName,
        String avatarUrl
) {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Long id;
        private OAuthProviderName providerName;
        private String avatarUrl;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder providerName(OAuthProviderName providerName) {
            this.providerName = providerName;
            return this;
        }

        public Builder avatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
            return this;
        }

        public OAuthProviderResult build() {
            return new OAuthProviderResult(
                    id,
                    providerName,
                    avatarUrl
            );
        }
    }
}
