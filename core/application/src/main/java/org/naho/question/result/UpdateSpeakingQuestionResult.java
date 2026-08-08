package org.naho.question.result;

import org.naho.question.type.QuestionStatus;

public record UpdateSpeakingQuestionResult(
        Long id,
        Long userId,
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
