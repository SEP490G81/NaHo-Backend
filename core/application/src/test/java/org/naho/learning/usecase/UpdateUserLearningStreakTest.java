package org.naho.learning.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.learning.command.UpdateUserStreakCommand;
import org.naho.learning.model.UserLearningProgress;
import org.naho.shared.exception.ApplicationException;
import org.naho.user.exception.UserErrorCode;

import java.time.Instant;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateUserLearningStreakTest {

    @InjectMocks
    private UserLearningStreakUseCase userLearningStreakUseCase;

    @Test
    @DisplayName("UTCID01 - Cập nhật streak thành công khi người dùng học lần đầu tiên")
    void UTCID01_FirstTimeLearning() {
        // Arrange
        Long userId = 1L;
        Instant now = Instant.parse("2026-08-08T10:00:00Z");
        ZoneId zoneId = ZoneId.of("UTC");

        UserLearningProgress progress = mock(UserLearningProgress.class);
        when(progress.getLastLearningAt()).thenReturn(null);

        UpdateUserStreakCommand command = UpdateUserStreakCommand.builder()
                .userId(userId)
                .userLearningProgress(progress)
                .now(now)
                .zoneId(zoneId)
                .build();

        // Act
        UserLearningProgress result = userLearningStreakUseCase.updateUserLearningStreak(command);

        // Assert
        assertEquals(progress, result);
        verify(progress, times(1)).setCurrentStreak(1);
        verify(progress, times(1)).setLongestStreak(1);
        verify(progress, times(1)).setLastLearningAt(now);
    }

    @Test
    @DisplayName("UTCID02 - Cập nhật streak thành công khi người dùng học lại trong cùng một ngày")
    void UTCID02_LearnedTodaySameDay() {
        // Arrange
        Long userId = 1L;
        Instant now = Instant.parse("2026-08-08T15:00:00Z");
        Instant lastLearningAt = Instant.parse("2026-08-08T08:00:00Z");
        ZoneId zoneId = ZoneId.of("UTC");

        UserLearningProgress progress = mock(UserLearningProgress.class);
        when(progress.getLastLearningAt()).thenReturn(lastLearningAt);

        UpdateUserStreakCommand command = UpdateUserStreakCommand.builder()
                .userId(userId)
                .userLearningProgress(progress)
                .now(now)
                .zoneId(zoneId)
                .build();

        // Act
        UserLearningProgress result = userLearningStreakUseCase.updateUserLearningStreak(command);

        // Assert
        assertEquals(progress, result);
        verify(progress, times(1)).setLastLearningAt(now);
        verify(progress, never()).setCurrentStreak(anyInt());
    }

    @Test
    @DisplayName("UTCID03 - Cập nhật streak thành công khi người dùng học vào ngày kế tiếp liên tiếp")
    void UTCID03_LearnedConsecutiveDays() {
        // Arrange
        Long userId = 1L;
        Instant now = Instant.parse("2026-08-09T10:00:00Z");
        Instant lastLearningAt = Instant.parse("2026-08-08T10:00:00Z");
        ZoneId zoneId = ZoneId.of("UTC");

        UserLearningProgress progress = mock(UserLearningProgress.class);
        when(progress.getLastLearningAt()).thenReturn(lastLearningAt);
        when(progress.getCurrentStreak()).thenReturn(2);
        when(progress.getLongestStreak()).thenReturn(5);

        UpdateUserStreakCommand command = UpdateUserStreakCommand.builder()
                .userId(userId)
                .userLearningProgress(progress)
                .now(now)
                .zoneId(zoneId)
                .build();

        // Act
        UserLearningProgress result = userLearningStreakUseCase.updateUserLearningStreak(command);

        // Assert
        assertEquals(progress, result);
        verify(progress, times(1)).setCurrentStreak(3);
        verify(progress, times(1)).setLongestStreak(5); // Math.max(5, 3) = 5
        verify(progress, times(1)).setLastLearningAt(now);
    }

    @Test
    @DisplayName("UTCID04 - Cập nhật streak thành công khi người dùng bỏ lỡ ngày và bị đứt chuỗi học")
    void UTCID04_StreakBroken() {
        // Arrange
        Long userId = 1L;
        Instant now = Instant.parse("2026-08-11T10:00:00Z"); // Bỏ lỡ ngày 9, 10
        Instant lastLearningAt = Instant.parse("2026-08-08T10:00:00Z");
        ZoneId zoneId = ZoneId.of("UTC");

        UserLearningProgress progress = mock(UserLearningProgress.class);
        when(progress.getLastLearningAt()).thenReturn(lastLearningAt);
        when(progress.getCurrentStreak()).thenReturn(10);
        when(progress.getLongestStreak()).thenReturn(15);

        UpdateUserStreakCommand command = UpdateUserStreakCommand.builder()
                .userId(userId)
                .userLearningProgress(progress)
                .now(now)
                .zoneId(zoneId)
                .build();

        // Act
        UserLearningProgress result = userLearningStreakUseCase.updateUserLearningStreak(command);

        // Assert
        assertEquals(progress, result);
        verify(progress, times(1)).setCurrentStreak(0);
        verify(progress, times(1)).setLongestStreak(15); // Math.max(15, 0) = 15
        verify(progress, times(1)).setLastLearningAt(now);
    }

    @Test
    @DisplayName("UTCID05 - Cập nhật streak thất bại khi userId bị null")
    void UTCID05_UserIdNull() {
        // Arrange
        Long userId = null;
        UserLearningProgress progress = mock(UserLearningProgress.class);

        UpdateUserStreakCommand command = UpdateUserStreakCommand.builder()
                .userId(userId)
                .userLearningProgress(progress)
                .build();

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> userLearningStreakUseCase.updateUserLearningStreak(command)
        );

        assertEquals(UserErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_ID_NULL, exception.getMessage());
        verify(progress, never()).getLastLearningAt();
    }
}
