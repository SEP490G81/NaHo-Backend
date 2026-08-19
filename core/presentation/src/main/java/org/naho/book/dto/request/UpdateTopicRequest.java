package org.naho.book.dto.request;

import jakarta.validation.constraints.NotNull;
import org.naho.book.type.TopicStatus;

public record UpdateTopicRequest(
        String japaneseName,
        String japaneseDescription,
        String vietnameseDescription,
        String englishDescription,
        @NotNull TopicStatus status,
        Long coverImageFileId
) {
}
