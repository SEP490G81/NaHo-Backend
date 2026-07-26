package org.naho.config.application;

import org.naho.chest.adapter.ChestRepositoryAdapter;
import org.naho.learning.adapter.LearningPathNodeRepositoryAdapter;
import org.naho.learning.adapter.UserLearningProgressRepositoryAdapter;
import org.naho.learning.mapper.UserLearningProgressResultMapper;
import org.naho.learning.port.in.CrudUserLearningProgressInputPort;
import org.naho.learning.port.in.GetLearningPathNodeDetailInputPort;
import org.naho.learning.port.in.UserLearningStreakInputPort;
import org.naho.learning.usecase.CrudUserLearningProgressUseCase;
import org.naho.learning.usecase.GetLearningPathNodeDetailUseCase;
import org.naho.learning.usecase.UserLearningStreakUseCase;
import org.naho.question.adapter.SpeakingQuestionRepositoryAdapter;
import org.naho.question.adapter.VocabularyQuestionRepositoryAdapter;
import org.naho.user.port.out.UserRepositoryPort;
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

    @Bean
    public UserLearningProgressResultMapper userLearningProgressResultMapper(
            UserRepositoryPort userRepositoryPort
    ) {
        return new UserLearningProgressResultMapper(
                userRepositoryPort
        );
    }

    @Bean
    public CrudUserLearningProgressInputPort crudUserLearningProgressInputPort(
            UserLearningProgressRepositoryAdapter userLearningProgressRepositoryAdapter,
            LearningPathNodeRepositoryAdapter learningPathNodeRepositoryAdapter,
            UserLearningProgressResultMapper userLearningProgressResultMapper
    ) {
        return new CrudUserLearningProgressUseCase(
                userLearningProgressRepositoryAdapter,
                learningPathNodeRepositoryAdapter,
                userLearningProgressResultMapper
        );
    }

    @Bean
    public UserLearningStreakInputPort userLearningStreakInputPort() {
        return new UserLearningStreakUseCase();
    }
}
