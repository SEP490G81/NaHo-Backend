package org.naho.book.dto.request;

import jakarta.validation.constraints.NotNull;
import org.naho.book.type.TopicStatus;

public record UpdateObjectiveRequest(
        String japaneseName,
        String japaneseDescription,
        @NotNull(message = "Status cannot be null") TopicStatus status
) {
}
