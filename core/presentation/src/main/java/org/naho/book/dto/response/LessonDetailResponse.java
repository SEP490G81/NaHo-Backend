package org.naho.book.dto.response;

import org.naho.book.type.TopicStatus;

import java.util.List;

public record LessonDetailResponse(
        Long id,
        String japaneseName,
        String japaneseDescription,
        String japaneseNameMarkup,
        String japaneseDescriptionMarkup,
        TopicStatus status,
        Double orderIndex,
        Double firstNodeGlobalOrderIndex,
        Double lastNodeGlobalOrderIndex,
        List<ObjectiveResponse> objectives
) {
}
