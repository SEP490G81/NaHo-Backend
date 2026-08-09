package org.naho.book.dto.response;

import org.naho.book.type.TopicStatus;

import java.util.List;

public record TopicDetailResponse(
        Long id,
        Long userId,
        String japaneseName,
        String japaneseDescription,
        String vietnameseDescription,
        String englishDescription,
        String japaneseNameMarkup,
        String japaneseDescriptionMarkup,
        TopicStatus status,
        Double orderIndex,
        Double firstNodeGlobalOrderIndex,
        Double lastNodeGlobalOrderIndex,
        Long coverImageFileId,
        List<LessonResponse> lessons
) {
}
