package org.naho.question.command;

public record CreateSpeakingQuestionCommand(
        Long userId,
        String japaneseName,
        String vietnameseName,
        String description,
        String japaneseSampleAnswer,
        String vietnameseSampleAnswer,
        String englishSampleAnswer,
        boolean isContentManager
) {
}
