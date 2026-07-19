package org.naho.book.usecase;


import org.naho.book.command.GetObjectiveDetailCommand;
import org.naho.book.exception.ObjectiveErrorCode;
import org.naho.book.model.Objective;
import org.naho.book.port.in.GetObjectiveDetailInputPort;
import org.naho.book.port.out.ObjectiveRepositoryPort;
import org.naho.book.result.ObjectiveDetailResult;
import org.naho.i18n.message.book.ObjectiveDetailMessageKey;
import org.naho.learning.port.out.LearningPathNodeRepositoryPort;
import org.naho.learning.result.LearningPathNodeListItemResult;
import org.naho.shared.exception.ApplicationException;

public class GetObjectiveDetailUseCase implements GetObjectiveDetailInputPort {
    private final ObjectiveRepositoryPort objectiveRepositoryPort;
    private final LearningPathNodeRepositoryPort learningPathNodeRepositoryPort;

    public GetObjectiveDetailUseCase(
            ObjectiveRepositoryPort objectiveRepositoryPort,
            LearningPathNodeRepositoryPort learningPathNodeRepositoryPort
    ) {
        this.objectiveRepositoryPort = objectiveRepositoryPort;
        this.learningPathNodeRepositoryPort = learningPathNodeRepositoryPort;
    }

    @Override
    public ObjectiveDetailResult getObjectiveDetail(GetObjectiveDetailCommand command) {
        Objective objective = objectiveRepositoryPort.findById(command.id())
                .orElseThrow(() -> new ApplicationException(
                        ObjectiveErrorCode.OBJECTIVE_NOT_FOUND,
                        ObjectiveDetailMessageKey.OBJECTIVE_ID_NOT_FOUND,
                        command.id()));

        var nodes = learningPathNodeRepositoryPort.findByObjectiveId(command.id()).stream()
                .map(node -> new LearningPathNodeListItemResult(
                        node.getId(),
                        node.getObjectiveId(),
                        node.getSpeakingQuestionId(),
                        node.getVocabularyQuestionId(),
                        node.getChestId(),
                        node.getGlobalOrderIndex(),
                        node.getOrderIndex(),
                        node.getNodeType()
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
                nodes
        );
    }
}
