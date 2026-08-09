package org.naho.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        String username,
        String password,
        String email,
        @NotBlank(message = "{USER_FULL_NAME_REQUIRED}")
        @Size(min = 2, max = 100, message = "{USER_FULL_NAME_INVALID_LENGTH}")
        String fullName
) {
}
