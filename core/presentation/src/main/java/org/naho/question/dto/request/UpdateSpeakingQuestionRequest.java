package org.naho.question.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateSpeakingQuestionRequest(
        @NotBlank(message = "Japanese name is required")
        String japaneseNameMarkup,

        String vietnameseName,

        @NotBlank(message = "Description is required")
        String descriptionMarkup,

        String japaneseSampleAnswerMarkup,

        String vietnameseSampleAnswer,

        String englishSampleAnswer
) {
}
