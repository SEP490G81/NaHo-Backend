package org.naho.topic.dto.response;

import org.naho.topic.type.TopicStatus;
import org.naho.user.type.JLPTLevel;

public record TopicListItemResponse(
        Long id,
        String japaneseName,
        String japaneseDescription,
        String japaneseNameMarkup,
        String japaneseDescriptionMarkup,
        TopicStatus status,
        JLPTLevel jlptLevel,
        Double orderIndex,
        Long coverImageFileId,
        Long categoryId,
        Integer totalQuestions
) {
}
