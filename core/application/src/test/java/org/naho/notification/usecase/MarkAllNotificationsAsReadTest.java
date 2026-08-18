package org.naho.notification.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.notification.port.out.NotificationRepositoryPort;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarkAllNotificationsAsReadTest {

    @Mock
    private NotificationRepositoryPort notificationRepositoryPort;

    @InjectMocks
    private MarkAllNotificationsAsReadUseCase markAllNotificationsAsReadUseCase;

    @Test
    @DisplayName("UTCID01 - Đánh dấu tất cả thông báo là đã đọc thành công")
    void UTCID01_MarkAllAsRead_Success() {
        // Arrange
        Long userId = 100L;
        doNothing().when(notificationRepositoryPort).markAllAsReadByUserId(userId);

        // Act
        assertDoesNotThrow(() -> markAllNotificationsAsReadUseCase.markAllAsRead(userId));

        // Assert
        verify(notificationRepositoryPort, times(1)).markAllAsReadByUserId(userId);
    }

    @Test
    @DisplayName("UTCID02 - Đánh dấu tất cả thông báo khi userId là null")
    void UTCID02_MarkAllAsRead_NullUserId() {
        // Arrange
        doNothing().when(notificationRepositoryPort).markAllAsReadByUserId(null);

        // Act
        assertDoesNotThrow(() -> markAllNotificationsAsReadUseCase.markAllAsRead(null));

        // Assert
        verify(notificationRepositoryPort, times(1)).markAllAsReadByUserId(null);
    }
}
