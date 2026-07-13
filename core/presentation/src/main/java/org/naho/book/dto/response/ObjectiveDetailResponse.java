package org.naho.book.dto.response;

import org.naho.book.type.TopicStatus;
import org.naho.question.dto.response.SpeakingQuestionListItemResponse;

import java.util.List;

public record ObjectiveDetailResponse(
        Long id,
        String japaneseName,
        String japaneseDescription,
        String japaneseNameMarkup,
        String japaneseDescriptionMarkup,
        TopicStatus status,
        Double orderIndex,
        List<SpeakingQuestionListItemResponse> questions
) {
}
