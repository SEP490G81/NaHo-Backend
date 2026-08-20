package org.naho.config.application;

import org.naho.book.port.in.GetObjectiveDetailInputPort;
import org.naho.book.port.in.UpdateObjectiveInputPort;
import org.naho.book.port.out.ObjectiveRepositoryPort;
import org.naho.book.usecase.GetObjectiveDetailUseCase;
import org.naho.book.usecase.UpdateObjectiveUseCase;
import org.naho.furigana.port.out.FuriganaGenerationPort;
import org.naho.learning.port.out.LearningPathNodeRepositoryPort;
import org.naho.shared.port.out.TransactionPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectiveConfig {

    @Bean
    public GetObjectiveDetailInputPort getObjectiveDetailInputPort(
            ObjectiveRepositoryPort objectiveRepositoryPort,
            LearningPathNodeRepositoryPort learningPathNodeRepositoryPort
    ) {
        return new GetObjectiveDetailUseCase(objectiveRepositoryPort, learningPathNodeRepositoryPort);
    }

    @Bean
    public UpdateObjectiveInputPort updateObjectiveInputPort(
            ObjectiveRepositoryPort objectiveRepositoryPort,
            TransactionPort transactionPort,
            FuriganaGenerationPort furiganaGenerationPort
    ) {
        return new UpdateObjectiveUseCase(
                objectiveRepositoryPort,
                transactionPort,
                furiganaGenerationPort
        );
    }
}
