package org.naho.question.usecase;

import org.naho.i18n.message.learning.LearningPathNodeDetailMessageKey;
import org.naho.i18n.message.learning.UserLearningProgressDetailMessageKey;
import org.naho.i18n.message.question.VocabularyQuestionDetailMessageKey;
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
import org.naho.point.command.PointHistoryCommand;
import org.naho.point.port.in.CrudPointHistoryInputPort;
import org.naho.point.type.PointTransactionType;
import org.naho.question.command.CompleteVocabularyQuestionCommand;
import org.naho.question.exception.VocabularyQuestionErrorCode;
import org.naho.question.port.in.CompleteVocabularyQuestionInputPort;
import org.naho.shared.constant.SystemZoneId;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;

import java.time.Instant;

public class CompleteVocabularyQuestionUseCase implements CompleteVocabularyQuestionInputPort {
    private static final Double DEFAULT_SCORE_OF_VOCABULARY_QUESTION = 10.0;

    private final UserNodeProgressRepositoryPort userNodeProgressRepositoryPort;
    private final UserLearningProgressRepositoryPort userLearningProgressRepositoryPort;
    private final CrudPointHistoryInputPort crudPointHistoryInputPort;
    private final TransactionPort transactionPort;
    private final UserLearningStreakInputPort userLearningStreakInputPort;
    private final CrudUserLearningProgressInputPort crudUserLearningProgressInputPort;
    private final LearningPathNodeRepositoryPort learningPathNodeRepositoryPort;

    public CompleteVocabularyQuestionUseCase(
            UserNodeProgressRepositoryPort userNodeProgressRepositoryPort,
            UserLearningProgressRepositoryPort userLearningProgressRepositoryPort,
            CrudPointHistoryInputPort crudPointHistoryInputPort,
            TransactionPort transactionPort,
            UserLearningStreakInputPort userLearningStreakInputPort,
            CrudUserLearningProgressInputPort crudUserLearningProgressInputPort,
            LearningPathNodeRepositoryPort learningPathNodeRepositoryPort
    ) {
        this.userNodeProgressRepositoryPort = userNodeProgressRepositoryPort;
        this.userLearningProgressRepositoryPort = userLearningProgressRepositoryPort;
        this.crudPointHistoryInputPort = crudPointHistoryInputPort;
        this.transactionPort = transactionPort;
        this.userLearningStreakInputPort = userLearningStreakInputPort;
        this.crudUserLearningProgressInputPort = crudUserLearningProgressInputPort;
        this.learningPathNodeRepositoryPort = learningPathNodeRepositoryPort;
    }

    @Override
    public void completeVocabularyQuestion(CompleteVocabularyQuestionCommand command) {
        transactionPort.execute(() -> doCompleteVocabularyQuestion(command));
    }

    private void doCompleteVocabularyQuestion(CompleteVocabularyQuestionCommand command) {
        Instant now = Instant.now();

        LearningPathNode vocabularyQuestionLearningPathNode = learningPathNodeRepositoryPort
                .findByVocabularyQuestionId(command.vocabularyQuestionId())
                .orElseThrow(() -> new ApplicationException(
                        LearningPathNodeErrorCode.LEARNING_PATH_NODE_NOT_FOUND,
                        LearningPathNodeDetailMessageKey.LEARNING_PATH_NODE_ID_NOT_FOUND,
                        command.vocabularyQuestionId()
                ));

        UserLearningProgress progress = userLearningProgressRepositoryPort
                .findByUserId(command.userId())
                .orElseThrow(() -> new ApplicationException(
                        UserLearningProgressErrorCode.USER_LEARNING_PROGRESS_NOT_FOUND,
                        UserLearningProgressDetailMessageKey.USER_LEARNING_PROGRESS_NOT_FOUND_BY_USER_ID,
                        command.userId()
                ));

        // nếu node xa nhất người dùng có thể học còn nhỏ hơn node đang định học
        if (progress.getFarthestAvailableNodeGlobalOrderIndex()
                < vocabularyQuestionLearningPathNode.getGlobalOrderIndex()) {
            throw new ApplicationException(
                    VocabularyQuestionErrorCode.VOCABULARY_QUESTION_LOCKED,
                    VocabularyQuestionDetailMessageKey.VOCABULARY_QUESTION_LOCKED
            );
        }

        UserNodeProgress currentUserNodeProgress = userNodeProgressRepositoryPort
                .findByLearningPathNodeIdAndUserId(
                        vocabularyQuestionLearningPathNode.getId(),
                        command.userId()
                )
                .orElse(null);

        // nếu người dùng đã học node này rồi thì thôi
        if (currentUserNodeProgress != null) {
            return;
        }

        // nếu chưa học thì tạo mới user node progress
        currentUserNodeProgress = UserNodeProgress
                .builder()
                .learningPathNodeId(vocabularyQuestionLearningPathNode.getId())
                .userId(command.userId())
                .bestScore(DEFAULT_SCORE_OF_VOCABULARY_QUESTION)
                .currentScore(DEFAULT_SCORE_OF_VOCABULARY_QUESTION)
                .completedAt(now)
                .status(NodeStatus.COMPLETED)
                .build();

        userNodeProgressRepositoryPort.save(currentUserNodeProgress);

        // cộng điểm cho người dùng vì học xong node này
        progress.addPoint(DEFAULT_SCORE_OF_VOCABULARY_QUESTION);

        // cập nhật node xa nhất mà người dùng có thể học sau khi học xong node này
        progress = crudUserLearningProgressInputPort.updateFarthestAvailableNodeWhenCompletedANode(
                new UpdateFarthestAvailableNodeCommand(
                        progress,
                        vocabularyQuestionLearningPathNode
                )
        );

        // update node cuối cùng mà người dùng học
        progress.setLastLearningNodeId(vocabularyQuestionLearningPathNode.getId());
        progress.setLastLearningNodeGlobalOrderIndex(vocabularyQuestionLearningPathNode.getGlobalOrderIndex());

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

        // tạo lịch sử nhận điểm mới
        PointHistoryCommand pointHistoryCommand = PointHistoryCommand.builder()
                .userId(command.userId())
                .point(DEFAULT_SCORE_OF_VOCABULARY_QUESTION)
                .transactionType(PointTransactionType.LEARNING_PATH_NODE_COMPLETION)
                .learningPathNodeId(vocabularyQuestionLearningPathNode.getId())
                .build();

        crudPointHistoryInputPort.createPointHistory(pointHistoryCommand);
    }
}
