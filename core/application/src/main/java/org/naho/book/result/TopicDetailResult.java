package org.naho.book.result;

import org.naho.book.type.TopicStatus;

import java.util.List;

public record TopicDetailResult(
        Long id,
        Long userId,
        String japaneseName,
        String japaneseDescription,
        String japaneseNameMarkup,
        String japaneseDescriptionMarkup,
        TopicStatus status,
        Double orderIndex,
        Long coverImageFileId,
        List<LessonListItemResult> lessons
) {
}
