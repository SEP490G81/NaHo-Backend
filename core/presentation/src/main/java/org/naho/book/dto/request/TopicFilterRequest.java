package org.naho.book.dto.request;

import org.naho.book.type.TopicStatus;

public record TopicFilterRequest(
        String keyword,
        TopicStatus status,
        Long bookId
) {
}
