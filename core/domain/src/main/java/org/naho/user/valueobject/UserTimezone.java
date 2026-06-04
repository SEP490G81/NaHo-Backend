package org.naho.user.valueobject;

import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.user.exception.DomainException;
import org.naho.user.exception.UserDomainErrorCode;

import java.time.ZoneId;

public class UserTimezone {
    private final String value;

    private UserTimezone(String value) {
        this.value = value;
    }

    public static UserTimezone of(String value) {
        if (value == null || !ZoneId.getAvailableZoneIds().contains(value)) {
            throw new DomainException(
                    UserDomainErrorCode.USER_TIMEZONE_NOT_VALID,
                    UserDetailMessageKey.USER_TIMEZONE_NOT_FOUND,
                    value
            );
        }
        return new UserTimezone(value);
    }
}
