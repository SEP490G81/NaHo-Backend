package org.naho.user.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.file.port.in.CrudFileInputPort;
import org.naho.file.port.out.FileValidatorPort;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.mapper.UserResultMapper;
import org.naho.user.model.User;
import org.naho.user.port.out.RoleRepositoryPort;
import org.naho.user.port.out.UserRepositoryPort;
import org.naho.user.result.UserResult;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindUserByIdTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;
    @Mock
    private FileValidatorPort fileValidatorPort;
    @Mock
    private CrudFileInputPort crudFileInputPort;
    @Mock
    private UserResultMapper userResultMapper;
    @Mock
    private RoleRepositoryPort roleRepositoryPort;

    @InjectMocks
    private CrudUserUseCase crudUserUseCase;

    @Test
    @DisplayName("UTCID01 - Tìm người dùng thành công khi userId hợp lệ và tồn tại")
    void UTCID01_FindUserByIdSuccess() {
        // Arrange
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .fullName("Nguyen Van A")
                .build();
        UserResult expectedResult = mock(UserResult.class);

        when(userRepositoryPort.findById(userId)).thenReturn(Optional.of(user));
        when(userResultMapper.domainToResult(user)).thenReturn(expectedResult);

        // Act
        UserResult result = crudUserUseCase.findUserById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResult, result);
        verify(userRepositoryPort, times(1)).findById(userId);
        verify(userResultMapper, times(1)).domainToResult(user);
    }

    @Test
    @DisplayName("UTCID02 - Tìm người dùng thất bại khi userId là null")
    void UTCID02_UserIdNull() {
        // Arrange
        Long userId = null;

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> crudUserUseCase.findUserById(userId)
        );

        assertEquals(UserErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_ID_NULL, exception.getMessage());
        verify(userRepositoryPort, never()).findById(any());
    }

    @Test
    @DisplayName("UTCID03 - Tìm người dùng thất bại khi không tìm thấy người dùng với userId tương ứng")
    void UTCID03_UserNotFound() {
        // Arrange
        Long userId = 99L;

        when(userRepositoryPort.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> crudUserUseCase.findUserById(userId)
        );

        assertEquals(UserErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_ID_NOT_FOUND, exception.getMessage());
        verify(userRepositoryPort, times(1)).findById(userId);
        verify(userResultMapper, never()).domainToResult(any());
    }
}
