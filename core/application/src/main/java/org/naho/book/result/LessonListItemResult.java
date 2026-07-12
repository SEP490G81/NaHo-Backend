package org.naho.book.result;


import org.naho.book.type.TopicStatus;

public record LessonListItemResult(
        Long id,
        String japaneseName,
        String japaneseDescription,
        String japaneseNameMarkup,
        String japaneseDescriptionMarkup,
        TopicStatus status,
        Double orderIndex
) {
}