package org.naho.notification.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.notification.command.GetListNotificationCommand;
import org.naho.notification.model.Notification;
import org.naho.notification.port.out.NotificationRepositoryPort;
import org.naho.notification.result.NotificationResult;
import org.naho.notification.type.NotificationType;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetListNotificationTest {

    @Mock
    private NotificationRepositoryPort notificationRepositoryPort;

    @InjectMocks
    private GetListNotificationUseCase getListNotificationUseCase;

    @Test
    @DisplayName("UTCID01 - Lấy danh sách thông báo thành công khi có dữ liệu thông báo")
    void UTCID01_GetListByUserId_Success() {
        // Arrange
        Long userId = 100L;
        GetListNotificationCommand command = new GetListNotificationCommand(userId, 10, 0);

        Notification n1 = Notification.builder()
                .id(1L)
                .userId(userId)
                .type(NotificationType.SYSTEM)
                .title("Hệ thống bảo trì")
                .content("Hệ thống sẽ bảo trì vào 0h")
                .isRead(false)
                .targetUrl("/system/status")
                .createdTime(Instant.now())
                .build();

        Notification n2 = Notification.builder()
                .id(2L)
                .userId(userId)
                .type(NotificationType.PAYMENT)
                .title("Thanh toán thành công")
                .content("Gói của bạn đã được gia hạn")
                .isRead(true)
                .targetUrl("/payment/history")
                .createdTime(Instant.now())
                .build();

        when(notificationRepositoryPort.findByUserId(userId, 10, 0))
                .thenReturn(List.of(n1, n2));

        // Act
        List<NotificationResult> results = getListNotificationUseCase.getListByUserId(command);

        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(1L, results.get(0).id());
        assertEquals("Hệ thống bảo trì", results.get(0).title());
        assertFalse(results.get(0).isRead());
        assertEquals(2L, results.get(1).id());
        assertTrue(results.get(1).isRead());
        verify(notificationRepositoryPort, times(1)).findByUserId(userId, 10, 0);
    }

    @Test
    @DisplayName("UTCID02 - Lấy danh sách thông báo trả về danh sách rỗng khi người dùng chưa có thông báo")
    void UTCID02_GetListByUserId_EmptyList() {
        // Arrange
        Long userId = 100L;
        GetListNotificationCommand command = new GetListNotificationCommand(userId, 10, 0);

        when(notificationRepositoryPort.findByUserId(userId, 10, 0))
                .thenReturn(Collections.emptyList());

        // Act
        List<NotificationResult> results = getListNotificationUseCase.getListByUserId(command);

        // Assert
        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(notificationRepositoryPort, times(1)).findByUserId(userId, 10, 0);
    }

    @Test
    @DisplayName("UTCID03 - Lấy danh sách thông báo với phân trang tuỳ chỉnh limit và offset")
    void UTCID03_GetListByUserId_WithOffsetAndLimit() {
        // Arrange
        Long userId = 100L;
        GetListNotificationCommand command = new GetListNotificationCommand(userId, 5, 10);

        Notification n = Notification.builder()
                .id(11L)
                .userId(userId)
                .type(NotificationType.REPORT)
                .title("Báo cáo đã xử lý")
                .content("Báo cáo của bạn đã được giải quyết")
                .isRead(false)
                .targetUrl("/report/11")
                .createdTime(Instant.now())
                .build();

        when(notificationRepositoryPort.findByUserId(userId, 5, 10))
                .thenReturn(List.of(n));

        // Act
        List<NotificationResult> results = getListNotificationUseCase.getListByUserId(command);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(11L, results.get(0).id());
        verify(notificationRepositoryPort, times(1)).findByUserId(userId, 5, 10);
    }
}
