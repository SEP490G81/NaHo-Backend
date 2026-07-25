package org.naho.config.application;

import org.naho.chest.mapper.ChestResultMapper;
import org.naho.chest.port.in.OpenChestInputPort;
import org.naho.chest.port.out.ChestRepositoryPort;
import org.naho.chest.usecase.OpenChestUseCase;
import org.naho.learning.port.in.CrudUserLearningProgressInputPort;
import org.naho.learning.port.in.UserLearningStreakInputPort;
import org.naho.learning.port.out.LearningPathNodeRepositoryPort;
import org.naho.learning.port.out.UserLearningProgressRepositoryPort;
import org.naho.learning.port.out.UserNodeProgressRepositoryPort;
import org.naho.point.port.in.CrudPointHistoryInputPort;
import org.naho.shared.port.out.TransactionPort;
import org.naho.user.port.out.UserRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChestConfig {

    @Bean
    public ChestResultMapper chestResultMapper() {
        return new ChestResultMapper();
    }

    @Bean
    public OpenChestInputPort openChestInputPort(
            ChestRepositoryPort chestRepositoryPort,
            UserRepositoryPort userRepositoryPort,
            CrudPointHistoryInputPort crudPointHistoryInputPort,
            UserLearningProgressRepositoryPort userLearningProgressRepositoryPort,
            TransactionPort transactionPort,
            LearningPathNodeRepositoryPort learningPathNodeRepositoryPort,
            UserNodeProgressRepositoryPort userNodeProgressRepositoryPort,
            UserLearningStreakInputPort userLearningStreakInputPort,
            CrudUserLearningProgressInputPort crudUserLearningProgressInputPort
    ) {
        return new OpenChestUseCase(
                chestRepositoryPort,
                userRepositoryPort,
                crudPointHistoryInputPort,
                userLearningProgressRepositoryPort,
                transactionPort,
                learningPathNodeRepositoryPort,
                userNodeProgressRepositoryPort,
                userLearningStreakInputPort,
                crudUserLearningProgressInputPort
        );
    }
}
