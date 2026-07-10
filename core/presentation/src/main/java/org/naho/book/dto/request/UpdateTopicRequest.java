package org.naho.book.dto.request;

import jakarta.validation.constraints.NotNull;
import org.naho.book.type.TopicStatus;

public record UpdateTopicRequest(
        String japaneseNameMarkup,
        String japaneseDescriptionMarkup,
        @NotNull TopicStatus status,
        Double orderIndex,
        Long coverImageFileId
) {
}
