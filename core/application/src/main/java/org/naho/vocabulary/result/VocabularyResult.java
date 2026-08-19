package org.naho.vocabulary.result;

public record VocabularyResult(
        Long id,
        String reading,
        String japanese,
        String vietnameseMeaningText,
        String englishMeaningText
) {
}
