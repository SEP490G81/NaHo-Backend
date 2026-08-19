package org.naho.vocabulary.dto.response;

public record VocabularyResponse(
        Long id,
        String reading,
        String japanese,
        String vietnameseMeaningText,
        String englishMeaningText
) {
}
