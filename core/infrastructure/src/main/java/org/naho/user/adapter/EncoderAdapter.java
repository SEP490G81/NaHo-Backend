package org.naho.user.adapter;

import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.user.constant.JwtProperties;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.port.out.EncoderPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class EncoderAdapter implements EncoderPort {
    private static final String REFRESH_TOKEN_HASH_ALGORITHM = "HmacSHA256";
    private final PasswordEncoder passwordEncoder;

    private final SecretKeySpec refreshTokenHashKey;
    private final Base64.Encoder base64UrlEncoder;

    public EncoderAdapter(
            PasswordEncoder passwordEncoder,
            JwtProperties jwtProperties
    ) {
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenHashKey = new SecretKeySpec(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8),
                REFRESH_TOKEN_HASH_ALGORITHM
        );
        this.base64UrlEncoder = Base64.getUrlEncoder().withoutPadding();
    }

    @Override
    public boolean matches(String rawPassword, String hashPassword) {
        return passwordEncoder.matches(rawPassword, hashPassword);
    }

    @Override
    public String hashRefreshToken(String raw) {
        try {
            Mac mac = Mac.getInstance(REFRESH_TOKEN_HASH_ALGORITHM);
            mac.init(refreshTokenHashKey);

            byte[] digest = mac.doFinal(raw.getBytes(StandardCharsets.UTF_8));

            return base64UrlEncoder.encodeToString(digest);
        } catch (Exception ex) {
            throw new ApplicationException(
                    UserErrorCode.USER_HASH_FAILED,
                    UserDetailMessageKey.USER_HASH_FAILED,
                    ex.getMessage()
            );
        }
    }

    @Override
    public String hashPassword(String raw) {
        return passwordEncoder.encode(raw);
    }
}
