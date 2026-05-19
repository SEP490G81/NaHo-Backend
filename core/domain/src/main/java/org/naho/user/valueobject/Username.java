package org.naho.user.valueobject;

import org.naho.shared.exception.DomainException;
import org.naho.user.exception.UserErrorCode;

import java.util.regex.Pattern;

public class Username {

    private static final int MIN_LENGTH = 4;
    public static final int MAX_LENGTH = 36;

    private static final Pattern USERNAME_PATTERN =
            Pattern.compile("^[a-z][a-z0-9]*$");

    private final String value;

    private Username(String value) {
        this.value = value;
    }

    public static Username of(String value) {

        if (value == null || value.isBlank()) {
            throw new DomainException(
                    UserErrorCode.USERNAME_REQUIRED,
                    "Username is required!"
            );
        }

        if (value.length() < MIN_LENGTH
                || value.length() > MAX_LENGTH) {

            throw new DomainException(
                    UserErrorCode.USERNAME_NOT_VALID,
                    String.format(
                            "Username must be between %d and %d characters!",
                            MIN_LENGTH,
                            MAX_LENGTH
                    )
            );
        }

        if (!USERNAME_PATTERN.matcher(value).matches()) {
            throw new DomainException(
                    UserErrorCode.USERNAME_NOT_VALID,
                    """
                            Username must:
                            - start with a lowercase letter
                            - contain only lowercase letters and numbers
                            """
            );
        }

        return new Username(value);
    }

    public String getValue() {
        return value;
    }
}