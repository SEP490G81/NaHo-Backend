package org.naho.speech.topic.dto.request;

import jakarta.validation.constraints.NotNull;
import org.naho.topic.type.TopicStatus;
import org.naho.user.type.JLPTLevel;

public record UpdateTopicRequest(
        String japaneseName,
        String description,
        Object nameTokens,
        Object descriptionTokens,
        @NotNull TopicStatus status,
        @NotNull JLPTLevel jlptLevel,
        Double orderIndex,
        Long coverImageFileId
) {
}
