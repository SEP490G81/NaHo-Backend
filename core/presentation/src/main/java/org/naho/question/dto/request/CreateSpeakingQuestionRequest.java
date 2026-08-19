package org.naho.question.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateSpeakingQuestionRequest(
        @NotBlank(message = "Japanese name is required")
        String japaneseName,

        String vietnameseName,

        @NotBlank(message = "Description is required")
        String description,

        String japaneseSampleAnswer,

        String vietnameseSampleAnswer,

        String englishSampleAnswer
) {
}
