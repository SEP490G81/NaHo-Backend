package org.naho.speech.llm.dto.response;

public record WordPronunciationResponse(
        String word,
        Double accuracyScore,
        String errorType,
        String colorCategory,
        String hexColor
) {
}
