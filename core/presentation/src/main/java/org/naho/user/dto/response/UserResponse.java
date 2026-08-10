package org.naho.user.dto.response;

import lombok.Builder;
import org.naho.user.type.Gender;
import org.naho.user.type.UserStatus;

import java.time.LocalDate;
import java.util.List;

@Builder
public record UserResponse(
        Long id,
        RoleResponse role,
        List<AuthProviderResponse> authProviders,

        String avatarUrl,
        Long userLearningProgressId,

        String username,
        String email,
        String fullName,
        Gender gender,
        LocalDate dob,
        UserStatus status
) {
}
