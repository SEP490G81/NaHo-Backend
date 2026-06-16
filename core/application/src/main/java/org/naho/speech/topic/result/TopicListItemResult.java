package org.naho.speech.topic.result;

import org.naho.topic.type.TopicStatus;
import org.naho.user.type.JLPTLevel;

public record TopicListItemResult(
        Long id,
        String japaneseName,
        String description,
        String japaneseNameTokens,
        String japaneseDescriptionTokens,
        TopicStatus status,
        JLPTLevel jlptLevel,
        Double orderIndex,
        Long coverImageFileId,
        Integer totalQuestions
) {
}
