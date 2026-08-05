package org.naho.question.command;

public record UpdateSpeakingQuestionCommand(
        Long id,
        Long userId,
        String titleMarkup,
        String descriptionMarkup,
        String sampleAnswer,
        boolean isContentManager
) {
}
