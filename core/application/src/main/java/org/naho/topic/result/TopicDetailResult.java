package org.naho.topic.result;

import org.naho.topic.type.TopicStatus;
import org.naho.user.type.JLPTLevel;

public record TopicDetailResult(
        Long id,
        Long userId,
        String japaneseName,
        String japaneseDescription,
        String japaneseNameMarkup,
        String japaneseDescriptionMarkup,
        TopicStatus status,
        JLPTLevel jlptLevel,
        Double orderIndex,
        Long coverImageFileId,
        Long categoryId
) {
}
