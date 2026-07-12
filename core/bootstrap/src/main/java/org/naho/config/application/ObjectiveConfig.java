package org.naho.config.application;

import org.naho.book.adapter.ObjectiveRepositoryAdapter;
import org.naho.book.port.in.GetObjectiveDetailInputPort;
import org.naho.book.usecase.GetObjectiveDetailUseCase;
import org.naho.question.adapter.QuestionRepositoryAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectiveConfig {

    @Bean
    public GetObjectiveDetailInputPort getObjectiveDetailInputPort(
            ObjectiveRepositoryAdapter objectiveRepositoryAdapter,
            QuestionRepositoryAdapter questionRepositoryAdapter
    ) {
        return new GetObjectiveDetailUseCase(objectiveRepositoryAdapter, questionRepositoryAdapter);
    }
}
