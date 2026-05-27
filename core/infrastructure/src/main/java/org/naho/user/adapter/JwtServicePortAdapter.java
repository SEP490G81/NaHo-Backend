package org.naho.user.adapter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.naho.user.constant.JwtCustomClaimKey;
import org.naho.user.constant.JwtProperty;
import org.naho.user.model.User;
import org.naho.user.port.out.JwtServicePort;
import org.naho.user.result.TokenResult;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtServicePortAdapter implements JwtServicePort {
    private static final String ACCESS_TOKEN_TYPE = "access";
    private static final String REFRESH_TOKEN_TYPE = "refresh";

    private final JwtProperty jwtProperty;
    private final SecretKey secretKey;

    public JwtServicePortAdapter(JwtProperty jwtProperty) {
        this.jwtProperty = jwtProperty;
        this.secretKey = Keys.hmacShaKeyFor(jwtProperty.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public TokenResult generateAccessToken(User user) {
        Instant issuedAt = Instant.now();
        Instant expireAt = issuedAt.plus(jwtProperty.getAccessTokenExpiration());

        Map<String, Object> claims = Map.of(
                JwtCustomClaimKey.USER_ROLES, user.getRoles().stream().map(role -> role.getRoleName().name()).toList(),
                JwtCustomClaimKey.ACCOUNT_TYPE, user.getAccountType().name(),
                JwtCustomClaimKey.TOKEN_TYPE, ACCESS_TOKEN_TYPE
        );

        String value = buildToken(user, claims, issuedAt, expireAt);
        return new TokenResult(value, jwtProperty.getAccessTokenExpiration().toSeconds());
    }

    @Override
    public TokenResult generateRefreshToken(User user) {
        Instant issuedAt = Instant.now();
        Instant expireAt = issuedAt.plus(jwtProperty.getRefreshTokenExpiration());

        Map<String, Object> claims = Map.of(
                JwtCustomClaimKey.TOKEN_TYPE, REFRESH_TOKEN_TYPE
        );

        String value = buildToken(user, claims, issuedAt, expireAt);
        return new TokenResult(value, jwtProperty.getRefreshTokenExpiration().toSeconds());
    }

    private String buildToken(
            User user,
            Map<String, Object> claims,
            Instant issuedAt,
            Instant expireAt
    ) {
        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claims(claims)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expireAt))
                .signWith(secretKey)
                .compact();
    }
}
