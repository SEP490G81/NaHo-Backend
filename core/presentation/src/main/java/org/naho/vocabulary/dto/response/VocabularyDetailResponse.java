package org.naho.vocabulary.dto.response;

public record VocabularyDetailResponse(
        Long id,
        String reading,
        String japanese,
        String vietnameseMeaningText,
        String englishMeaningText
) {
}
