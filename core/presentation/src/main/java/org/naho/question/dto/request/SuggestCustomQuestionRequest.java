package org.naho.question.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SuggestCustomQuestionRequest(
        @NotBlank(message = "Hint VI cannot be blank")
        String hintVi,
        String category
) {}
