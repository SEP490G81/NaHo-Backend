package org.naho.book.dto.response;

import org.naho.book.type.TopicStatus;
import org.naho.learning.dto.response.LearningPathNodeListItemResponse;

import java.util.List;

public record ObjectiveDetailResponse(
        Long id,
        String japaneseName,
        String japaneseDescription,
        String japaneseNameMarkup,
        String japaneseDescriptionMarkup,
        TopicStatus status,
        Double orderIndex,
        List<LearningPathNodeListItemResponse> learningPathNodes
) {
}
