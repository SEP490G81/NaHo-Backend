package org.naho.speech.topic.dto.response;

import org.naho.speech.type.TopicStatus;
import org.naho.user.type.JLPTLevel;

public record TopicListItemResponse(
        Long id,
        String name,
        String description,
        Object japaneseNameTokens,
        Object japaneseDescriptionTokens,
        TopicStatus status,
        JLPTLevel jlptLevel,
        Double orderIndex,
        Long coverImageFileId,
        Integer totalQuestions
) {
}
