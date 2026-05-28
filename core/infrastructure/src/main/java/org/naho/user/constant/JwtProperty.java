package org.naho.user.constant;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.jwt")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class JwtProperty {
    String secret;
    Duration accessTokenExpiration;
    Duration refreshTokenExpiration;
}
