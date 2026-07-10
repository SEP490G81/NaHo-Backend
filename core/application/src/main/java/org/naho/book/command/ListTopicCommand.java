package org.naho.book.command;

import org.naho.book.type.TopicStatus;

public record ListTopicCommand(
        int page,
        int size,
        String keyword,
        TopicStatus status,
        Long bookId,
        String sortBy,
        String sortDirection,
        boolean isAdmin
) {
    public ListTopicCommand withForcedStatus(TopicStatus newStatus) {
        return new ListTopicCommand(page, size, keyword, newStatus, bookId, sortBy, sortDirection, isAdmin);
    }
}
