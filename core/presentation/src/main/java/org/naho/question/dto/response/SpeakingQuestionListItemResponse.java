package org.naho.question.dto.response;

import org.naho.question.type.QuestionStatus;

import java.time.ZonedDateTime;

public record SpeakingQuestionListItemResponse(
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
        QuestionStatus status,
        ZonedDateTime createdTime
) {
}
