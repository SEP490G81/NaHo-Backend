package org.naho.topic.result;

import org.naho.topic.type.TopicStatus;
import org.naho.user.type.JLPTLevel;

public record TopicListItemResult(
        Long id,
        String name,
        String description,
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
