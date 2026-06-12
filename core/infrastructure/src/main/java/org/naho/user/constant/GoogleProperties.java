package org.naho.user.constant;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "spring.security.oauth2.client.registration.google")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class GoogleProperties {
    String clientId;
}
