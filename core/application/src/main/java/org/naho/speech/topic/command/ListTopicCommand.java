package org.naho.speech.topic.command;

import org.naho.topic.type.TopicStatus;
import org.naho.user.type.JLPTLevel;

public record ListTopicCommand(
        int page,
        int size,
        String keyword,
        TopicStatus status,
        JLPTLevel jlptLevel,
        String sortBy,
        String sortDirection,
        boolean isAdmin
) {
    public ListTopicCommand withForcedStatus(TopicStatus newStatus) {
        return new ListTopicCommand(page, size, keyword, newStatus, jlptLevel, sortBy, sortDirection, isAdmin);
    }
}
