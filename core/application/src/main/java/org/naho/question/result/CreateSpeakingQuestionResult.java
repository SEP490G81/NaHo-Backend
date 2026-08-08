package org.naho.question.result;

import org.naho.question.type.QuestionStatus;

public record CreateSpeakingQuestionResult(
        Long id,
        Long userId,
        Long speakingQuestionAudioFileId,
        String japaneseName,
        String japaneseNameMarkup,
        String vietnameseName,
        String description,
        String descriptionMarkup,
        String japaneseSampleAnswer,
        String japaneseSampleAnswerMarkup,
        String vietnameseSampleAnswer,
        String englishSampleAnswer,
        QuestionStatus status
) {
}
