package org.naho.book.port.in;

import org.naho.book.command.UpdateLessonCommand;
import org.naho.book.result.LessonListItemResult;

public interface UpdateLessonInputPort {
    LessonListItemResult updateLesson(UpdateLessonCommand command);
}
