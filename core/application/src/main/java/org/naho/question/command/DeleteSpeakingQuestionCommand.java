package org.naho.question.command;

public record DeleteSpeakingQuestionCommand(
        Long id,
        Long userId,
        boolean isAdminOrManager
) {
}
