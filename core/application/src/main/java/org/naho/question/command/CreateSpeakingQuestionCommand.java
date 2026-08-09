package org.naho.question.command;

public record CreateSpeakingQuestionCommand(
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
