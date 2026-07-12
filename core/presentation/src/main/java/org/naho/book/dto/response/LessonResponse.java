package org.naho.book.dto.response;

import org.naho.book.type.TopicStatus;

public record LessonResponse(
        Long id,
        String japaneseName,
        String japaneseDescription,
        String japaneseNameMarkup,
        String japaneseDescriptionMarkup,
        TopicStatus status,
        Double orderIndex
) {
}