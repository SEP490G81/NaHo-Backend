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
class GetUserByUserNameTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;
    @Mock
    private UserResultMapper userResultMapper;

    @InjectMocks
    private GetUserUseCase getUserUseCase;

    @Test
    @DisplayName("UTCID01 - Lấy thông tin người dùng theo username thành công khi username tồn tại")
    void UTCID01_GetUserByUserNameSuccess() {
        // Arrange
        String userName = "vuongtruc";
        User user = User.builder().id(1L).fullName("Nguyen Vuong Truc").build();
        UserResult expectedResult = mock(UserResult.class);

        when(userRepositoryPort.findByUsername(userName)).thenReturn(Optional.of(user));
        when(userResultMapper.domainToResult(user)).thenReturn(expectedResult);

        // Act
        UserResult result = getUserUseCase.getUserByUserName(userName);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResult, result);
        verify(userRepositoryPort, times(1)).findByUsername(userName);
        verify(userResultMapper, times(1)).domainToResult(user);
    }

    @Test
    @DisplayName("UTCID02 - Lấy thông tin người dùng theo username thất bại khi username không tồn tại")
    void UTCID02_UserNotFound() {
        // Arrange
        String userName = "unknown_user";

        when(userRepositoryPort.findByUsername(userName)).thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> getUserUseCase.getUserByUserName(userName)
        );

        assertEquals(UserErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_GET_FAILED, exception.getMessage());
        verify(userRepositoryPort, times(1)).findByUsername(userName);
        verify(userResultMapper, never()).domainToResult(any());
    }
}
