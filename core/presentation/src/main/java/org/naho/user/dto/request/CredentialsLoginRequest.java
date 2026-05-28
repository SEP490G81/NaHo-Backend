package org.naho.user.dto.request;

import org.naho.user.type.DeviceType;

public record CredentialsLoginRequest(
        String usernameOrEmail,
        String rawPassword,
        String deviceId,
        String deviceName,
        DeviceType deviceType,
        String userAgent,
        String ipAddress
) {
}
