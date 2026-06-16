package org.naho.topic.command;

import org.naho.topic.type.TopicStatus;
import org.naho.user.type.JLPTLevel;

public record UpdateTopicCommand(
        Long id,
        Long requestUserId,
        boolean isAdminOrManager,
        String japaneseNameMarkup,
        String japaneseDescriptionMarkup,
        TopicStatus status,
        JLPTLevel jlptLevel,
        Double orderIndex,
        Long coverImageFileId,
        Long categoryId
) {
}
