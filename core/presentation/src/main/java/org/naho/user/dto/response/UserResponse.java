package org.naho.user.dto.response;

import lombok.Builder;
import org.naho.file.result.FileResult;
import org.naho.user.result.OAuthProviderResult;
import org.naho.user.result.RoleResult;
import org.naho.user.type.Gender;
import org.naho.user.type.JLPTLevel;
import org.naho.user.type.UserStatus;

import java.time.LocalDate;
import java.util.List;

@Builder
public record UserResponse(
        Long id,
        List<RoleResult> roles,
        List<Long> userSessionIds,
        List<OAuthProviderResult> oAuthProviders,

        Long userLearningProgressId,
        FileResult avatar,

        String username,
        String email,
        String fullName,
        Gender gender,
        LocalDate dob,
        JLPTLevel jlptLevel,

        UserStatus status
) {
}
