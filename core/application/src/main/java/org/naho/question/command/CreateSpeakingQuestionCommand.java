package org.naho.question.command;

public record CreateSpeakingQuestionCommand(
        Long userId,
        String titleMarkup,
        String descriptionMarkup,
        String sampleAnswer,
        boolean isContentManager
) {
}
