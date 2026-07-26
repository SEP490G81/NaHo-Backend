package org.naho.question.command;

public record CompleteVocabularyQuestionCommand(
        Long vocabularyQuestionId,
        Long userId
) {
}
