package org.naho.speech.topic.result;

import org.naho.speech.type.TopicStatus;
import org.naho.user.type.JLPTLevel;

public record CreateTopicResult(
        Long id,
        Long userId,
        String japaneseName,
        String japaneseDescription,
        String japaneseNameTokens,
        String japaneseDescriptionTokens,
        TopicStatus status,
        JLPTLevel jlptLevel,
        Double orderIndex,
        Long coverImageFileId
) {
}
