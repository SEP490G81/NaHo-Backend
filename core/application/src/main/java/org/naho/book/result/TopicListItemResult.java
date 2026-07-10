package org.naho.book.result;

import org.naho.book.type.TopicStatus;

public record TopicListItemResult(
        Long id,
        String japaneseName,
        String japaneseDescription,
        String japaneseNameMarkup,
        String japaneseDescriptionMarkup,
        TopicStatus status,
        Double orderIndex,
        Long coverImageFileId,
        Integer totalQuestions
) {
}
