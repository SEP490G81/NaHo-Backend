package org.naho.topic.command;

import org.naho.user.type.JLPTLevel;

public record CreateTopicCommand(
        Long userId,
        String japaneseNameMarkup,
        String japaneseDescriptionMarkup,
        JLPTLevel jlptLevel,
        Double orderIndex,
        Long coverImageFileId,
        Long categoryId
) {
}
