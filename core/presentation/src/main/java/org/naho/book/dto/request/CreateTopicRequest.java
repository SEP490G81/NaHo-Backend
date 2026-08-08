package org.naho.book.dto.request;

import jakarta.validation.constraints.NotNull;

public record CreateTopicRequest(
        String vietnameseDescription,
        String englishDescription,
        String japaneseNameMarkup,
        String japaneseDescriptionMarkup,
        @NotNull Long bookId,
        Double orderIndex,
        Long coverImageFileId
) {
}
