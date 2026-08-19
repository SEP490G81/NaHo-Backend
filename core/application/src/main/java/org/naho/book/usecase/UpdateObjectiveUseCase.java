package org.naho.book.usecase;

import org.naho.book.command.UpdateObjectiveCommand;
import org.naho.book.exception.ObjectiveErrorCode;
import org.naho.book.model.Objective;
import org.naho.book.port.in.UpdateObjectiveInputPort;
import org.naho.book.port.out.ObjectiveRepositoryPort;
import org.naho.book.result.ObjectiveListItemResult;
import org.naho.furigana.port.out.FuriganaGenerationPort;
import org.naho.i18n.message.book.ObjectiveDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;

public class UpdateObjectiveUseCase implements UpdateObjectiveInputPort {

    private final ObjectiveRepositoryPort objectiveRepositoryPort;
    private final TransactionPort transactionPort;
    private final FuriganaGenerationPort furiganaGenerationPort;

    public UpdateObjectiveUseCase(ObjectiveRepositoryPort objectiveRepositoryPort, TransactionPort transactionPort, FuriganaGenerationPort furiganaGenerationPort) {
        this.objectiveRepositoryPort = objectiveRepositoryPort;
        this.transactionPort = transactionPort;
        this.furiganaGenerationPort = furiganaGenerationPort;
    }

    @Override
    public ObjectiveListItemResult updateObjective(UpdateObjectiveCommand command) {
        if (!command.isAdminOrManager()) {
            throw new ApplicationException(
                    ObjectiveErrorCode.OBJECTIVE_UPDATE_FORBIDDEN,
                    ObjectiveDetailMessageKey.OBJECTIVE_UPDATE_FORBIDDEN
            );
        }

        return transactionPort.execute(() -> {
            Objective existingObjective = objectiveRepositoryPort.findById(command.objectiveId())
                    .orElseThrow(() -> new ApplicationException(
                            ObjectiveErrorCode.OBJECTIVE_NOT_FOUND,
                            ObjectiveDetailMessageKey.OBJECTIVE_ID_NOT_FOUND
                    ));

            String japaneseNameMarkup = furiganaGenerationPort.generateFuriganaMarkup(command.japaneseName());
            String japaneseDescriptionMarkup = command.japaneseDescription() != null
                    ? furiganaGenerationPort.generateFuriganaMarkup(command.japaneseDescription())
                    : null;

            existingObjective.update(
                    command.japaneseName(),
                    command.japaneseDescription(),
                    japaneseNameMarkup,
                    japaneseDescriptionMarkup,
                    command.status(),
                    existingObjective.getOrderIndex()
            );

            objectiveRepositoryPort.save(existingObjective);

            return new ObjectiveListItemResult(
                    existingObjective.getId(),
                    existingObjective.getJapaneseName(),
                    existingObjective.getJapaneseDescription(),
                    existingObjective.getJapaneseNameMarkup(),
                    existingObjective.getJapaneseDescriptionMarkup(),
                    existingObjective.getStatus(),
                    existingObjective.getOrderIndex(),
                    existingObjective.getFirstNodeGlobalOrderIndex(),
                    existingObjective.getLastNodeGlobalOrderIndex()
            );
        });
    }
}
