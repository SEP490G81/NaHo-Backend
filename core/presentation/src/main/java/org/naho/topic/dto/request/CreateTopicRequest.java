package org.naho.topic.dto.request;

import org.naho.user.type.JLPTLevel;

public record CreateTopicRequest(
        String nameMarkup,
        String descriptionMarkup,
        JLPTLevel jlptLevel,
        Double orderIndex,
        Long coverImageFileId,
        Long categoryId
) {
}
