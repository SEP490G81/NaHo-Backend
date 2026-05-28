package org.naho.user.adapter;

import org.naho.shared.exception.ApplicationException;
import org.naho.user.constant.JwtProperty;
import org.naho.user.constant.UserApplicationMessageKey;
import org.naho.user.exception.UserApplicationErrorCode;
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
            JwtProperty jwtProperty
    ) {
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenHashKey = new SecretKeySpec(
                jwtProperty.getSecret().getBytes(StandardCharsets.UTF_8),
                REFRESH_TOKEN_HASH_ALGORITHM
        );
        this.base64UrlEncoder = Base64.getUrlEncoder().withoutPadding();
    }

    @Override
    public boolean matches(String rawPassword, String hashPassword) {
        return passwordEncoder.matches(rawPassword, hashPassword);
    }

    @Override
    public String hash(String raw) {
        try {
            Mac mac = Mac.getInstance(REFRESH_TOKEN_HASH_ALGORITHM);
            mac.init(refreshTokenHashKey);

            byte[] digest = mac.doFinal(raw.getBytes(StandardCharsets.UTF_8));

            return base64UrlEncoder.encodeToString(digest);
        } catch (Exception ex) {
            throw new ApplicationException(
                    UserApplicationErrorCode.USER_HASH_FAILED,
                    UserApplicationMessageKey.USER_HASH_FAILED,
                    ex.getMessage()
            );
        }
    }
}
