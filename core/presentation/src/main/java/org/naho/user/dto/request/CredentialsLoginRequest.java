package org.naho.user.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class CredentialsLoginRequest {
    String usernameOrEmail;
    String rawPassword;
    String deviceId;
    String userAgent;
    String ipAddress;
}
