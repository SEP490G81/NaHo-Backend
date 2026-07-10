package org.naho.book.command;

import org.naho.book.type.TopicStatus;

public record UpdateTopicCommand(
        Long id,
        Long requestUserId,
        boolean isAdminOrManager,
        String japaneseNameMarkup,
        String japaneseDescriptionMarkup,
        TopicStatus status,
        Double orderIndex,
        Long coverImageFileId
) {
}
