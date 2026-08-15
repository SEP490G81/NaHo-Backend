package org.naho.book.command;

import org.naho.book.type.TopicStatus;

public record UpdateTopicCommand(
        Long id,
        Long requestUserId,
        boolean isAdminOrManager,
        String japaneseName,
        String japaneseDescription,
        String vietnameseDescription,
        String englishDescription,
        TopicStatus status,
        Long coverImageFileId
) {
}
