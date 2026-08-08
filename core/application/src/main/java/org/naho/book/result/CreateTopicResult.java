package org.naho.book.result;

import org.naho.book.type.TopicStatus;

public record CreateTopicResult(
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
        Long coverImageFileId
) {
}
