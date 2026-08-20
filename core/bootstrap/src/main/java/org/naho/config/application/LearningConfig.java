package org.naho.config.application;

import org.naho.chest.mapper.ChestResultMapper;
import org.naho.chest.port.out.ChestRepositoryPort;
import org.naho.learning.mapper.UserLearningProgressResultMapper;
import org.naho.learning.mapper.UserNodeProgressResultMapper;
import org.naho.learning.port.in.CrudUserLearningProgressInputPort;
import org.naho.learning.port.in.CrudUserNodeProgressPort;
import org.naho.learning.port.in.GetLearningPathNodeDetailInputPort;
import org.naho.learning.port.in.UserLearningStreakInputPort;
import org.naho.learning.port.out.LearningPathNodeRepositoryPort;
import org.naho.learning.port.out.UserLearningProgressRepositoryPort;
import org.naho.learning.port.out.UserNodeProgressRepositoryPort;
import org.naho.learning.usecase.CrudUserLearningProgressUseCase;
import org.naho.learning.usecase.CrudUserNodeProgressUseCase;
import org.naho.learning.usecase.GetLearningPathNodeDetailUseCase;
import org.naho.learning.usecase.UserLearningStreakUseCase;
import org.naho.question.mapper.SpeakingQuestionResultMapper;
import org.naho.question.port.in.GetSpeakingQuestionInputPort;
import org.naho.question.port.out.SpeakingQuestionRepositoryPort;
import org.naho.question.port.out.VocabularyQuestionRepositoryPort;
import org.naho.subscription.port.in.GetActiveSubscriptionInputPort;
import org.naho.user.port.out.UserRepositoryPort;
import org.naho.vocabulary.mapper.VocabularyResultMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LearningConfig {

    @Bean
    public GetLearningPathNodeDetailInputPort getLearningPathNodeDetailInputPort(
            LearningPathNodeRepositoryPort learningPathNodeRepositoryPort,
            SpeakingQuestionRepositoryPort speakingQuestionRepositoryPort,
            VocabularyQuestionRepositoryPort vocabularyQuestionRepositoryPort,
            ChestRepositoryPort chestRepositoryPort,
            GetActiveSubscriptionInputPort getActiveSubscriptionInputPort,
            SpeakingQuestionResultMapper speakingQuestionResultMapper,
            VocabularyResultMapper vocabularyResultMapper,
            ChestResultMapper chestResultMapper,
            GetSpeakingQuestionInputPort getSpeakingQuestionInputPort
    ) {
        return new GetLearningPathNodeDetailUseCase(
                learningPathNodeRepositoryPort,
                speakingQuestionRepositoryPort,
                vocabularyQuestionRepositoryPort,
                chestRepositoryPort,
                getActiveSubscriptionInputPort,
                speakingQuestionResultMapper,
                vocabularyResultMapper,
                chestResultMapper,
                getSpeakingQuestionInputPort
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
            UserLearningProgressRepositoryPort userLearningProgressRepositoryPort,
            LearningPathNodeRepositoryPort learningPathNodeRepositoryPort,
            UserLearningProgressResultMapper userLearningProgressResultMapper
    ) {
        return new CrudUserLearningProgressUseCase(
                userLearningProgressRepositoryPort,
                learningPathNodeRepositoryPort,
                userLearningProgressResultMapper
        );
    }

    @Bean
    public UserLearningStreakInputPort userLearningStreakInputPort() {
        return new UserLearningStreakUseCase();
    }

    @Bean
    public UserNodeProgressResultMapper userNodeProgressResultMapper() {
        return new UserNodeProgressResultMapper();
    }

    @Bean
    public CrudUserNodeProgressPort crudUserNodeProgressPort(
            UserNodeProgressRepositoryPort userNodeProgressRepositoryPort,
            UserNodeProgressResultMapper userNodeProgressResultMapper
    ) {
        return new CrudUserNodeProgressUseCase(
                userNodeProgressRepositoryPort,
                userNodeProgressResultMapper
        );
    }
}
