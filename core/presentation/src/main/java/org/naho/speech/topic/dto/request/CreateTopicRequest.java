package org.naho.speech.topic.dto.request;

import org.naho.user.type.JLPTLevel;

public record CreateTopicRequest(
        String name,
        String description,
        Object nameTokens,
        Object descriptionTokens,
        JLPTLevel jlptLevel,
        Double orderIndex,
        Long coverImageFileId
) {
}
