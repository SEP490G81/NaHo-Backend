package org.naho.book.dto.response;

import org.naho.book.type.TopicStatus;

public record TopicResponse(
        Long id,
        Long userId,
        Long bookId,
        Long coverImageFileId,
        String japaneseName,
        String japaneseDescription,
        String vietnameseDescription,
        String englishDescription,
        String japaneseNameMarkup,
        String japaneseDescriptionMarkup,
        TopicStatus status,
        Double orderIndex,
        Double firstNodeGlobalOrderIndex,
        Double lastNodeGlobalOrderIndex
) {
}
