package org.naho.user.valueobject;

import org.naho.shared.exception.DomainException;
import org.naho.user.constant.UserMessageKey;
import org.naho.user.exception.UserErrorCode;

import java.util.regex.Pattern;

public class Email {
    private final String value;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private Email(String value) {
        this.value = value;
    }

    public static Email of(String value) {
        if (value == null || value.isBlank()) {
            throw new DomainException(
                    UserErrorCode.USER_EMAIL_NOT_VALID,
                    UserMessageKey.USER_EMAIL_REQUIRED
            );
        }

        value = value.trim().toLowerCase();

        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new DomainException(
                    UserErrorCode.USER_EMAIL_NOT_VALID,
                    UserMessageKey.USER_EMAIL_INVALID_FORMAT,
                    value
            );
        }

        return new Email(value);
    }

    public String getValue() {
        return value;
    }
}
