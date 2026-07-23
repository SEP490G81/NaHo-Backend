package org.naho.question.usecase;

import org.naho.i18n.message.learning.LearningPathNodeDetailMessageKey;
import org.naho.i18n.message.learning.UserLearningProgressDetailMessageKey;
import org.naho.learning.command.UpdateUserStreakCommand;
import org.naho.learning.exception.LearningPathNodeErrorCode;
import org.naho.learning.exception.UserLearningProgressErrorCode;
import org.naho.learning.model.LearningPathNode;
import org.naho.learning.model.UserLearningProgress;
import org.naho.learning.model.UserNodeProgress;
import org.naho.learning.port.in.UserLearningStreakInputPort;
import org.naho.learning.port.out.LearningPathNodeRepositoryPort;
import org.naho.learning.port.out.UserLearningProgressRepositoryPort;
import org.naho.learning.port.out.UserNodeProgressRepositoryPort;
import org.naho.learning.type.NodeStatus;
import org.naho.learning.usecase.UserLearningStreakUseCase;
import org.naho.point.command.PointHistoryCommand;
import org.naho.point.port.in.CrudPointHistoryInputPort;
import org.naho.point.type.PointTransactionType;
import org.naho.question.command.CompleteSpeakingQuestionCommand;
import org.naho.question.port.in.CompleteSpeakingQuestionInputPort;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;

import java.time.Instant;
import java.time.ZoneId;

public class CompleteSpeakingQuestionUseCase implements CompleteSpeakingQuestionInputPort {

    private final LearningPathNodeRepositoryPort learningPathNodeRepositoryPort;
    private final UserNodeProgressRepositoryPort userNodeProgressRepositoryPort;
    private final UserLearningProgressRepositoryPort userLearningProgressRepositoryPort;
    private final CrudPointHistoryInputPort crudPointHistoryInputPort;
    private final TransactionPort transactionPort;
    private final UserLearningStreakInputPort userLearningStreakInputPort;

    public CompleteSpeakingQuestionUseCase(
            LearningPathNodeRepositoryPort learningPathNodeRepositoryPort,
            UserNodeProgressRepositoryPort userNodeProgressRepositoryPort,
            UserLearningProgressRepositoryPort userLearningProgressRepositoryPort,
            CrudPointHistoryInputPort crudPointHistoryInputPort,
            TransactionPort transactionPort,
            UserLearningStreakInputPort userLearningStreakInputPort
    ) {
        this.learningPathNodeRepositoryPort = learningPathNodeRepositoryPort;
        this.userNodeProgressRepositoryPort = userNodeProgressRepositoryPort;
        this.userLearningProgressRepositoryPort = userLearningProgressRepositoryPort;
        this.crudPointHistoryInputPort = crudPointHistoryInputPort;
        this.transactionPort = transactionPort;
        this.userLearningStreakInputPort = userLearningStreakInputPort;
    }

    @Override
    public void completeSpeakingQuestion(CompleteSpeakingQuestionCommand command) {
        transactionPort.execute(() -> doCompleteSpeakingQuestion(command));
    }

    public void doCompleteSpeakingQuestion(CompleteSpeakingQuestionCommand command) {
        Instant now = Instant.now();

        LearningPathNode speakingQuestionLearningPathNode = learningPathNodeRepositoryPort
                .findBySpeakingQuestionId(command.speakingQuestionId())
                .orElseThrow(() -> new ApplicationException(
                        LearningPathNodeErrorCode.LEARNING_PATH_NODE_NOT_FOUND,
                        LearningPathNodeDetailMessageKey.LEARNING_PATH_NODE_ID_NOT_FOUND,
                        command.speakingQuestionId()
                ));

        UserLearningProgress progress = userLearningProgressRepositoryPort
                .findByUserId(command.userId())
                .orElseThrow(() -> new ApplicationException(
                        UserLearningProgressErrorCode.USER_LEARNING_PROGRESS_NOT_FOUND,
                        UserLearningProgressDetailMessageKey.USER_LEARNING_PROGRESS_NOT_FOUND_BY_USER_ID,
                        command.userId()
                ));

        UserNodeProgress currentUserNodeProgress = userNodeProgressRepositoryPort
                .findByLearningPathNodeIdAndUserId(
                        speakingQuestionLearningPathNode.getId(),
                        command.userId()
                )
                .orElse(null);


        Double point = null;

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
            }

            currentUserNodeProgress.setCurrentScore(command.overallScore());
            currentUserNodeProgress.increaseAttemptCount();
            currentUserNodeProgress.setCompletedAt(now);

            userNodeProgressRepositoryPort.save(currentUserNodeProgress);
        }

        // nếu node này xa hơn node xa nhất hiện tại mà người dùng đã học thì cập nhật
        Long currentFarthestAvailableNodeId = progress.getFarthestAvailableNodeId();
        if (currentFarthestAvailableNodeId == null) {
            progress.setFarthestAvailableNodeId(speakingQuestionLearningPathNode.getId());
            progress.setFarthestAvailableNodeGlobalOrderIndex(speakingQuestionLearningPathNode.getGlobalOrderIndex());
        } else {
            LearningPathNode currentFarthestAvailableNode = learningPathNodeRepositoryPort
                    .findById(currentFarthestAvailableNodeId)
                    .orElseThrow(() -> new ApplicationException(
                            LearningPathNodeErrorCode.LEARNING_PATH_NODE_NOT_FOUND,
                            LearningPathNodeDetailMessageKey.LEARNING_PATH_NODE_ID_NOT_FOUND,
                            currentFarthestAvailableNodeId
                    ));
            if (currentFarthestAvailableNode.getGlobalOrderIndex() <
                    speakingQuestionLearningPathNode.getGlobalOrderIndex()) {
                progress.setFarthestAvailableNodeId(speakingQuestionLearningPathNode.getId());
                progress.setFarthestAvailableNodeGlobalOrderIndex(speakingQuestionLearningPathNode.getGlobalOrderIndex());
            }
        }

        progress.setLastLearningNodeId(speakingQuestionLearningPathNode.getId());
        progress.setLastLearningNodeGlobalOrderIndex(speakingQuestionLearningPathNode.getGlobalOrderIndex());

        // chỉnh lại streak của người dùng
        progress = userLearningStreakInputPort.updateUserLearningStreak(
                UpdateUserStreakCommand.builder()
                        .userLearningProgress(progress)
                        .userId(command.userId())
                        .now(now)
                        .zoneId(ZoneId.of(UserLearningStreakUseCase.HO_CHI_MINH_ZONE_ID))
                        .build()
        );

        userLearningProgressRepositoryPort.save(progress);

        // nếu có sự thay đổi điểm thì tạo lịch sử điểm
        if (point != null) {
            PointHistoryCommand pointHistoryCommand = PointHistoryCommand.builder()
                    .userId(command.userId())
                    .point(point)
                    .transactionType(PointTransactionType.LEARNING_PATH_NODE_COMPLETION)
                    .learningPathNodeId(speakingQuestionLearningPathNode.getId())
                    .build();

            crudPointHistoryInputPort.createPointHistory(pointHistoryCommand);
        }
    }
}
