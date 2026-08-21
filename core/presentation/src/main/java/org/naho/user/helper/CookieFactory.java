package org.naho.user.helper;

import lombok.RequiredArgsConstructor;
import org.naho.shared.constant.CookieProperty;
import org.naho.user.result.TokenResult;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class CookieFactory {
    private final CookieProperty cookieProperty;

    public ResponseCookie createCookieForJWTToken(TokenResult tokenResult) {
        Duration maxAge = Duration.between(
                Instant.now(),
                tokenResult.expiresAt()
        );

        if (maxAge.isNegative()) {
            maxAge = Duration.ZERO;
        }

        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie
                .from(tokenResult.cookieName(), tokenResult.value())
                .httpOnly(true)
                .secure(cookieProperty.isSecure())
                .sameSite("Lax")
                .path("/")
                .maxAge(maxAge);

        if (cookieProperty.getDomain() != null && !cookieProperty.getDomain().isBlank()) {
            builder.domain(cookieProperty.getDomain());
        }

        return builder.build();
    }

    public ResponseCookie clearCookieForJWTToken(String cookieName) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie
                .from(cookieName, "")
                .httpOnly(true)
                .secure(cookieProperty.isSecure())
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ZERO);

        if (cookieProperty.getDomain() != null && !cookieProperty.getDomain().isBlank()) {
            builder.domain(cookieProperty.getDomain());
        }

        return builder.build();
    }
}
