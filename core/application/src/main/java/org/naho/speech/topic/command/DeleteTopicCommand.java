package org.naho.speech.topic.command;

public record DeleteTopicCommand(
        Long id,
        boolean isAdminOrManager
) {
}
