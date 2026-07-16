package org.naho.config.application;

import org.naho.book.adapter.ObjectiveRepositoryAdapter;
import org.naho.book.port.in.GetObjectiveDetailInputPort;
import org.naho.book.usecase.GetObjectiveDetailUseCase;
import org.naho.learning.adapter.LearningPathNodeRepositoryAdapter;
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
}
