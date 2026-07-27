package org.naho.question.usecase;

import org.naho.daily.command.CompleteMissionCommand;
import org.naho.daily.port.in.CrudDailyMissionInputPort;
import org.naho.daily.type.MissionType;
import org.naho.learning.command.UpdateFarthestAvailableNodeCommand;
import org.naho.learning.command.UpdateUserStreakCommand;
import org.naho.learning.model.LearningPathNode;
import org.naho.learning.model.UserLearningProgress;
import org.naho.learning.model.UserNodeProgress;
import org.naho.learning.port.in.CrudUserLearningProgressInputPort;
import org.naho.learning.port.in.UserLearningStreakInputPort;
import org.naho.learning.port.out.UserLearningProgressRepositoryPort;
import org.naho.learning.port.out.UserNodeProgressRepositoryPort;
import org.naho.learning.type.NodeStatus;
import org.naho.point.command.PointHistoryCommand;
import org.naho.point.port.in.CrudPointHistoryInputPort;
import org.naho.point.type.PointTransactionType;
import org.naho.question.command.CompleteSpeakingQuestionCommand;
import org.naho.question.port.in.CompleteSpeakingQuestionInputPort;
import org.naho.shared.constant.SystemZoneId;
import org.naho.shared.port.out.TransactionPort;

import java.time.Instant;

public class CompleteSpeakingQuestionUseCase implements CompleteSpeakingQuestionInputPort {

    private final UserNodeProgressRepositoryPort userNodeProgressRepositoryPort;
    private final UserLearningProgressRepositoryPort userLearningProgressRepositoryPort;
    private final CrudPointHistoryInputPort crudPointHistoryInputPort;
    private final TransactionPort transactionPort;
    private final UserLearningStreakInputPort userLearningStreakInputPort;
    private final CrudUserLearningProgressInputPort crudUserLearningProgressInputPort;
    private final CrudDailyMissionInputPort crudDailyMissionInputPort;

    public CompleteSpeakingQuestionUseCase(
            UserNodeProgressRepositoryPort userNodeProgressRepositoryPort,
            UserLearningProgressRepositoryPort userLearningProgressRepositoryPort,
            CrudPointHistoryInputPort crudPointHistoryInputPort,
            TransactionPort transactionPort,
            UserLearningStreakInputPort userLearningStreakInputPort,
            CrudUserLearningProgressInputPort crudUserLearningProgressInputPort,
            CrudDailyMissionInputPort crudDailyMissionInputPort
    ) {
        this.userNodeProgressRepositoryPort = userNodeProgressRepositoryPort;
        this.userLearningProgressRepositoryPort = userLearningProgressRepositoryPort;
        this.crudPointHistoryInputPort = crudPointHistoryInputPort;
        this.transactionPort = transactionPort;
        this.userLearningStreakInputPort = userLearningStreakInputPort;
        this.crudUserLearningProgressInputPort = crudUserLearningProgressInputPort;
        this.crudDailyMissionInputPort = crudDailyMissionInputPort;
    }

    @Override
    public void completeSpeakingQuestion(CompleteSpeakingQuestionCommand command) {
        transactionPort.execute(() -> doCompleteSpeakingQuestion(command));
    }

    public void doCompleteSpeakingQuestion(CompleteSpeakingQuestionCommand command) {
        Instant now = Instant.now();

        LearningPathNode speakingQuestionLearningPathNode =
                command.speakingQuestionLearningPathNode();

        UserLearningProgress progress = command.userLearningProgress();

        UserNodeProgress currentUserNodeProgress = userNodeProgressRepositoryPort
                .findByLearningPathNodeIdAndUserId(
                        speakingQuestionLearningPathNode.getId(),
                        command.userId()
                )
                .orElse(null);

        Double point = null;

        PointTransactionType pointTransactionType = PointTransactionType.LEARNING_PATH_NODE_COMPLETION;
        // nếu người dùng chưa từng học node này, tạo mới
        if (currentUserNodeProgress == null) {
            UserNodeProgress newUserNodeProgress = UserNodeProgress
                    .builder()
                    .learningPathNodeId(speakingQuestionLearningPathNode.getId())
                    .userId(command.userId())
                    .bestScore(command.overallScore())
                    .currentScore(command.overallScore())
                    .attemptCount(1)
                    .completedAt(now)
                    .status(NodeStatus.COMPLETED)
                    .build();

            userNodeProgressRepositoryPort.save(newUserNodeProgress);

            point = progress.addPoint(command.overallScore());

        } else {
            // nếu đã từng học thì sẽ xem điểm có cao hơn không
            // nếu có thì mới cộng điểm = số chênh lệch
            double differentScore = command.overallScore() - currentUserNodeProgress.getBestScore();

            if (differentScore > 0) {
                currentUserNodeProgress.setBestScore(command.overallScore());
                point = progress.addPoint(differentScore);
                pointTransactionType = PointTransactionType.LEARNING_PATH_NODE_RETAKE;
            }

            currentUserNodeProgress.setCurrentScore(command.overallScore());
            currentUserNodeProgress.increaseAttemptCount();
            currentUserNodeProgress.setCompletedAt(now);

            userNodeProgressRepositoryPort.save(currentUserNodeProgress);
        }

        // cập nhật node xa nhất mà người dùng có thể học sau khi học xong node này
        progress = crudUserLearningProgressInputPort.updateFarthestAvailableNodeWhenCompletedANode(
                new UpdateFarthestAvailableNodeCommand(
                        progress,
                        speakingQuestionLearningPathNode
                )
        );

        // update node cuối cùng mà người dùng học
        progress.setLastLearningNodeId(speakingQuestionLearningPathNode.getId());
        progress.setLastLearningNodeGlobalOrderIndex(speakingQuestionLearningPathNode.getGlobalOrderIndex());

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

        // nếu có sự thay đổi điểm thì tạo lịch sử nhận điểm
        if (point != null) {
            PointHistoryCommand pointHistoryCommand = PointHistoryCommand.builder()
                    .userId(command.userId())
                    .point(point)
                    .transactionType(pointTransactionType)
                    .learningPathNodeId(speakingQuestionLearningPathNode.getId())
                    .build();

            crudPointHistoryInputPort.createPointHistory(pointHistoryCommand);
        }

        crudDailyMissionInputPort.completeMission(new CompleteMissionCommand(
                MissionType.COMPLETE_SPEAKING_QUESTION_NODE,
                command.userId()
        ));
    }
}
