package org.naho.user.valueobject;

import org.naho.shared.exception.DomainException;
import org.naho.user.exception.UserErrorCode;

import java.util.regex.Pattern;

public class Password {
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\\\d).{8,}$");

    private String value;

    private Password(String value) {
        this.value = value;
    }

    public static Password of(String value) {
        if (value == null || value.isBlank()) {
            throw new DomainException(
                    UserErrorCode.PASSWORD_REQUIRED,
                    "Password is required!"
            );
        }

        if (!PASSWORD_PATTERN.matcher(value).matches()) {
            throw new DomainException(
                    UserErrorCode.PASSWORD_NOT_VALID,
                    """
                            Password must:
                            - be at least 8 characters
                            - contain uppercase letter
                            - contain lowercase letter
                            - contain number
                            """
            );
        }
        return new Password(value);
    }
}
