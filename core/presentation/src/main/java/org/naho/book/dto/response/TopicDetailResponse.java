package org.naho.book.dto.response;

import org.naho.book.type.TopicStatus;

public record TopicDetailResponse(
        Long id,
        Long userId,
        String japaneseName,
        String japaneseDescription,
        String japaneseNameMarkup,
        String japaneseDescriptionMarkup,
        TopicStatus status,
        Double orderIndex,
        Long coverImageFileId
) {
}
