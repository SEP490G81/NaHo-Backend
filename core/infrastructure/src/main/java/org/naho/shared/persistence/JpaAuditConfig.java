package org.naho.shared.persistence;

import org.naho.user.result.AccessTokenPayload;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class JpaAuditConfig implements AuditorAware<Long> {
    private static final Long SYSTEM_USER_ID = 0L;

    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.of(SYSTEM_USER_ID);
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof AccessTokenPayload payload) {
            return Optional.of(payload.userId());
        }

        return Optional.of(SYSTEM_USER_ID);
    }
}
