package org.naho.learning.usecase;

import org.naho.learning.mapper.UserLearningProgressResultMapper;
import org.naho.learning.model.LearningPathNode;
import org.naho.learning.model.UserLearningProgress;
import org.naho.learning.port.in.CrudUserLearningProgressInputPort;
import org.naho.learning.port.out.LearningPathNodeRepositoryPort;
import org.naho.learning.port.out.UserLearningProgressRepositoryPort;
import org.naho.learning.result.UserLearningProgressResult;
import org.naho.i18n.message.learning.LearningPathNodeDetailMessageKey;
import org.naho.learning.exception.LearningPathNodeErrorCode;
import org.naho.shared.exception.ApplicationException;

public class CrudUserLearningProgressUseCase implements CrudUserLearningProgressInputPort {

    private final UserLearningProgressRepositoryPort userLearningProgressRepositoryPort;
    private final LearningPathNodeRepositoryPort learningPathNodeRepositoryPort;
    private final UserLearningProgressResultMapper userLearningProgressResultMapper;

    public CrudUserLearningProgressUseCase(
            UserLearningProgressRepositoryPort userLearningProgressRepositoryPort,
            LearningPathNodeRepositoryPort learningPathNodeRepositoryPort,
            UserLearningProgressResultMapper userLearningProgressResultMapper
    ) {
        this.userLearningProgressRepositoryPort = userLearningProgressRepositoryPort;
        this.learningPathNodeRepositoryPort = learningPathNodeRepositoryPort;
        this.userLearningProgressResultMapper = userLearningProgressResultMapper;
    }

    @Override
    public UserLearningProgressResult findUserLearningProgressByUserId(Long userId) {
        UserLearningProgress currentUserLearningProgress = userLearningProgressRepositoryPort
                .findUserLearningProgressByUserId(userId)
                .orElse(null);

        if (currentUserLearningProgress == null) {
            LearningPathNode firstLearningPathNode = learningPathNodeRepositoryPort
                    .findFirstLearningPathNode()
                    .orElseThrow(() -> new ApplicationException(
                            LearningPathNodeErrorCode.LEARNING_PATH_NODE_NOT_FOUND,
                            LearningPathNodeDetailMessageKey.LEARNING_PATH_NODE_ID_NOT_FOUND
                    ));

            UserLearningProgress userLearningProgress =
                    UserLearningProgress.init(firstLearningPathNode.getId());

            currentUserLearningProgress =
                    userLearningProgressRepositoryPort.save(userLearningProgress, userId);
        }
        
        return userLearningProgressResultMapper.domainToResult(currentUserLearningProgress);
    }
}
