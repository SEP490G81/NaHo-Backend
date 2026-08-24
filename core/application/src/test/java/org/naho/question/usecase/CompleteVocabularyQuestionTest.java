package org.naho.question.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.learning.model.LearningPathNode;
import org.naho.learning.model.UserLearningProgress;
import org.naho.learning.model.UserNodeProgress;
import org.naho.learning.port.in.CrudUserLearningProgressInputPort;
import org.naho.learning.port.in.UserLearningStreakInputPort;
import org.naho.learning.port.out.LearningPathNodeRepositoryPort;
import org.naho.learning.port.out.UserLearningProgressRepositoryPort;
import org.naho.learning.port.out.UserNodeProgressRepositoryPort;
import org.naho.point.port.in.CrudPointHistoryInputPort;
import org.naho.question.command.CompleteVocabularyQuestionCommand;
import org.naho.shared.port.out.TransactionPort;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompleteVocabularyQuestionTest {

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
    private LearningPathNodeRepositoryPort learningPathNodeRepositoryPort;

    @InjectMocks
    private CompleteVocabularyQuestionUseCase completeVocabularyQuestionUseCase;

    @BeforeEach
    void setUp() {
        doAnswer(inv -> {
            Runnable r = inv.getArgument(0);
            r.run();
            return null;
        }).when(transactionPort).execute(any(Runnable.class));
    }

    @Test
    @DisplayName("UTCID01 - Hoàn thành câu hỏi từ vựng thành công")
    void UTCID01_CompleteVocabularyQuestion_Success() {
        CompleteVocabularyQuestionCommand command = new CompleteVocabularyQuestionCommand(10L, 1L);
        LearningPathNode node = mock(LearningPathNode.class);
        UserLearningProgress progress = mock(UserLearningProgress.class);

        when(node.getId()).thenReturn(100L);
        when(node.getGlobalOrderIndex()).thenReturn(1.0);
        when(progress.getFarthestAvailableNodeGlobalOrderIndex()).thenReturn(5.0);
        when(learningPathNodeRepositoryPort.findByVocabularyQuestionId(10L)).thenReturn(Optional.of(node));
        when(userLearningProgressRepositoryPort.findByUserId(1L)).thenReturn(Optional.of(progress));
        when(userNodeProgressRepositoryPort.findByLearningPathNodeIdAndUserId(100L, 1L)).thenReturn(Optional.empty());
        when(crudUserLearningProgressInputPort.updateFarthestAvailableNodeWhenCompletedANode(any())).thenReturn(progress);
        when(userLearningStreakInputPort.updateUserLearningStreak(any())).thenReturn(progress);

        completeVocabularyQuestionUseCase.completeVocabularyQuestion(command);

        verify(userNodeProgressRepositoryPort, times(1)).save(any(UserNodeProgress.class));
        verify(crudPointHistoryInputPort, times(1)).createPointHistory(any());
    }
}
