package org.naho.question.result;

import org.naho.question.type.QuestionStatus;

public record SpeakingQuestionListItemResult(
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
        QuestionStatus status
) {
}
