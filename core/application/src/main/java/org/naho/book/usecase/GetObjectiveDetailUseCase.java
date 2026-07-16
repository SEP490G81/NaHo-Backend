package org.naho.book.usecase;

import lombok.RequiredArgsConstructor;
import org.naho.book.command.GetObjectiveDetailCommand;
import org.naho.book.exception.ObjectiveErrorCode;
import org.naho.book.model.Objective;
import org.naho.book.port.in.GetObjectiveDetailInputPort;
import org.naho.book.port.out.ObjectiveRepositoryPort;
import org.naho.book.result.ObjectiveDetailResult;
import org.naho.i18n.message.book.ObjectiveDetailMessageKey;
import org.naho.question.port.out.SpeakingQuestionRepositoryPort;
import org.naho.question.result.SpeakingQuestionListItemResult;
import org.naho.shared.exception.ApplicationException;

@RequiredArgsConstructor
public class GetObjectiveDetailUseCase implements GetObjectiveDetailInputPort {
    private final ObjectiveRepositoryPort objectiveRepositoryPort;
    private final SpeakingQuestionRepositoryPort questionRepositoryPort;

    @Override
    public ObjectiveDetailResult getObjectiveDetail(GetObjectiveDetailCommand command) {
        Objective objective = objectiveRepositoryPort.findById(command.id())
                .orElseThrow(() -> new ApplicationException(
                        ObjectiveErrorCode.OBJECTIVE_NOT_FOUND,
                        ObjectiveDetailMessageKey.OBJECTIVE_ID_NOT_FOUND,
                        command.id()));

        var questions = questionRepositoryPort.findByObjectiveId(command.id()).stream()
                .map(question -> new SpeakingQuestionListItemResult(
                        question.getId(),
                        question.getUserId(),
                        question.getQuestionAudioFileId(),
                        question.getTitle(),
                        question.getTitleMarkup(),
                        question.getDescription(),
                        question.getDescriptionMarkup(),
                        question.getOrderIndex(),
                        question.getStatus()
                ))
                .toList();

        return new ObjectiveDetailResult(
                objective.getId(),
                objective.getJapaneseName(),
                objective.getJapaneseDescription(),
                objective.getJapaneseNameMarkup(),
                objective.getJapaneseDescriptionMarkup(),
                objective.getStatus(),
                objective.getOrderIndex(),
                questions
        );
    }
}
