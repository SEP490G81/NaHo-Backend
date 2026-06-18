package org.naho.question.command;

public record UpdateQuestionCommand(
        Long id,
        Long userId,
        String titleMarkup,
        String descriptionMarkup,
        Double orderIndex,
        boolean isContentManager
) {
}
