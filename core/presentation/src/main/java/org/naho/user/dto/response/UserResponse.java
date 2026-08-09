package org.naho.user.dto.response;

import lombok.Builder;
import org.naho.user.type.Gender;

import java.time.LocalDate;
import java.util.List;

@Builder
public record UserResponse(
        Long id,
        List<AuthProviderResponse> authProviders,

        Long userLearningProgressId,
        String avatarUrl,

        String username,
        String email,
        String fullName,
        Gender gender,
        LocalDate dob
) {
}
