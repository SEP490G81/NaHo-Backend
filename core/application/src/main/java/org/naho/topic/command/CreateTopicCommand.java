package org.naho.topic.command;

import org.naho.user.type.JLPTLevel;

public record CreateTopicCommand(
        Long userId,
        String nameMarkup,
        String descriptionMarkup,
        JLPTLevel jlptLevel,
        Double orderIndex,
        Long coverImageFileId,
        Long categoryId
) {
}
