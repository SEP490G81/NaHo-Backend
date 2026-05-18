package org.naho.user.valueobject;

import org.naho.shared.exception.DomainException;
import org.naho.user.exception.UserErrorCode;

import java.util.regex.Pattern;

public class Email {
    private String value;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private Email(String value) {
        this.value = value;
    }

    public static Email of(String value) {
        if (value == null || value.isBlank()) {
            throw new DomainException(
                    UserErrorCode.EMAIL_REQUIRED,
                    "Email is required!"
            );
        }

        value = value.trim().toLowerCase();

        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new DomainException(
                    UserErrorCode.EMAIL_NOT_VALID,
                    value + " is not a valid email!"
            );
        }

        return new Email(value);
    }

    public String getValue() {
        return value;
    }
}
