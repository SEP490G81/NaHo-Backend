package org.naho.question.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SuggestCustomSpeakingQuestionRequest(
        @NotBlank(message = "Hint VI cannot be blank")
        String hintVi,
        String category
) {}
