package org.naho.vocabulary.command;

public record UpdateVocabularyCommand(
        Long id,
        String reading,
        String japanese,
        String vietnameseMeaningText,
        String englishMeaningText
) {
}
