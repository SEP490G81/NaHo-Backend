package org.naho.config.application;

import org.naho.learning.adapter.LearningPathNodeRepositoryAdapter;
import org.naho.learning.port.in.GetLearningPathNodeDetailInputPort;
import org.naho.learning.usecase.GetLearningPathNodeDetailUseCase;
import org.naho.question.adapter.SpeakingQuestionRepositoryAdapter;
import org.naho.question.adapter.VocabularyQuestionRepositoryAdapter;
import org.naho.question.adapter.ChestRepositoryAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LearningConfig {

    @Bean
    public GetLearningPathNodeDetailInputPort getLearningPathNodeDetailInputPort(
            LearningPathNodeRepositoryAdapter learningPathNodeRepositoryAdapter,
            SpeakingQuestionRepositoryAdapter speakingQuestionRepositoryAdapter,
            VocabularyQuestionRepositoryAdapter vocabularyQuestionRepositoryAdapter,
            ChestRepositoryAdapter chestRepositoryAdapter
    ) {
        return new GetLearningPathNodeDetailUseCase(
                learningPathNodeRepositoryAdapter,
                speakingQuestionRepositoryAdapter,
                vocabularyQuestionRepositoryAdapter,
                chestRepositoryAdapter
        );
    }
}
