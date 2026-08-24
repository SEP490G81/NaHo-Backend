package org.naho.question.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.daily.port.in.CrudUserDailyMissionInputPort;
import org.naho.learning.model.LearningPathNode;
import org.naho.learning.model.UserLearningProgress;
import org.naho.learning.model.UserNodeProgress;
import org.naho.learning.port.in.CrudUserLearningProgressInputPort;
import org.naho.learning.port.in.UserLearningStreakInputPort;
import org.naho.learning.port.out.UserLearningProgressRepositoryPort;
import org.naho.learning.port.out.UserNodeProgressRepositoryPort;
import org.naho.point.port.in.CrudPointHistoryInputPort;
import org.naho.question.command.CompleteSpeakingQuestionCommand;
import org.naho.shared.port.out.TransactionPort;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompleteSpeakingQuestionTest {

    @Mock
    private UserNodeProgressRepositoryPort userNodeProgressRepositoryPort;
    @Mock
    private UserLearningProgressRepositoryPort userLearningProgressRepositoryPort;
    @Mock
    private CrudPointHistoryInputPort crudPointHistoryInputPort;
    @Mock
    private TransactionPort transactionPort;
    @Mock
    private UserLearningStreakInputPort userLearningStreakInputPort;
    @Mock
    private CrudUserLearningProgressInputPort crudUserLearningProgressInputPort;
    @Mock
    private CrudUserDailyMissionInputPort crudUserDailyMissionInputPort;

    @InjectMocks
    private CompleteSpeakingQuestionUseCase completeSpeakingQuestionUseCase;

    @BeforeEach
    void setUp() {
        doAnswer(inv -> {
            Runnable r = inv.getArgument(0);
            r.run();
            return null;
        }).when(transactionPort).execute(any(Runnable.class));
    }

    @Test
    @DisplayName("UTCID01 - Hoàn thành câu hỏi nói lần đầu thành công với điểm đạt")
    void UTCID01_CompleteSpeakingQuestion_FirstAttempt_Pass() {
        LearningPathNode node = mock(LearningPathNode.class);
        UserLearningProgress progress = mock(UserLearningProgress.class);
        CompleteSpeakingQuestionCommand command = new CompleteSpeakingQuestionCommand(progress, node, 1L, 85.0);

        when(node.getId()).thenReturn(10L);
        when(node.getGlobalOrderIndex()).thenReturn(1.0);
        when(userNodeProgressRepositoryPort.findByLearningPathNodeIdAndUserId(10L, 1L)).thenReturn(Optional.empty());
        when(crudUserLearningProgressInputPort.updateFarthestAvailableNodeWhenCompletedANode(any())).thenReturn(progress);
        when(progress.addPoint(85.0)).thenReturn(85.0);
        when(userLearningStreakInputPort.updateUserLearningStreak(any())).thenReturn(progress);

        completeSpeakingQuestionUseCase.completeSpeakingQuestion(command);

        verify(userNodeProgressRepositoryPort, times(1)).save(any(UserNodeProgress.class));
        verify(userLearningProgressRepositoryPort, times(1)).save(progress);
        verify(crudUserDailyMissionInputPort, times(1)).completeMission(any());
    }
}
