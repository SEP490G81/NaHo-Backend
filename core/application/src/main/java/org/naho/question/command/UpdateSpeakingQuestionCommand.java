package org.naho.question.command;

public record UpdateSpeakingQuestionCommand(
        Long id,
        Long userId,
        String japaneseNameMarkup,
        String vietnameseName,
        String descriptionMarkup,
        String japaneseSampleAnswerMarkup,
        String vietnameseSampleAnswer,
        String englishSampleAnswer,
        boolean isContentManager
) {
}
