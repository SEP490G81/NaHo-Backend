package org.naho.user.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.naho.user.type.Gender;
import org.naho.user.type.JLPTLevel;
import org.naho.user.type.UserStatus;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class UserResponse {
    Long id;
    String username;
    List<String> roleNames;
    String avatarFileUrl;

    String email;
    String firstName;
    String lastName;
    Gender gender;
    LocalDate dob;
    JLPTLevel jlptLevel;
    UserStatus status;
}
