package org.naho.book.command;

import org.naho.book.type.TopicStatus;

public record UpdateObjectiveCommand(
        Long objectiveId,
        String japaneseName,
        String japaneseDescription,
        TopicStatus status,
        Long adminUserId,
        boolean isAdminOrManager
) {
}
