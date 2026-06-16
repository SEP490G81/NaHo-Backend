package org.naho.speech.topic.dto.request;

import org.naho.topic.type.TopicStatus;
import org.naho.user.type.JLPTLevel;

public record TopicFilterRequest(
        String keyword,
        TopicStatus status,
        JLPTLevel jlptLevel
) {
}
