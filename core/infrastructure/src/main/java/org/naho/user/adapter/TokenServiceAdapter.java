package org.naho.user.adapter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.user.constant.JwtCustomClaimKey;
import org.naho.user.constant.JwtProperties;
import org.naho.user.constant.TokenType;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.model.UserSession;
import org.naho.user.port.out.TokenServicePort;
import org.naho.user.result.AccessTokenPayload;
import org.naho.user.result.TokenResult;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class TokenServiceAdapter implements TokenServicePort {
    private static final String ACCESS_TOKEN_TYPE = "access-token";

    private static final int TOKEN_BYTES = 64;

    private final JwtProperties jwtProperties;
    private final SecretKey jwtSecretKey;
    private final SecureRandom secureRandom;
    private final Base64.Encoder base64UrlEncoder;

    public TokenServiceAdapter(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;

        this.jwtSecretKey = Keys.hmacShaKeyFor(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8)
        );

        this.secureRandom = new SecureRandom();
        this.base64UrlEncoder = Base64.getUrlEncoder().withoutPadding();
    }

    @Override
    public TokenResult generateAccessToken(List<String> roleNames, UserSession userSession) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(jwtProperties.getAccessTokenExpiration());
        Long expiresIn = Instant.now().plus(jwtProperties.getAccessTokenExpiration()).getEpochSecond();

        Map<String, Object> claims = Map.of(
                JwtCustomClaimKey.USER_SESSION_ID, userSession.getId(),
                JwtCustomClaimKey.TOKEN_TYPE, ACCESS_TOKEN_TYPE
        );

        String value = Jwts.builder()
                .subject(String.valueOf(userSession.getUserId()))
                .claims(claims)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))
                .signWith(jwtSecretKey, Jwts.SIG.HS512)
                .compact();

        return new TokenResult(TokenType.ACCESS_TOKEN_NAME, value, expiresAt, expiresIn);
    }

    @Override
    public TokenResult generateRefreshToken() {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(jwtProperties.getRefreshTokenExpiration());
        Long expiresIn = Instant.now().plus(jwtProperties.getAccessTokenExpiration()).getEpochSecond();

        String rawToken = generateSecureRandomToken();
        return new TokenResult(TokenType.REFRESH_TOKEN_NAME, rawToken, expiresAt, expiresIn);
    }

    @Override
    public AccessTokenPayload verifyAccessToken(String accessToken) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(jwtSecretKey)
                    .build()
                    .parseSignedClaims(accessToken)
                    .getPayload();

            String tokenType = claims.get(JwtCustomClaimKey.TOKEN_TYPE, String.class);

            if (!ACCESS_TOKEN_TYPE.equals(tokenType)) {
                throw new ApplicationException(
                        UserErrorCode.USER_UNAUTHORIZED,
                        UserDetailMessageKey.USER_UNAUTHORIZED
                );
            }

            Long userId = Long.valueOf(claims.getSubject());
            Long sessionId = claims.get(
                    JwtCustomClaimKey.USER_SESSION_ID,
                    Long.class
            );
            Instant expiresAt = claims.getExpiration().toInstant();

            return new AccessTokenPayload(
                    userId,
                    sessionId,
                    expiresAt
            );

        } catch (JwtException | IllegalArgumentException ex) {
            log.warn(ex.getMessage(), ex);
            throw new ApplicationException(
                    UserErrorCode.USER_UNAUTHORIZED,
                    UserDetailMessageKey.USER_UNAUTHORIZED
            );
        }
    }

    private String generateSecureRandomToken() {
        byte[] bytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(bytes);
        return base64UrlEncoder.encodeToString(bytes);
    }
}