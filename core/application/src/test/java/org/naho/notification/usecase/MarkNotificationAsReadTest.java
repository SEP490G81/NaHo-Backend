package org.naho.notification.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.i18n.message.notification.NotificationDetailMessageKey;
import org.naho.notification.command.MarkNotificationAsReadCommand;
import org.naho.notification.model.Notification;
import org.naho.notification.port.out.NotificationRepositoryPort;
import org.naho.notification.result.NotificationResult;
import org.naho.notification.type.NotificationType;
import org.naho.shared.exception.DomainException;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarkNotificationAsReadTest {

    @Mock
    private NotificationRepositoryPort notificationRepositoryPort;

    @InjectMocks
    private MarkNotificationAsReadUseCase markNotificationAsReadUseCase;

    @Test
    @DisplayName("UTCID01 - Đánh dấu thông báo đã đọc thành công")
    void UTCID01_MarkAsRead_Success() {
        // Arrange
        Long userId = 100L;
        Long notificationId = 1L;
        MarkNotificationAsReadCommand command = new MarkNotificationAsReadCommand(notificationId, userId);

        Notification originalNotification = Notification.builder()
                .id(notificationId)
                .userId(userId)
                .type(NotificationType.SOCIAL)
                .title("Có người thích bình luận")
                .content("UserA đã thích bình luận của bạn")
                .isRead(false)
                .targetUrl("/post/123")
                .createdTime(Instant.now())
                .build();

        Notification savedNotification = originalNotification.markAsRead();

        when(notificationRepositoryPort.findById(notificationId)).thenReturn(originalNotification);
        when(notificationRepositoryPort.save(any(Notification.class))).thenReturn(savedNotification);

        // Act
        NotificationResult result = markNotificationAsReadUseCase.markAsRead(command);

        // Assert
        assertNotNull(result);
        assertEquals(notificationId, result.id());
        assertEquals(userId, result.userId());
        assertTrue(result.isRead());
        verify(notificationRepositoryPort, times(1)).findById(notificationId);
        verify(notificationRepositoryPort, times(1)).save(any(Notification.class));
    }

    @Test
    @DisplayName("UTCID02 - Ném ngoại lệ khi không tìm thấy thông báo trong hệ thống")
    void UTCID02_MarkAsRead_NotificationNotFound() {
        // Arrange
        Long userId = 100L;
        Long notificationId = 999L;
        MarkNotificationAsReadCommand command = new MarkNotificationAsReadCommand(notificationId, userId);

        when(notificationRepositoryPort.findById(notificationId)).thenReturn(null);

        // Act & Assert
        DomainException exception = assertThrows(DomainException.class,
                () -> markNotificationAsReadUseCase.markAsRead(command));

        assertEquals("NOTIFICATION_NOT_FOUND", exception.getErrorCode().getCode());
        assertEquals("notification.title.not-found", exception.getErrorCode().getTitleKey());
        assertEquals(404, exception.getErrorCode().getStatusCode());
        assertEquals(NotificationDetailMessageKey.NOTIFICATION_NOT_FOUND, exception.getMessage());
        verify(notificationRepositoryPort, times(1)).findById(notificationId);
        verify(notificationRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("UTCID03 - Ném ngoại lệ khi thông báo không thuộc về người dùng yêu cầu")
    void UTCID03_MarkAsRead_UserNotOwner() {
        // Arrange
        Long requestingUserId = 100L;
        Long ownerUserId = 200L;
        Long notificationId = 1L;
        MarkNotificationAsReadCommand command = new MarkNotificationAsReadCommand(notificationId, requestingUserId);

        Notification notificationOfOtherUser = Notification.builder()
                .id(notificationId)
                .userId(ownerUserId)
                .type(NotificationType.LEAGUE)
                .title("Thăng hạng")
                .content("Bạn đã lên hạng Vàng")
                .isRead(false)
                .targetUrl("/league")
                .createdTime(Instant.now())
                .build();

        when(notificationRepositoryPort.findById(notificationId)).thenReturn(notificationOfOtherUser);

        // Act & Assert
        DomainException exception = assertThrows(DomainException.class,
                () -> markNotificationAsReadUseCase.markAsRead(command));

        assertEquals("NOTIFICATION_NOT_FOUND", exception.getErrorCode().getCode());
        assertEquals("notification.title.not-found", exception.getErrorCode().getTitleKey());
        assertEquals(404, exception.getErrorCode().getStatusCode());
        assertEquals(NotificationDetailMessageKey.NOTIFICATION_NOT_FOUND, exception.getMessage());
        verify(notificationRepositoryPort, times(1)).findById(notificationId);
        verify(notificationRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("UTCID04 - Đánh dấu đã đọc thành công khi thông báo vốn đã ở trạng thái đã đọc")
    void UTCID04_MarkAsRead_AlreadyRead() {
        // Arrange
        Long userId = 100L;
        Long notificationId = 1L;
        MarkNotificationAsReadCommand command = new MarkNotificationAsReadCommand(notificationId, userId);

        Notification alreadyReadNotification = Notification.builder()
                .id(notificationId)
                .userId(userId)
                .type(NotificationType.SYSTEM)
                .title("Chào mừng")
                .content("Chào mừng bạn đến với NaHo")
                .isRead(true)
                .targetUrl("/home")
                .createdTime(Instant.now())
                .build();

        when(notificationRepositoryPort.findById(notificationId)).thenReturn(alreadyReadNotification);
        when(notificationRepositoryPort.save(any(Notification.class))).thenReturn(alreadyReadNotification);

        // Act
        NotificationResult result = markNotificationAsReadUseCase.markAsRead(command);

        // Assert
        assertNotNull(result);
        assertEquals(notificationId, result.id());
        assertTrue(result.isRead());
        verify(notificationRepositoryPort, times(1)).findById(notificationId);
        verify(notificationRepositoryPort, times(1)).save(any(Notification.class));
    }
}
