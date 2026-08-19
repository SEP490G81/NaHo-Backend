package org.naho.vocabulary.command;

public record SearchVocabularyCommand(
        String keyword,
        int page,
        int size
) {
}
