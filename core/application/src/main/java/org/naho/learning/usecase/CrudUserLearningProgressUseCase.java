package org.naho.learning.usecase;

import org.naho.i18n.message.learning.LearningPathNodeDetailMessageKey;
import org.naho.i18n.message.learning.UserLearningProgressDetailMessageKey;
import org.naho.learning.command.UpdateFarthestAvailableNodeCommand;
import org.naho.learning.exception.LearningPathNodeErrorCode;
import org.naho.learning.exception.UserLearningProgressErrorCode;
import org.naho.learning.mapper.UserLearningProgressResultMapper;
import org.naho.learning.model.LearningPathNode;
import org.naho.learning.model.UserLearningProgress;
import org.naho.learning.port.in.CrudUserLearningProgressInputPort;
import org.naho.learning.port.out.LearningPathNodeRepositoryPort;
import org.naho.learning.port.out.UserLearningProgressRepositoryPort;
import org.naho.learning.result.UserLearningProgressResult;
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
        // if user learned a node, return current progress
        UserLearningProgress currentUserLearningProgress = userLearningProgressRepositoryPort
                .findByUserId(userId)
                .orElseThrow(() -> new ApplicationException(
                        UserLearningProgressErrorCode.USER_LEARNING_PROGRESS_NOT_FOUND,
                        UserLearningProgressDetailMessageKey.USER_LEARNING_PROGRESS_NOT_FOUND_BY_USER_ID,
                        userId
                ));

        return userLearningProgressResultMapper.domainToDetailsResult(
                currentUserLearningProgress,
                userId
        );
    }

    @Override
    public void initUserLearningProgress(Long userId) {
        if (userLearningProgressRepositoryPort.existsByUserId(userId)) {
            throw new ApplicationException(
                    UserLearningProgressErrorCode.USER_LEARNING_PROGRESS_ALREADY_EXISTS,
                    UserLearningProgressDetailMessageKey.USER_LEARNING_PROGRESS_ALREADY_EXISTS_BY_USER_ID,
                    userId
            );
        }

        // if user doesn't learn any node, create new
        LearningPathNode firstLearningPathNode = learningPathNodeRepositoryPort
                .findFirstLearningPathNode()
                .orElseThrow(() -> new ApplicationException(
                        LearningPathNodeErrorCode.LEARNING_PATH_NODE_NOT_FOUND,
                        LearningPathNodeDetailMessageKey.LEARNING_PATH_NODE_ID_NOT_FOUND
                ));

        UserLearningProgress userLearningProgress = UserLearningProgress.init(
                firstLearningPathNode.getId(),
                firstLearningPathNode.getGlobalOrderIndex()
        );

        UserLearningProgress savedUserLearningProgress =
                userLearningProgressRepositoryPort.createNew(userLearningProgress, userId);

        userLearningProgressResultMapper.domainToResult(savedUserLearningProgress);
    }

    @Override
    public UserLearningProgress updateFarthestAvailableNodeWhenCompletedANode(UpdateFarthestAvailableNodeCommand command) {
        UserLearningProgress progress = command.userLearningProgress();
        LearningPathNode currentLearningPathNode = command.currentLearningPathNode();

        Double farthestAvailableNodeGlobalOrderIndex = progress.getFarthestAvailableNodeGlobalOrderIndex();
        Double currentLearningPathNodeGlobalOrderIndex = currentLearningPathNode.getGlobalOrderIndex();

        // nếu trạng thái học bất thường: node xa nhất lại gần hơn cả node hiện tại đang học
        if (farthestAvailableNodeGlobalOrderIndex < currentLearningPathNodeGlobalOrderIndex) {
            throw new ApplicationException(
                    UserLearningProgressErrorCode.USER_LEARNING_PROGRESS_INVALID,
                    UserLearningProgressDetailMessageKey.USER_LEARNING_PROGRESS_INVALID
            );
        }

        // nếu đã học tới node xa hơn node hiện tại đã học xong
        if (farthestAvailableNodeGlobalOrderIndex > currentLearningPathNodeGlobalOrderIndex) {
            return progress;
        }

        // Lấy ra node tiếp theo (ngay sau node hiện tại đang/đã học)
        LearningPathNode nextLearningPathNode = learningPathNodeRepositoryPort
                .findTopByGlobalOrderIndexGreaterThanOrderByGlobalOrderIndex(currentLearningPathNodeGlobalOrderIndex)
                .orElse(null);

        // nếu đã học tới node cuối cùng
        if (nextLearningPathNode == null) {
            return progress;
        }

        progress.setFarthestAvailableNodeId(nextLearningPathNode.getId());
        progress.setFarthestAvailableNodeGlobalOrderIndex(nextLearningPathNode.getGlobalOrderIndex());
        return userLearningProgressRepositoryPort.save(progress);
    }
}
