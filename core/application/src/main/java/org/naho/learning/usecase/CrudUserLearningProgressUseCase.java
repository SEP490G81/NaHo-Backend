package org.naho.learning.usecase;

import org.naho.i18n.message.learning.LearningPathNodeDetailMessageKey;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.learning.exception.LearningPathNodeErrorCode;
import org.naho.learning.mapper.UserLearningProgressResultMapper;
import org.naho.learning.model.LearningPathNode;
import org.naho.learning.model.UserLearningProgress;
import org.naho.learning.port.in.CrudUserLearningProgressInputPort;
import org.naho.learning.port.out.LearningPathNodeRepositoryPort;
import org.naho.learning.port.out.UserLearningProgressRepositoryPort;
import org.naho.learning.result.UserLearningProgressResult;
import org.naho.shared.exception.ApplicationException;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.port.out.UserRepositoryPort;
import org.naho.user.result.LeaderboardUserResult;

public class CrudUserLearningProgressUseCase implements CrudUserLearningProgressInputPort {

    private final UserLearningProgressRepositoryPort userLearningProgressRepositoryPort;
    private final LearningPathNodeRepositoryPort learningPathNodeRepositoryPort;
    private final UserLearningProgressResultMapper userLearningProgressResultMapper;
    private final UserRepositoryPort userRepositoryPort;

    public CrudUserLearningProgressUseCase(
            UserLearningProgressRepositoryPort userLearningProgressRepositoryPort,
            LearningPathNodeRepositoryPort learningPathNodeRepositoryPort,
            UserLearningProgressResultMapper userLearningProgressResultMapper,
            UserRepositoryPort userRepositoryPort
    ) {
        this.userLearningProgressRepositoryPort = userLearningProgressRepositoryPort;
        this.learningPathNodeRepositoryPort = learningPathNodeRepositoryPort;
        this.userLearningProgressResultMapper = userLearningProgressResultMapper;
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public UserLearningProgressResult findUserLearningProgressByUserId(Long userId) {
        // if user learned a node, return current progress
        UserLearningProgress currentUserLearningProgress = userLearningProgressRepositoryPort
                .findUserLearningProgressByUserId(userId)
                .orElse(null);

        // if user doesn't learn any node, create new
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
                    userLearningProgressRepositoryPort.createNew(userLearningProgress, userId);
        }

        LeaderboardUserResult leaderboardUserResult =
                userRepositoryPort.findTopOfUserByUserId(userId)
                        .orElseThrow(() -> new ApplicationException(
                                UserErrorCode.USER_NOT_FOUND,
                                UserDetailMessageKey.USER_ID_NOT_FOUND
                        ));

        UserLearningProgressResult userLearningProgressResult =
                userLearningProgressResultMapper.domainToResult(currentUserLearningProgress);

        userLearningProgressResult.setLeaderboardUser(leaderboardUserResult);
        
        return userLearningProgressResult;
    }
}
