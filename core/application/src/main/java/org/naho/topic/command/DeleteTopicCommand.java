package org.naho.topic.command;

public record DeleteTopicCommand(
        Long id,
        boolean isAdminOrManager
) {
}
