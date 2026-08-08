package org.naho.user.dto.response;

import lombok.Builder;
import org.naho.file.dto.response.FileResponse;
import org.naho.user.result.OAuthProviderResult;
import org.naho.user.type.Gender;
import org.naho.user.type.JLPTLevel;

import java.time.LocalDate;
import java.util.List;

@Builder
public record UserResponse(
        Long id,
        List<OAuthProviderResult> oAuthProviders,

        Long userLearningProgressId,
        FileResponse avatarFile,

        String username,
        String email,
        String fullName,
        Gender gender,
        LocalDate dob,
        JLPTLevel jlptLevel
) {
}
