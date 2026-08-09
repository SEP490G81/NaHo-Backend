package org.naho.book.command;

import org.naho.book.type.TopicStatus;

public record UpdateTopicCommand(
        Long id,
        Long requestUserId,
        boolean isAdminOrManager,
        String vietnameseDescription,
        String englishDescription,
        String japaneseNameMarkup,
        String japaneseDescriptionMarkup,
        TopicStatus status,
        Double orderIndex,
        Long coverImageFileId
) {
}
