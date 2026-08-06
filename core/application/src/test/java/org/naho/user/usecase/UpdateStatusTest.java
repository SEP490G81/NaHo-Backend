package org.naho.user.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.mapper.UserResultMapper;
import org.naho.user.model.User;
import org.naho.user.port.out.UserRepositoryPort;
import org.naho.user.result.UserResult;
import org.naho.user.type.UserStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateStatusTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;
    @Mock
    private UserResultMapper userResultMapper;

    @InjectMocks
    private UpdateUserUseCase updateUserUseCase;

    @Test
    @DisplayName("UTCID01 - Cập nhật trạng thái người dùng thành công khi id và status hợp lệ")
    void UTCID01_UpdateStatusSuccess() {
        // Arrange
        Long userId = 1L;
        String statusStr = "active";

        User user = User.builder()
                .id(userId)
                .status(UserStatus.UNACTIVE)
                .build();

        User updatedUser = User.builder()
                .id(userId)
                .status(UserStatus.ACTIVE)
                .build();

        UserResult expectedResult = mock(UserResult.class);

        when(userRepositoryPort.findById(userId)).thenReturn(Optional.of(user));
        when(userRepositoryPort.save(user)).thenReturn(updatedUser);
        when(userResultMapper.domainToResult(updatedUser)).thenReturn(expectedResult);

        // Act
        UserResult result = updateUserUseCase.updateStatus(userId, statusStr);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResult, result);
        assertEquals(UserStatus.ACTIVE, user.getStatus());
        verify(userRepositoryPort, times(1)).findById(userId);
        verify(userRepositoryPort, times(1)).save(user);
        verify(userResultMapper, times(1)).domainToResult(updatedUser);
    }

    @Test
    @DisplayName("UTCID02 - Cập nhật trạng thái thất bại khi không tìm thấy người dùng")
    void UTCID02_UserNotFound() {
        // Arrange
        Long userId = 99L;
        String statusStr = "ACTIVE";

        when(userRepositoryPort.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> updateUserUseCase.updateStatus(userId, statusStr)
        );

        assertEquals(UserErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_ID_NOT_FOUND, exception.getMessage());
        verify(userRepositoryPort, times(1)).findById(userId);
        verify(userRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("UTCID03 - Cập nhật trạng thái thất bại khi chuỗi status không hợp lệ")
    void UTCID03_InvalidUserStatus() {
        // Arrange
        Long userId = 1L;
        String invalidStatusStr = "INVALID_STATUS";

        User user = User.builder()
                .id(userId)
                .status(UserStatus.ACTIVE)
                .build();

        when(userRepositoryPort.findById(userId)).thenReturn(Optional.of(user));

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> updateUserUseCase.updateStatus(userId, invalidStatusStr)
        );

        assertEquals(UserErrorCode.USER_PERSIST_FAILED, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_UPDATE_STATUS_FAILED, exception.getMessage());
        verify(userRepositoryPort, never()).save(any());
    }
}
