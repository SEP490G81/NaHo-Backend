package org.naho.book.command;

import org.naho.book.type.TopicStatus;

public record UpdateLessonCommand(
        Long lessonId,
        String japaneseName,
        String japaneseDescription,
        TopicStatus status,
        Long adminUserId,
        boolean isAdminOrManager
) {
}
