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
import org.naho.user.model.Role;
import org.naho.user.model.User;
import org.naho.user.port.out.RoleRepositoryPort;
import org.naho.user.port.out.UserRepositoryPort;
import org.naho.user.result.UserResult;
import org.naho.user.type.RoleName;
import org.naho.user.type.UserStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.naho.shared.port.out.TransactionPort;

@ExtendWith(MockitoExtension.class)
class UpdateStatusTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;
    @Mock
    private RoleRepositoryPort roleRepositoryPort;
    @Mock
    private UserResultMapper userResultMapper;
    @Mock
    private TransactionPort transactionPort;

    @InjectMocks
    private UpdateUserUseCase updateUserUseCase;

    @BeforeEach
    void setUp() {
        lenient().doAnswer(invocation -> {
            java.util.function.Supplier<?> action = invocation.getArgument(0);
            return action.get();
        }).when(transactionPort).execute(any(java.util.function.Supplier.class));
    }

    @Test
    @DisplayName("UTCID01 - Cập nhật trạng thái người dùng thành công khi id và status hợp lệ")
    void UTCID01_UpdateStatusSuccess() {
        // Arrange
        Long userId = 1L;
        Long roleId = 2L;

        User user = User.builder()
                .id(userId)
                .roleId(roleId)
                .status(UserStatus.UNACTIVE)
                .build();

        Role role = Role.builder()
                .id(roleId)
                .roleName(RoleName.LEARNER)
                .build();

        User updatedUser = User.builder()
                .id(userId)
                .roleId(roleId)
                .status(UserStatus.ACTIVE)
                .build();

        when(userRepositoryPort.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepositoryPort.findById(roleId)).thenReturn(Optional.of(role));
        when(userRepositoryPort.updateUserStatus(userId, UserStatus.ACTIVE)).thenReturn(updatedUser);

        // Act
        UserStatus result = updateUserUseCase.updateStatus(userId);

        // Assert
        assertEquals(UserStatus.ACTIVE, result);
        verify(userRepositoryPort, times(1)).findById(userId);
        verify(roleRepositoryPort, times(1)).findById(roleId);
        verify(userRepositoryPort, times(1)).updateUserStatus(userId, UserStatus.ACTIVE);
    }

    @Test
    @DisplayName("UTCID02 - Cập nhật trạng thái thất bại khi không tìm thấy người dùng")
    void UTCID02_UserNotFound() {
        // Arrange
        Long userId = 99L;

        when(userRepositoryPort.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> updateUserUseCase.updateStatus(userId)
        );

        assertEquals(UserErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_ID_NOT_FOUND, exception.getMessage());
        verify(userRepositoryPort, times(1)).findById(userId);
        verify(userRepositoryPort, never()).updateUserStatus(any(), any());
    }

    @Test
    @DisplayName("UTCID03 - Cập nhật trạng thái thất bại khi chuỗi status không hợp lệ")
    void UTCID03_InvalidUserStatus() {
        // Arrange
        Long userId = 1L;
        Long roleId = 2L;

        User user = User.builder()
                .id(userId)
                .roleId(roleId)
                .status(null)
                .build();

        Role role = Role.builder()
                .id(roleId)
                .roleName(RoleName.LEARNER)
                .build();

        when(userRepositoryPort.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepositoryPort.findById(roleId)).thenReturn(Optional.of(role));

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> updateUserUseCase.updateStatus(userId)
        );

        assertEquals(UserErrorCode.USER_PERSIST_FAILED, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_UPDATE_STATUS_FAILED, exception.getMessage());
        verify(userRepositoryPort, never()).updateUserStatus(any(), any());
    }

    @Test
    @DisplayName("UTCID04 - Cập nhật trạng thái thất bại khi người dùng có vai trò ADMIN")
    void UTCID04_AdminCannotBeDisabled() {
        // Arrange
        Long userId = 1L;
        Long roleId = 1L;

        User user = User.builder()
                .id(userId)
                .roleId(roleId)
                .status(UserStatus.ACTIVE)
                .build();

        Role role = Role.builder()
                .id(roleId)
                .roleName(RoleName.ADMIN)
                .build();

        when(userRepositoryPort.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepositoryPort.findById(roleId)).thenReturn(Optional.of(role));

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> updateUserUseCase.updateStatus(userId)
        );

        assertEquals(UserErrorCode.USER_ACCESS_DENIED, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_CANNOT_LOCK_ADMIN, exception.getMessage());
        verify(userRepositoryPort, never()).updateUserStatus(any(), any());
    }
}
