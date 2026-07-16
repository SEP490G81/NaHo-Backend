package org.naho.learning.result;

public record VocabularyDetailResult(
        Long id,
        String reading,
        String japanese,
        String vietnameseMeaningText,
        String englishMeaningText
) {
}
