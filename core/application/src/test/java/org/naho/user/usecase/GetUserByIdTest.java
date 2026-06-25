package org.naho.user.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.shared.exception.ApplicationException;
import org.naho.user.exception.UserErrorCode;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.user.mapper.UserResultMapper;
import org.naho.user.model.User;
import org.naho.user.port.out.RoleRepositoryPort;
import org.naho.user.port.out.UserRepositoryPort;
import org.naho.user.result.UserResult;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUserByIdTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private RoleRepositoryPort roleRepositoryPort;

    @Mock
    private UserResultMapper userResultMapper;

    @InjectMocks
    private GetUserUseCase getUserUseCase;

    @Test
    void UTCID01_Should_ReturnUserResult_When_UserIdExists() {
        // Arrange (Given)
        Long userId = 1L;

        User user = User.builder()
                .id(userId)
                .build();

        List<String> roleNames = List.of("LEARNER");
        UserResult expectedResult = mock(UserResult.class);

        when(userRepositoryPort.findById(userId))
                .thenReturn(Optional.of(user));

        when(roleRepositoryPort.findRoleNamesByUserId(userId))
                .thenReturn(roleNames);

        when(userResultMapper.domainToResult(user, roleNames))
                .thenReturn(expectedResult);

        // Act (When)
        UserResult result = getUserUseCase.getUserById(userId);

        // Assert (Then)
        assertEquals(expectedResult, result);

        verify(userRepositoryPort, times(1))
                .findById(userId);

        verify(roleRepositoryPort, times(1))
                .findRoleNamesByUserId(userId);

        verify(userResultMapper, times(1))
                .domainToResult(user, roleNames);

        verifyNoMoreInteractions(userRepositoryPort, roleRepositoryPort, userResultMapper);
    }

    @Test
    void UTCID02_Should_ThrowException_When_UserIdDoesNotExist() {
        // Arrange (Given)
        Long userId = 2L;

        when(userRepositoryPort.findById(userId))
                .thenReturn(Optional.empty());

        // Act (When) & Assert (Then)
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> getUserUseCase.getUserById(userId)
        );

        assertEquals(UserErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_GET_FAILED, exception.getMessage());

        verify(userRepositoryPort, times(1))
                .findById(userId);

        verifyNoMoreInteractions(userRepositoryPort);
        verifyNoInteractions(roleRepositoryPort, userResultMapper);
    }
}
