package org.naho.book.command;

public record CreateTopicCommand(
        Long userId,
        String japaneseNameMarkup,
        String japaneseDescriptionMarkup,
        Long bookId,
        Double orderIndex,
        Long coverImageFileId
) {
}
