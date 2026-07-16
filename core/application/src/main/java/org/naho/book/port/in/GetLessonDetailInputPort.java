package org.naho.book.port.in;

import org.naho.book.command.GetLessonDetailCommand;
import org.naho.book.result.LessonDetailResult;

public interface GetLessonDetailInputPort {
    LessonDetailResult getLessonDetail(GetLessonDetailCommand command);
}
