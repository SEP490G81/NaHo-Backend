package org.naho.user.valueobject;

import org.naho.shared.exception.DomainException;
import org.naho.user.constant.UserMessageKey;
import org.naho.user.exception.UserErrorCode;

import java.time.ZoneId;

public class UserTimezone {
    private final String value;

    private UserTimezone(String value) {
        this.value = value;
    }

    public static UserTimezone of(String value) {
        if (value == null || !ZoneId.getAvailableZoneIds().contains(value)) {
            throw new DomainException(
                    UserErrorCode.USER_TIMEZONE_NOT_VALID,
                    UserMessageKey.USER_TIMEZONE_NOT_FOUND,
                    value
            );
        }
        return new UserTimezone(value);
    }
}
