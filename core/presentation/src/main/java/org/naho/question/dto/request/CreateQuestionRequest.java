package org.naho.question.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateQuestionRequest(
        Long objectiveId,

        @NotBlank(message = "Title is required")
        String titleMarkup,

        @NotBlank(message = "Description is required")
        String descriptionMarkup,

        Double orderIndex
) {
}
