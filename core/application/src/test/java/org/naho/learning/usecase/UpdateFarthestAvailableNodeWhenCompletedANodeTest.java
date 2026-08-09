package org.naho.learning.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.i18n.message.learning.UserLearningProgressDetailMessageKey;
import org.naho.learning.command.UpdateFarthestAvailableNodeCommand;
import org.naho.learning.exception.UserLearningProgressErrorCode;
import org.naho.learning.mapper.UserLearningProgressResultMapper;
import org.naho.learning.model.LearningPathNode;
import org.naho.learning.model.UserLearningProgress;
import org.naho.learning.port.out.LearningPathNodeRepositoryPort;
import org.naho.learning.port.out.UserLearningProgressRepositoryPort;
import org.naho.shared.exception.ApplicationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateFarthestAvailableNodeWhenCompletedANodeTest {

    @Mock
    private UserLearningProgressRepositoryPort userLearningProgressRepositoryPort;

    @Mock
    private LearningPathNodeRepositoryPort learningPathNodeRepositoryPort;

    @Mock
    private UserLearningProgressResultMapper userLearningProgressResultMapper;

    @InjectMocks
    private CrudUserLearningProgressUseCase crudUserLearningProgressUseCase;

    @Test
    @DisplayName("UTCID01 - Cập nhật node xa nhất mở khóa thành công khi hoàn thành node hiện tại và có node kế tiếp")
    void UTCID01_UpdateFarthestAvailableNodeSuccess() {
        // Arrange
        UserLearningProgress progress = mock(UserLearningProgress.class);
        LearningPathNode currentNode = mock(LearningPathNode.class);
        LearningPathNode nextNode = mock(LearningPathNode.class);

        when(progress.getFarthestAvailableNodeGlobalOrderIndex()).thenReturn(1.0);
        when(currentNode.getGlobalOrderIndex()).thenReturn(1.0);

        when(nextNode.getId()).thenReturn(20L);
        when(nextNode.getGlobalOrderIndex()).thenReturn(2.0);

        when(learningPathNodeRepositoryPort.findTopByGlobalOrderIndexGreaterThanOrderByGlobalOrderIndex(1.0))
                .thenReturn(Optional.of(nextNode));

        UserLearningProgress updatedProgress = mock(UserLearningProgress.class);
        when(userLearningProgressRepositoryPort.save(progress)).thenReturn(updatedProgress);

        UpdateFarthestAvailableNodeCommand command = new UpdateFarthestAvailableNodeCommand(progress, currentNode);

        // Act
        UserLearningProgress result = crudUserLearningProgressUseCase.updateFarthestAvailableNodeWhenCompletedANode(command);

        // Assert
        assertNotNull(result);
        assertEquals(updatedProgress, result);
        verify(progress, times(1)).setFarthestAvailableNodeId(20L);
        verify(progress, times(1)).setFarthestAvailableNodeGlobalOrderIndex(2.0);
        verify(userLearningProgressRepositoryPort, times(1)).save(progress);
    }

    @Test
    @DisplayName("UTCID02 - Trả về tiến trình giữ nguyên khi node xa nhất mở khóa đã hơn node vừa học xong")
    void UTCID02_AlreadyLearnedFartherNode() {
        // Arrange
        UserLearningProgress progress = mock(UserLearningProgress.class);
        LearningPathNode currentNode = mock(LearningPathNode.class);

        when(progress.getFarthestAvailableNodeGlobalOrderIndex()).thenReturn(3.0);
        when(currentNode.getGlobalOrderIndex()).thenReturn(1.0);

        UpdateFarthestAvailableNodeCommand command = new UpdateFarthestAvailableNodeCommand(progress, currentNode);

        // Act
        UserLearningProgress result = crudUserLearningProgressUseCase.updateFarthestAvailableNodeWhenCompletedANode(command);

        // Assert
        assertEquals(progress, result);
        verify(learningPathNodeRepositoryPort, never()).findTopByGlobalOrderIndexGreaterThanOrderByGlobalOrderIndex(anyDouble());
        verify(userLearningProgressRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("UTCID03 - Trả về tiến trình giữ nguyên khi đã hoàn thành node cuối cùng của hệ thống")
    void UTCID03_CompletedLastNode() {
        // Arrange
        UserLearningProgress progress = mock(UserLearningProgress.class);
        LearningPathNode currentNode = mock(LearningPathNode.class);

        when(progress.getFarthestAvailableNodeGlobalOrderIndex()).thenReturn(5.0);
        when(currentNode.getGlobalOrderIndex()).thenReturn(5.0);

        when(learningPathNodeRepositoryPort.findTopByGlobalOrderIndexGreaterThanOrderByGlobalOrderIndex(5.0))
                .thenReturn(Optional.empty());

        UpdateFarthestAvailableNodeCommand command = new UpdateFarthestAvailableNodeCommand(progress, currentNode);

        // Act
        UserLearningProgress result = crudUserLearningProgressUseCase.updateFarthestAvailableNodeWhenCompletedANode(command);

        // Assert
        assertEquals(progress, result);
        verify(learningPathNodeRepositoryPort, times(1)).findTopByGlobalOrderIndexGreaterThanOrderByGlobalOrderIndex(5.0);
        verify(userLearningProgressRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("UTCID04 - Thất bại khi trạng thái tiến trình không hợp lệ (node xa nhất lại nhỏ hơn node hiện tại)")
    void UTCID04_InvalidProgress() {
        // Arrange
        UserLearningProgress progress = mock(UserLearningProgress.class);
        LearningPathNode currentNode = mock(LearningPathNode.class);

        when(progress.getFarthestAvailableNodeGlobalOrderIndex()).thenReturn(1.0);
        when(currentNode.getGlobalOrderIndex()).thenReturn(3.0);

        UpdateFarthestAvailableNodeCommand command = new UpdateFarthestAvailableNodeCommand(progress, currentNode);

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> crudUserLearningProgressUseCase.updateFarthestAvailableNodeWhenCompletedANode(command)
        );

        assertEquals(UserLearningProgressErrorCode.USER_LEARNING_PROGRESS_INVALID, exception.getErrorCode());
        assertEquals(UserLearningProgressDetailMessageKey.USER_LEARNING_PROGRESS_INVALID, exception.getMessage());
        verify(userLearningProgressRepositoryPort, never()).save(any());
    }
}
