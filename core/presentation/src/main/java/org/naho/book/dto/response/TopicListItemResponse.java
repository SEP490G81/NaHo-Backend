package org.naho.book.dto.response;

import org.naho.book.type.TopicStatus;

public record TopicListItemResponse(
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
