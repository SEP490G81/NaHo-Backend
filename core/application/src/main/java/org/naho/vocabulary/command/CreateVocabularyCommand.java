package org.naho.vocabulary.command;

public record CreateVocabularyCommand(
        String reading,
        String japanese,
        String vietnameseMeaningText,
        String englishMeaningText
) {
}
