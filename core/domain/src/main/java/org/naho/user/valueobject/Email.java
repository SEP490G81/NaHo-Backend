package org.naho.user.valueobject;

import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.shared.exception.DomainException;
import org.naho.user.exception.UserDomainErrorCode;

import java.util.regex.Pattern;

public class Email {
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private final String value;

    private Email(String value) {
        this.value = value;
    }

    public static Email of(String value) {
        if (value == null || value.isBlank()) {
            throw new DomainException(
                    UserDomainErrorCode.USER_EMAIL_NOT_VALID,
                    UserDetailMessageKey.USER_EMAIL_REQUIRED
            );
        }

        value = value.trim().toLowerCase();

        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new DomainException(
                    UserDomainErrorCode.USER_EMAIL_NOT_VALID,
                    UserDetailMessageKey.USER_EMAIL_INVALID_FORMAT,
                    value
            );
        }

        return new Email(value);
    }

    public String getValue() {
        return value;
    }
}
