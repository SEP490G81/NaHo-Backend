package org.naho.question.dto.response;

import org.naho.question.type.QuestionStatus;

import java.time.ZonedDateTime;

public record SpeakingQuestionListItemResponse(
        Long id,
        Long userId,
        Long speakingQuestionAudioFileId,
        String title,
        String titleMarkup,
        String description,
        String descriptionMarkup,
        Double orderIndex,
        QuestionStatus status,
        ZonedDateTime createdTime
) {
}
