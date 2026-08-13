package org.naho.chest.usecase;

import org.naho.chest.command.OpenChestCommand;
import org.naho.chest.exception.ChestErrorCode;
import org.naho.chest.model.Chest;
import org.naho.chest.port.in.OpenChestInputPort;
import org.naho.chest.port.out.ChestRepositoryPort;
import org.naho.chest.result.OpenChestResult;
import org.naho.i18n.message.chest.ChestDetailMessageKey;
import org.naho.i18n.message.learning.LearningPathNodeDetailMessageKey;
import org.naho.i18n.message.learning.UserLearningProgressDetailMessageKey;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.learning.command.UpdateFarthestAvailableNodeCommand;
import org.naho.learning.command.UpdateUserStreakCommand;
import org.naho.learning.exception.LearningPathNodeErrorCode;
import org.naho.learning.exception.UserLearningProgressErrorCode;
import org.naho.learning.model.LearningPathNode;
import org.naho.learning.model.UserLearningProgress;
import org.naho.learning.model.UserNodeProgress;
import org.naho.learning.port.in.CrudUserLearningProgressInputPort;
import org.naho.learning.port.in.UserLearningStreakInputPort;
import org.naho.learning.port.out.LearningPathNodeRepositoryPort;
import org.naho.learning.port.out.UserLearningProgressRepositoryPort;
import org.naho.learning.port.out.UserNodeProgressRepositoryPort;
import org.naho.learning.type.NodeStatus;
import org.naho.learning.type.NodeType;
import org.naho.point.command.PointHistoryCommand;
import org.naho.point.port.in.CrudPointHistoryInputPort;
import org.naho.point.type.PointTransactionType;
import org.naho.shared.constant.SystemZoneId;
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
    private final UserLearningStreakInputPort userLearningStreakInputPort;
    private final CrudUserLearningProgressInputPort crudUserLearningProgressInputPort;

    public OpenChestUseCase(
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
        this.chestRepositoryPort = chestRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
        this.crudPointHistoryInputPort = crudPointHistoryInputPort;
        this.userLearningProgressRepositoryPort = userLearningProgressRepositoryPort;
        this.transactionPort = transactionPort;
        this.learningPathNodeRepositoryPort = learningPathNodeRepositoryPort;
        this.userNodeProgressRepositoryPort = userNodeProgressRepositoryPort;
        this.userLearningStreakInputPort = userLearningStreakInputPort;
        this.crudUserLearningProgressInputPort = crudUserLearningProgressInputPort;
    }

    @Override
    public OpenChestResult openChest(OpenChestCommand command) {
        return transactionPort.execute(() -> doOpenChest(command));
    }

    private OpenChestResult doOpenChest(OpenChestCommand command) {
        Instant now = Instant.now();

        Long learningPathNodeId = command.learningPathNodeId();

        LearningPathNode chestLearningPathNode = learningPathNodeRepositoryPort
                .findByIdAndNodeType(learningPathNodeId, NodeType.CHEST)
                .orElseThrow(() -> new ApplicationException(
                        LearningPathNodeErrorCode.LEARNING_PATH_NODE_NOT_FOUND,
                        LearningPathNodeDetailMessageKey.LEARNING_PATH_NODE_ID_NOT_FOUND,
                        learningPathNodeId
                ));

        // nếu người dùng đã từng mở rương này rồi thì không cho mở nữa
        if (userNodeProgressRepositoryPort.existsByLearningPathNodeId(
                chestLearningPathNode.getId()
        )) {
            throw new ApplicationException(
                    ChestErrorCode.CHEST_ALREADY_OPENED,
                    ChestDetailMessageKey.CHEST_ALREADY_OPENED
            );
        }

        // Lấy thông tin về thành tích học tập của người dùng
        UserLearningProgress progress = userLearningProgressRepositoryPort
                .findByUserId(command.userId())
                .orElseThrow(() -> new ApplicationException(
                        UserLearningProgressErrorCode.USER_LEARNING_PROGRESS_NOT_FOUND,
                        UserLearningProgressDetailMessageKey.USER_LEARNING_PROGRESS_NOT_FOUND_BY_USER_ID,
                        command.userId()
                ));

        // nếu người dùng chưa được phép học tới node này
        if (progress.getFarthestAvailableNodeGlobalOrderIndex() <
                chestLearningPathNode.getGlobalOrderIndex()) {
            throw new ApplicationException(
                    ChestErrorCode.CHEST_LOCKED,
                    ChestDetailMessageKey.CHEST_LOCKED
            );
        }

        Long chestId = chestLearningPathNode.getChestId();

        Chest chest = chestRepositoryPort.findById(chestId)
                .orElseThrow(() -> new ApplicationException(
                        ChestErrorCode.CHEST_NOT_FOUND,
                        ChestDetailMessageKey.CHEST_NOT_FOUND,
                        chestId
                ));

        User user = userRepositoryPort.findById(command.userId())
                .orElseThrow(() -> new ApplicationException(
                        UserErrorCode.USER_NOT_FOUND,
                        UserDetailMessageKey.USER_ID_NOT_FOUND,
                        command.userId()
                ));

        // nhận điểm ngẫu nhiên
        double earnedPoint = chest.getRandomPoint();

        // thêm điểm
        progress.addPoint(earnedPoint);

        // cập nhật node xa nhất mà người dùng có thể học sau khi học xong node này
        progress = crudUserLearningProgressInputPort.updateFarthestAvailableNodeWhenCompletedANode(
                new UpdateFarthestAvailableNodeCommand(
                        progress,
                        chestLearningPathNode
                )
        );

        // update node cuối cùng người dùng học
        progress.setLastLearningNodeId(chestLearningPathNode.getId());
        progress.setLastLearningNodeGlobalOrderIndex(chestLearningPathNode.getGlobalOrderIndex());

        // chỉnh lại streak của người dùng
        progress = userLearningStreakInputPort.updateUserLearningStreak(
                UpdateUserStreakCommand.builder()
                        .userLearningProgress(progress)
                        .userId(command.userId())
                        .now(now)
                        .zoneId(SystemZoneId.HO_CHI_MINH_ZONE_ID)
                        .build()
        );

        userLearningProgressRepositoryPort.save(progress);

        // tạo ra UserNodeProgress mới trên node này
        UserNodeProgress userNodeProgress = UserNodeProgress.builder()
                .learningPathNodeId(chestLearningPathNode.getId())
                .userId(user.getId())
                .bestScore(earnedPoint)
                .currentScore(earnedPoint)
                .attemptCount(1)
                .lastCompletedAt(now)
                .status(NodeStatus.PASSED)
                .build();

        userNodeProgressRepositoryPort.save(userNodeProgress);

        // Lưu lịch sử nhận điểm của người dùng
        PointHistoryCommand pointHistoryCommand = PointHistoryCommand.builder()
                .userId(user.getId())
                .point(earnedPoint)
                .transactionType(PointTransactionType.LEARNING_PATH_NODE_COMPLETION)
                .learningPathNodeId(chestLearningPathNode.getId())
                .build();

        crudPointHistoryInputPort.createPointHistory(pointHistoryCommand);

        return new OpenChestResult(earnedPoint);
    }
}
