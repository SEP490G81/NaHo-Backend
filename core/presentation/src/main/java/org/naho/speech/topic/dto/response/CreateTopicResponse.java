package org.naho.speech.topic.dto.response;

import org.naho.topic.type.TopicStatus;
import org.naho.user.type.JLPTLevel;

public record CreateTopicResponse(
        Long id,
        Long userId,
        String japaneseName,
        String japaneseDescription,
        Object japaneseNameTokens,
        Object japaneseDescriptionTokens,
        TopicStatus status,
        JLPTLevel jlptLevel,
        Double orderIndex,
        Long coverImageFileId
) {
}
