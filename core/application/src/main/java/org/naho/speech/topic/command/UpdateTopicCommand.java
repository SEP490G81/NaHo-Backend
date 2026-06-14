package org.naho.speech.topic.command;

import org.naho.topic.type.TopicStatus;
import org.naho.user.type.JLPTLevel;

public record UpdateTopicCommand(
        Long id,
        Long requestUserId,
        boolean isAdminOrManager,
        String japaneseName,
        String description,
        String nameTokens,
        String descriptionTokens,
        TopicStatus status,
        JLPTLevel jlptLevel,
        Double orderIndex,
        Long coverImageFileId
) {
}
