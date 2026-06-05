package org.naho.user.helper;

import lombok.RequiredArgsConstructor;
import org.naho.shared.constant.CookieProperty;
import org.naho.user.result.LoginResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class AuthControllerHelper {
    private final CookieProperty cookieProperty;

    public ResponseEntity<Void> attachTokensToResponseHeader(LoginResult result) {
        Duration accessTokenMaxAge = Duration.between(
                Instant.now(),
                result.accessToken().expiresAt()
        );

        if (accessTokenMaxAge.isNegative()) {
            accessTokenMaxAge = Duration.ZERO;
        }

        Duration refreshTokenMaxAge = Duration.between(
                Instant.now(),
                result.refreshToken().expiresAt()
        );

        if (refreshTokenMaxAge.isNegative()) {
            refreshTokenMaxAge = Duration.ZERO;
        }

        ResponseCookie accessTokenCookie = ResponseCookie
                .from("access_token", result.accessToken().value())
                .httpOnly(true)
                .secure(cookieProperty.isSecure())
                .sameSite("Lax")
                .path("/")
                .maxAge(accessTokenMaxAge)
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie
                .from("refresh_token", result.refreshToken().value())
                .httpOnly(true)
                .secure(cookieProperty.isSecure())
                .sameSite("Lax")
                .path("/")
                .maxAge(refreshTokenMaxAge)
                .build();

        HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());

        headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return ResponseEntity.ok().headers(headers).build();
    }
}
