package org.naho.learning.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.i18n.message.learning.LearningPathNodeDetailMessageKey;
import org.naho.i18n.message.learning.UserLearningProgressDetailMessageKey;
import org.naho.learning.exception.LearningPathNodeErrorCode;
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
class InitUserLearningProgressTest {

    @Mock
    private UserLearningProgressRepositoryPort userLearningProgressRepositoryPort;

    @Mock
    private LearningPathNodeRepositoryPort learningPathNodeRepositoryPort;

    @Mock
    private UserLearningProgressResultMapper userLearningProgressResultMapper;

    @InjectMocks
    private CrudUserLearningProgressUseCase crudUserLearningProgressUseCase;

    @Test
    @DisplayName("UTCID01 - Khởi tạo tiến trình học thành công khi người dùng chưa có tiến trình và có node đầu tiên")
    void UTCID01_InitUserLearningProgressSuccess() {
        // Arrange
        Long userId = 1L;
        LearningPathNode firstNode = mock(LearningPathNode.class);
        when(firstNode.getId()).thenReturn(10L);
        when(firstNode.getGlobalOrderIndex()).thenReturn(1.0);

        UserLearningProgress savedProgress = mock(UserLearningProgress.class);

        when(userLearningProgressRepositoryPort.existsByUserId(userId)).thenReturn(false);
        when(learningPathNodeRepositoryPort.findFirstLearningPathNode()).thenReturn(Optional.of(firstNode));
        when(userLearningProgressRepositoryPort.createNew(any(UserLearningProgress.class), eq(userId)))
                .thenReturn(savedProgress);

        // Act
        crudUserLearningProgressUseCase.initUserLearningProgress(userId);

        // Assert
        verify(userLearningProgressRepositoryPort, times(1)).existsByUserId(userId);
        verify(learningPathNodeRepositoryPort, times(1)).findFirstLearningPathNode();
        verify(userLearningProgressRepositoryPort, times(1)).createNew(any(UserLearningProgress.class), eq(userId));
        verify(userLearningProgressResultMapper, times(1)).domainToResult(savedProgress);
    }

    @Test
    @DisplayName("UTCID02 - Khởi tạo tiến trình học thất bại khi tiến trình học đã tồn tại cho userId")
    void UTCID02_UserLearningProgressAlreadyExists() {
        // Arrange
        Long userId = 1L;
        when(userLearningProgressRepositoryPort.existsByUserId(userId)).thenReturn(true);

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> crudUserLearningProgressUseCase.initUserLearningProgress(userId)
        );

        assertEquals(UserLearningProgressErrorCode.USER_LEARNING_PROGRESS_ALREADY_EXISTS, exception.getErrorCode());
        assertEquals(UserLearningProgressDetailMessageKey.USER_LEARNING_PROGRESS_ALREADY_EXISTS_BY_USER_ID, exception.getMessage());
        verify(userLearningProgressRepositoryPort, times(1)).existsByUserId(userId);
        verify(learningPathNodeRepositoryPort, never()).findFirstLearningPathNode();
        verify(userLearningProgressRepositoryPort, never()).createNew(any(), any());
    }

    @Test
    @DisplayName("UTCID03 - Khởi tạo tiến trình học thất bại khi hệ thống chưa có node đầu tiên nào")
    void UTCID03_LearningPathNodeNotFound() {
        // Arrange
        Long userId = 1L;
        when(userLearningProgressRepositoryPort.existsByUserId(userId)).thenReturn(false);
        when(learningPathNodeRepositoryPort.findFirstLearningPathNode()).thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> crudUserLearningProgressUseCase.initUserLearningProgress(userId)
        );

        assertEquals(LearningPathNodeErrorCode.LEARNING_PATH_NODE_NOT_FOUND, exception.getErrorCode());
        assertEquals(LearningPathNodeDetailMessageKey.LEARNING_PATH_NODE_ID_NOT_FOUND, exception.getMessage());
        verify(userLearningProgressRepositoryPort, times(1)).existsByUserId(userId);
        verify(learningPathNodeRepositoryPort, times(1)).findFirstLearningPathNode();
        verify(userLearningProgressRepositoryPort, never()).createNew(any(), any());
    }
}
