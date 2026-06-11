package org.naho.speech.topic.command;

import org.naho.user.type.JLPTLevel;

public record CreateTopicCommand(
        Long userId,
        String name,
        String description,
        String nameTokens,
        String descriptionTokens,
        JLPTLevel jlptLevel,
        Double orderIndex,
        Long coverImageFileId
) {
}
