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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUserByIdTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;
    @Mock
    private UserResultMapper userResultMapper;

    @InjectMocks
    private GetUserUseCase getUserUseCase;

    @Test
    @DisplayName("UTCID01 - Lấy thông tin người dùng theo ID thành công khi userId tồn tại")
    void UTCID01_GetUserByIdSuccess() {
        // Arrange
        Long userId = 1L;
        User user = User.builder().id(userId).fullName("Nguyen Van A").build();
        UserResult expectedResult = mock(UserResult.class);

        when(userRepositoryPort.findById(userId)).thenReturn(Optional.of(user));
        when(userResultMapper.domainToResult(user)).thenReturn(expectedResult);

        // Act
        UserResult result = getUserUseCase.getUserById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResult, result);
        verify(userRepositoryPort, times(1)).findById(userId);
        verify(userResultMapper, times(1)).domainToResult(user);
    }

    @Test
    @DisplayName("UTCID02 - Lấy thông tin người dùng theo ID thất bại khi userId không tồn tại")
    void UTCID02_UserNotFound() {
        // Arrange
        Long userId = 99L;

        when(userRepositoryPort.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> getUserUseCase.getUserById(userId)
        );

        assertEquals(UserErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_GET_FAILED, exception.getMessage());
        verify(userRepositoryPort, times(1)).findById(userId);
        verify(userResultMapper, never()).domainToResult(any());
    }
}
