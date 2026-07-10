package org.naho.question.command;

public record CreateSpeakingQuestionCommand(
        Long userId,
        String titleMarkup,
        String descriptionMarkup,
        boolean isContentManager
) {
}
