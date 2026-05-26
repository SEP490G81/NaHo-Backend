package org.naho.user.result;

import org.naho.file.result.FileResult;
import org.naho.user.type.Gender;
import org.naho.user.type.JLPTLevel;
import org.naho.user.type.UserStatus;

import java.time.LocalDate;
import java.util.Set;

public record UserResult(
        Long id,
        String email,
        Set<String> roles,
        String firstName,
        String lastName,
        Gender gender,
        LocalDate dob,
        FileResult avatar,
        JLPTLevel jlptLevel,
        UserStatus status
) {
}
