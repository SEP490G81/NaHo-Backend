package org.naho.learning.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.i18n.message.learning.UserLearningProgressDetailMessageKey;
import org.naho.learning.exception.UserLearningProgressErrorCode;
import org.naho.learning.mapper.UserLearningProgressResultMapper;
import org.naho.learning.model.UserLearningProgress;
import org.naho.learning.port.out.LearningPathNodeRepositoryPort;
import org.naho.learning.port.out.UserLearningProgressRepositoryPort;
import org.naho.learning.result.UserLearningProgressResult;
import org.naho.shared.exception.ApplicationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindUserLearningProgressByUserIdTest {

    @Mock
    private UserLearningProgressRepositoryPort userLearningProgressRepositoryPort;

    @Mock
    private LearningPathNodeRepositoryPort learningPathNodeRepositoryPort;

    @Mock
    private UserLearningProgressResultMapper userLearningProgressResultMapper;

    @InjectMocks
    private CrudUserLearningProgressUseCase crudUserLearningProgressUseCase;

    @Test
    @DisplayName("UTCID01 - Tìm tiến trình học của người dùng thành công khi userId hợp lệ và tiến trình tồn tại")
    void UTCID01_FindUserLearningProgressByUserIdSuccess() {
        // Arrange
        Long userId = 1L;
        UserLearningProgress progress = mock(UserLearningProgress.class);
        UserLearningProgressResult expectedResult = mock(UserLearningProgressResult.class);

        when(userLearningProgressRepositoryPort.findByUserId(userId))
                .thenReturn(Optional.of(progress));
        when(userLearningProgressResultMapper.domainToDetailsResult(progress, userId))
                .thenReturn(expectedResult);

        // Act
        UserLearningProgressResult result = crudUserLearningProgressUseCase.findUserLearningProgressByUserId(userId);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResult, result);
        verify(userLearningProgressRepositoryPort, times(1)).findByUserId(userId);
        verify(userLearningProgressResultMapper, times(1)).domainToDetailsResult(progress, userId);
    }

    @Test
    @DisplayName("UTCID02 - Tìm tiến trình học thất bại khi không tìm thấy tiến trình với userId tương ứng")
    void UTCID02_UserLearningProgressNotFound() {
        // Arrange
        Long userId = 99L;

        when(userLearningProgressRepositoryPort.findByUserId(userId))
                .thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> crudUserLearningProgressUseCase.findUserLearningProgressByUserId(userId)
        );

        assertEquals(UserLearningProgressErrorCode.USER_LEARNING_PROGRESS_NOT_FOUND, exception.getErrorCode());
        assertEquals(UserLearningProgressDetailMessageKey.USER_LEARNING_PROGRESS_NOT_FOUND_BY_USER_ID, exception.getMessage());
        verify(userLearningProgressRepositoryPort, times(1)).findByUserId(userId);
        verify(userLearningProgressResultMapper, never()).domainToDetailsResult(any(), any());
    }
}
