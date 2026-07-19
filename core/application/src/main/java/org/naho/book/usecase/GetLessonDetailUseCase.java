package org.naho.book.usecase;

import org.naho.book.command.GetLessonDetailCommand;
import org.naho.book.exception.LessonErrorCode;
import org.naho.book.model.Lesson;
import org.naho.book.port.in.GetLessonDetailInputPort;
import org.naho.book.port.out.LessonRepositoryPort;
import org.naho.book.port.out.ObjectiveRepositoryPort;
import org.naho.book.result.LessonDetailResult;
import org.naho.book.result.ObjectiveListItemResult;
import org.naho.i18n.message.book.LessonDetailMessageKey;
import org.naho.shared.exception.ApplicationException;

public class GetLessonDetailUseCase implements GetLessonDetailInputPort {

    private final LessonRepositoryPort lessonRepositoryPort;
    private final ObjectiveRepositoryPort objectiveRepositoryPort;

    public GetLessonDetailUseCase(LessonRepositoryPort lessonRepositoryPort, ObjectiveRepositoryPort objectiveRepositoryPort) {
        this.lessonRepositoryPort = lessonRepositoryPort;
        this.objectiveRepositoryPort = objectiveRepositoryPort;
    }

    @Override
    public LessonDetailResult getLessonDetail(GetLessonDetailCommand command) {
        Lesson lesson = lessonRepositoryPort.findById(command.id())
                .orElseThrow(() -> new ApplicationException(
                        LessonErrorCode.LESSON_NOT_FOUND,
                        LessonDetailMessageKey.LESSON_ID_NOT_FOUND,
                        command.id()));

        var objectives = objectiveRepositoryPort.findByLessonId(command.id()).stream()
                .map(objective -> new ObjectiveListItemResult(
                        objective.getId(),
                        objective.getJapaneseName(),
                        objective.getJapaneseDescription(),
                        objective.getJapaneseNameMarkup(),
                        objective.getJapaneseDescriptionMarkup(),
                        objective.getStatus(),
                        objective.getOrderIndex(),
                        objective.getFirstNodeGlobalOrderIndex(),
                        objective.getLastNodeGlobalOrderIndex()
                ))
                .toList();

        return new LessonDetailResult(
                lesson.getId(),
                lesson.getJapaneseName(),
                lesson.getJapaneseDescription(),
                lesson.getJapaneseNameMarkup(),
                lesson.getJapaneseDescriptionMarkup(),
                lesson.getStatus(),
                lesson.getOrderIndex(),
                lesson.getFirstNodeGlobalOrderIndex(),
                lesson.getLastNodeGlobalOrderIndex(),
                objectives
        );
    }
}
