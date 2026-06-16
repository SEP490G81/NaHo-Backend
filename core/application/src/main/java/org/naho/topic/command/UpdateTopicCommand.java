package org.naho.topic.command;

import org.naho.topic.type.TopicStatus;
import org.naho.user.type.JLPTLevel;

public record UpdateTopicCommand(
        Long id,
        Long requestUserId,
        boolean isAdminOrManager,
        String nameMarkup,
        String descriptionMarkup,
        TopicStatus status,
        JLPTLevel jlptLevel,
        Double orderIndex,
        Long coverImageFileId,
        Long categoryId
) {
}
