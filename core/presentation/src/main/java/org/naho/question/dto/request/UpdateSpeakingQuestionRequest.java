package org.naho.question.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateSpeakingQuestionRequest(
        @NotBlank(message = "Title is required")
        String titleMarkup,

        @NotBlank(message = "Description is required")
        String descriptionMarkup,

        String sampleAnswer
) {
}
