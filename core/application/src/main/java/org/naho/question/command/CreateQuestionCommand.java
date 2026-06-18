package org.naho.question.command;

public record CreateQuestionCommand(
        Long topicId,
        Long userId,
        String titleMarkup,
        String descriptionMarkup,
        Double orderIndex,
        boolean isContentManager
) {
}
