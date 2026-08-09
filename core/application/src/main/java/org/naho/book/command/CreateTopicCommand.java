package org.naho.book.command;

public record CreateTopicCommand(
        Long userId,
        String vietnameseDescription,
        String englishDescription,
        String japaneseNameMarkup,
        String japaneseDescriptionMarkup,
        Long bookId,
        Double orderIndex,
        Long coverImageFileId
) {
}
