package org.naho.question.command;

public record DeleteQuestionCommand(
        Long id,
        Long userId,
        boolean isAdminOrManager
) {
}
