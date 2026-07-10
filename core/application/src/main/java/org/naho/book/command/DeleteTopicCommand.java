package org.naho.book.command;

public record DeleteTopicCommand(
        Long id,
        boolean isAdminOrManager
) {
}
