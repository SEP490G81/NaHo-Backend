package org.naho.speech.topic.query;

import org.naho.speech.type.TopicStatus;
import org.naho.user.type.JLPTLevel;

public record ListTopicQuery(
        int page,
        int size,
        String keyword,
        TopicStatus status,
        JLPTLevel jlptLevel,
        String sortBy,
        String sortDirection,
        boolean isAdmin
) {
    public ListTopicQuery withForcedStatus(TopicStatus newStatus) {
        return new ListTopicQuery(page, size, keyword, newStatus, jlptLevel, sortBy, sortDirection, isAdmin);
    }
}
