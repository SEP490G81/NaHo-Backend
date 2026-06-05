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

        return ResponseCookie
                .from(tokenResult.tokenName(), tokenResult.value())
                .httpOnly(true)
                .secure(cookieProperty.isSecure())
                .sameSite("Lax")
                .path("/")
                .maxAge(maxAge)
                .build();
    }

    public ResponseCookie clearCookieForJWTToken(String tokenName) {
        return ResponseCookie
                .from(tokenName, "")
                .httpOnly(true)
                .secure(cookieProperty.isSecure())
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ZERO)
                .build();
    }
}
