package org.naho.user.valueobject;

import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.shared.exception.DomainException;
import org.naho.user.exception.UserDomainErrorCode;

import java.util.regex.Pattern;

public class Password {
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!_~\\-]).{8,}$");
    private final String value;

    private Password(String value) {
        this.value = value;
    }

    public static Password of(String value) {
        if (value == null || value.isBlank()) {
            throw new DomainException(
                    UserDomainErrorCode.USER_PASSWORD_NOT_VALID,
                    UserDetailMessageKey.USER_PASSWORD_REQUIRED
            );
        }

        if (!PASSWORD_PATTERN.matcher(value).matches()) {
            throw new DomainException(
                    UserDomainErrorCode.USER_PASSWORD_NOT_VALID,
                    UserDetailMessageKey.USER_PASSWORD_INVALID_FORMAT
            );
        }

        return new Password(value);
    }

    public String getValue() {
        return value;
    }
}
