package org.naho.book.result;

import org.naho.book.type.TopicStatus;

import java.util.List;

public record LessonDetailResult(
        Long id,
        String japaneseName,
        String japaneseDescription,
        String japaneseNameMarkup,
        String japaneseDescriptionMarkup,
        TopicStatus status,
        Double orderIndex,
        List<ObjectiveListItemResult> objectives
) {
}