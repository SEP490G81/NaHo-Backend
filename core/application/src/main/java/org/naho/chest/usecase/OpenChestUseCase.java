package org.naho.chest.usecase;

import org.naho.chest.command.OpenChestCommand;
import org.naho.chest.exception.ChestErrorCode;
import org.naho.chest.model.Chest;
import org.naho.chest.port.in.OpenChestInputPort;
import org.naho.chest.port.out.ChestRepositoryPort;
import org.naho.i18n.message.chest.ChestDetailMessageKey;
import org.naho.i18n.message.learning.LearningPathNodeDetailMessageKey;
import org.naho.i18n.message.learning.UserLearningProgressDetailMessageKey;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.learning.exception.LearningPathNodeErrorCode;
import org.naho.learning.exception.UserLearningProgressErrorCode;
import org.naho.learning.model.LearningPathNode;
import org.naho.learning.model.UserLearningProgress;
import org.naho.learning.model.UserNodeProgress;
import org.naho.learning.port.out.LearningPathNodeRepositoryPort;
import org.naho.learning.port.out.UserLearningProgressRepositoryPort;
import org.naho.learning.port.out.UserNodeProgressRepositoryPort;
import org.naho.learning.type.NodeStatus;
import org.naho.point.command.PointHistoryCommand;
import org.naho.point.port.in.CrudPointHistoryInputPort;
import org.naho.point.type.PointTransactionType;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.model.User;
import org.naho.user.port.out.UserRepositoryPort;

import java.time.Instant;

public class OpenChestUseCase implements OpenChestInputPort {

    private final ChestRepositoryPort chestRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final CrudPointHistoryInputPort crudPointHistoryInputPort;
    private final UserLearningProgressRepositoryPort userLearningProgressRepositoryPort;
    private final TransactionPort transactionPort;
    private final LearningPathNodeRepositoryPort learningPathNodeRepositoryPort;
    private final UserNodeProgressRepositoryPort userNodeProgressRepositoryPort;

    public OpenChestUseCase(
            ChestRepositoryPort chestRepositoryPort,
            UserRepositoryPort userRepositoryPort,
            CrudPointHistoryInputPort crudPointHistoryInputPort,
            UserLearningProgressRepositoryPort userLearningProgressRepositoryPort,
            TransactionPort transactionPort,
            LearningPathNodeRepositoryPort learningPathNodeRepositoryPort,
            UserNodeProgressRepositoryPort userNodeProgressRepositoryPort
    ) {
        this.chestRepositoryPort = chestRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
        this.crudPointHistoryInputPort = crudPointHistoryInputPort;
        this.userLearningProgressRepositoryPort = userLearningProgressRepositoryPort;
        this.transactionPort = transactionPort;
        this.learningPathNodeRepositoryPort = learningPathNodeRepositoryPort;
        this.userNodeProgressRepositoryPort = userNodeProgressRepositoryPort;
    }

    @Override
    public void openChest(OpenChestCommand command) {
        transactionPort.execute(() -> doOpenChest(command));
    }

    private void doOpenChest(OpenChestCommand command) {
        Instant now = Instant.now();

        Chest chest = chestRepositoryPort.findById(command.chestId())
                .orElseThrow(() -> new ApplicationException(
                        ChestErrorCode.CHEST_NOT_FOUND,
                        ChestDetailMessageKey.CHEST_NOT_FOUND,
                        command.chestId()
                ));

        User user = userRepositoryPort.findById(command.userId())
                .orElseThrow(() -> new ApplicationException(
                        UserErrorCode.USER_NOT_FOUND,
                        UserDetailMessageKey.USER_ID_NOT_FOUND,
                        command.userId()
                ));

        LearningPathNode chestLearningPathNode = learningPathNodeRepositoryPort
                .findByChestId(chest.getId())
                .orElseThrow(() -> new ApplicationException(
                        LearningPathNodeErrorCode.LEARNING_PATH_NODE_NOT_FOUND,
                        LearningPathNodeDetailMessageKey.LEARNING_PATH_NODE_ID_NOT_FOUND
                ));

        if (userNodeProgressRepositoryPort
                .existsByLearningPathNodeId(chestLearningPathNode.getId())) {
            throw new ApplicationException(
                    ChestErrorCode.CHEST_ALREADY_OPENED,
                    ChestDetailMessageKey.CHEST_ALREADY_OPENED
            );
        }

        UserNodeProgress userNodeProgress = UserNodeProgress.builder()
                .learningPathNodeId(chestLearningPathNode.getId())
                .userId(user.getId())
                .bestScore(chest.getPoint())
                .currentScore(chest.getPoint())
                .attemptCount(1)
                .completedAt(now)
                .status(NodeStatus.COMPLETED)
                .build();

        userNodeProgressRepositoryPort.save(userNodeProgress);

        UserLearningProgress progress = userLearningProgressRepositoryPort
                .findByUserId(command.userId())
                .orElseThrow(() -> new ApplicationException(
                        UserLearningProgressErrorCode.USER_LEARNING_PROGRESS_NOT_FOUND,
                        UserLearningProgressDetailMessageKey.USER_LEARNING_PROGRESS_NOT_FOUND_BY_USER_ID,
                        command.userId()
                ));

        progress.addPoint(chest.getPoint());

        Long currentFarthestAvailableNodeId = progress.getFarthestAvailableNodeId();
        if (currentFarthestAvailableNodeId == null) {
            progress.setFarthestAvailableNodeId(chestLearningPathNode.getId());
        } else {
            LearningPathNode currentFarthestAvailableNode = learningPathNodeRepositoryPort
                    .findById(currentFarthestAvailableNodeId)
                    .orElseThrow(() -> new ApplicationException(
                            LearningPathNodeErrorCode.LEARNING_PATH_NODE_NOT_FOUND,
                            LearningPathNodeDetailMessageKey.LEARNING_PATH_NODE_ID_NOT_FOUND,
                            currentFarthestAvailableNodeId
                    ));
            if (currentFarthestAvailableNode.getGlobalOrderIndex() < chestLearningPathNode.getGlobalOrderIndex()) {
                progress.setFarthestAvailableNodeId(chestLearningPathNode.getId());
            }
        }

        progress.setLastLearningNodeId(chestLearningPathNode.getId());

        Integer currentStreak = progress.getCurrentStreak() + 1;

        progress.setCurrentStreak(currentStreak);

        if (progress.getLongestStreak() < currentStreak) {
            progress.setLongestStreak(currentStreak);
        }

        progress.setLastLearningAt(now);

        userLearningProgressRepositoryPort.save(progress);

        PointHistoryCommand pointHistoryCommand = PointHistoryCommand.builder()
                .userId(user.getId())
                .point(chest.getPoint())
                .transactionType(PointTransactionType.LEARNING_PATH_NODE_COMPLETION)
                .learningPathNodeId(chestLearningPathNode.getId())
                .build();

        crudPointHistoryInputPort.createPointHistory(pointHistoryCommand);
    }
}
