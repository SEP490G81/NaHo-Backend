package org.naho.user.valueobject;

import org.naho.shared.exception.DomainException;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.exception.UserErrorKey;

import java.time.LocalDate;
import java.time.Period;

public class Dob {
    private static final int MIN_AGE = 8;
    private static final int MAX_AGE = 65;

    private final LocalDate value;

    private Dob(LocalDate value) {
        this.value = value;
    }

    public static Dob of(LocalDate value) {
        if (value == null) {
            return null;
        }

        LocalDate now = LocalDate.now();
        int age = Period.between(value, now).getYears();

        if (age < MIN_AGE || age > MAX_AGE) {
            throw new DomainException(
                    UserErrorCode.USER_AGE_NOT_VALID,
                    UserErrorKey.USER_AGE_INVALID_RANGE,
                    MIN_AGE,
                    MAX_AGE
            );
        }

        return new Dob(value);
    }

    public LocalDate getValue() {
        return value;
    }
}
