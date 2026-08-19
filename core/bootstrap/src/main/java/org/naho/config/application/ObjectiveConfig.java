package org.naho.config.application;

import org.naho.book.adapter.ObjectiveRepositoryAdapter;
import org.naho.book.port.in.GetObjectiveDetailInputPort;
import org.naho.book.port.in.UpdateObjectiveInputPort;
import org.naho.book.port.out.ObjectiveRepositoryPort;
import org.naho.book.usecase.GetObjectiveDetailUseCase;
import org.naho.book.usecase.UpdateObjectiveUseCase;
import org.naho.learning.adapter.LearningPathNodeRepositoryAdapter;
import org.naho.shared.port.out.TransactionPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectiveConfig {

    @Bean
    public GetObjectiveDetailInputPort getObjectiveDetailInputPort(
            ObjectiveRepositoryAdapter objectiveRepositoryAdapter,
            LearningPathNodeRepositoryAdapter learningPathNodeRepositoryAdapter
    ) {
        return new GetObjectiveDetailUseCase(objectiveRepositoryAdapter, learningPathNodeRepositoryAdapter);
    }

    @Bean
    public UpdateObjectiveInputPort updateObjectiveInputPort(
            ObjectiveRepositoryPort objectiveRepositoryPort,
            TransactionPort transactionPort,
            org.naho.furigana.port.out.FuriganaGenerationPort furiganaGenerationPort
    ) {
        return new UpdateObjectiveUseCase(
                objectiveRepositoryPort,
                transactionPort,
                furiganaGenerationPort
        );
    }
}
