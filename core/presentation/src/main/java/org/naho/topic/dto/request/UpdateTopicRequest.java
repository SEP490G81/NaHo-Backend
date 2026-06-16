package org.naho.topic.dto.request;

import jakarta.validation.constraints.NotNull;
import org.naho.topic.type.TopicStatus;
import org.naho.user.type.JLPTLevel;

public record UpdateTopicRequest(
        String nameMarkup,
        String descriptionMarkup,
        @NotNull TopicStatus status,
        @NotNull JLPTLevel jlptLevel,
        Double orderIndex,
        Long coverImageFileId,
        Long categoryId
) {
}

