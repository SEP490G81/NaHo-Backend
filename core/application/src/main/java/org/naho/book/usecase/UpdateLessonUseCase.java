package org.naho.book.usecase;


import org.naho.book.command.UpdateLessonCommand;
import org.naho.book.exception.LessonErrorCode;
import org.naho.book.model.Lesson;
import org.naho.book.port.in.UpdateLessonInputPort;
import org.naho.book.port.out.LessonRepositoryPort;
import org.naho.book.result.LessonListItemResult;
import org.naho.furigana.port.out.FuriganaGenerationPort;
import org.naho.i18n.message.book.LessonDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;

public class UpdateLessonUseCase implements UpdateLessonInputPort {

    private final LessonRepositoryPort lessonRepositoryPort;
    private final TransactionPort transactionPort;
    private final FuriganaGenerationPort furiganaGenerationPort;

    public UpdateLessonUseCase(LessonRepositoryPort lessonRepositoryPort, TransactionPort transactionPort, FuriganaGenerationPort furiganaGenerationPort) {
        this.lessonRepositoryPort = lessonRepositoryPort;
        this.transactionPort = transactionPort;
        this.furiganaGenerationPort = furiganaGenerationPort;
    }

    @Override
    public LessonListItemResult updateLesson(UpdateLessonCommand command) {
        if (!command.isAdminOrManager()) {
            throw new ApplicationException(
                    LessonErrorCode.LESSON_UPDATE_FORBIDDEN,
                    LessonDetailMessageKey.LESSON_UPDATE_FORBIDDEN
            );
        }

        return transactionPort.execute(() -> {
            Lesson existingLesson = lessonRepositoryPort.findById(command.lessonId())
                    .orElseThrow(() -> new ApplicationException(
                            LessonErrorCode.LESSON_NOT_FOUND,
                            LessonDetailMessageKey.LESSON_ID_NOT_FOUND
                    ));

            String japaneseNameMarkup = furiganaGenerationPort.generateFuriganaMarkup(command.japaneseName());
            String japaneseDescriptionMarkup = command.japaneseDescription() != null
                    ? furiganaGenerationPort.generateFuriganaMarkup(command.japaneseDescription())
                    : null;

            existingLesson.update(
                    command.japaneseName(),
                    command.japaneseDescription(),
                    japaneseNameMarkup,
                    japaneseDescriptionMarkup,
                    command.status(),
                    existingLesson.getOrderIndex()
            );

            lessonRepositoryPort.save(existingLesson);

            return new LessonListItemResult(
                    existingLesson.getId(),
                    existingLesson.getJapaneseName(),
                    existingLesson.getJapaneseDescription(),
                    existingLesson.getJapaneseNameMarkup(),
                    existingLesson.getJapaneseDescriptionMarkup(),
                    existingLesson.getStatus(),
                    existingLesson.getOrderIndex(),
                    existingLesson.getFirstNodeGlobalOrderIndex(),
                    existingLesson.getLastNodeGlobalOrderIndex()
            );
        });
    }
}
