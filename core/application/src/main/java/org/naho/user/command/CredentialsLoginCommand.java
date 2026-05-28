package org.naho.user.command;

import org.naho.user.type.DeviceType;

public record CredentialsLoginCommand(
        String usernameOrEmail,
        String rawPassword,
        String deviceId,
        String deviceName,
        DeviceType deviceType,
        String userAgent,
        String ipAddress
) {
}
