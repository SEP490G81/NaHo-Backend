package org.naho.question.command;

public record CreateQuestionCommand(
        Long objectiveId,
        Long userId,
        String titleMarkup,
        String descriptionMarkup,
        Double orderIndex,
        boolean isContentManager
) {
}
