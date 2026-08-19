package org.naho.notification.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.notification.port.out.NotificationRepositoryPort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CountUnreadNotificationTest {

    @Mock
    private NotificationRepositoryPort notificationRepositoryPort;

    @InjectMocks
    private CountUnreadNotificationUseCase countUnreadNotificationUseCase;

    @Test
    @DisplayName("UTCID01 - Đếm số lượng thông báo chưa đọc thành công khi người dùng có thông báo chưa đọc")
    void UTCID01_CountUnreadByUserId_Success() {
        // Arrange
        Long userId = 100L;
        when(notificationRepositoryPort.countUnreadByUserId(userId)).thenReturn(5L);

        // Act
        long result = countUnreadNotificationUseCase.countUnreadByUserId(userId);

        // Assert
        assertEquals(5L, result);
        verify(notificationRepositoryPort, times(1)).countUnreadByUserId(userId);
    }

    @Test
    @DisplayName("UTCID02 - Đếm số lượng thông báo chưa đọc trả về 0 khi không có thông báo chưa đọc")
    void UTCID02_CountUnreadByUserId_ZeroUnread() {
        // Arrange
        Long userId = 100L;
        when(notificationRepositoryPort.countUnreadByUserId(userId)).thenReturn(0L);

        // Act
        long result = countUnreadNotificationUseCase.countUnreadByUserId(userId);

        // Assert
        assertEquals(0L, result);
        verify(notificationRepositoryPort, times(1)).countUnreadByUserId(userId);
    }

    @Test
    @DisplayName("UTCID03 - Đếm số lượng thông báo khi userId là null")
    void UTCID03_CountUnreadByUserId_NullUserId() {
        // Arrange
        when(notificationRepositoryPort.countUnreadByUserId(null)).thenReturn(0L);

        // Act
        long result = countUnreadNotificationUseCase.countUnreadByUserId(null);

        // Assert
        assertEquals(0L, result);
        verify(notificationRepositoryPort, times(1)).countUnreadByUserId(null);
    }
}
