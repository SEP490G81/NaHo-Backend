package org.naho.user.dto.request;

import org.naho.user.type.Gender;

import java.time.LocalDate;

public record UpdateUserInfoRequest(
        String username,
        String fullName,
        Gender gender,
        LocalDate dob
) {
}
