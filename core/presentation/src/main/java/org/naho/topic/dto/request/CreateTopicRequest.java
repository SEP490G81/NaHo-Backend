package org.naho.topic.dto.request;

import org.naho.user.type.JLPTLevel;

public record CreateTopicRequest(
        String japaneseNameMarkup,
        String japaneseDescriptionMarkup,
        JLPTLevel jlptLevel,
        Double orderIndex,
        Long coverImageFileId
) {
}
