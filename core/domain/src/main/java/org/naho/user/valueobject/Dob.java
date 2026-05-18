package org.naho.user.valueobject;

import org.naho.shared.exception.DomainException;
import org.naho.user.exception.UserErrorCode;

import java.time.LocalDate;
import java.time.Period;

public class Dob {
    private static final int MIN_AGE = 8;
    private static final int MAX_AGE = 65;

    private LocalDate value;

    private Dob(LocalDate value) {
        this.value = value;
    }

    public static Dob of(LocalDate value) {
        LocalDate now = LocalDate.now();
        int age = Period.between(value, now).getYears();

        if (age < MIN_AGE || age > MAX_AGE) {
            throw new DomainException(
                    UserErrorCode.AGE_NOT_VALID,
                    "Age must be between " + MIN_AGE + " and " + MAX_AGE + " years!"
            );
        }

        return new Dob(value);
    }

    public LocalDate getValue() {
        return value;
    }
}
