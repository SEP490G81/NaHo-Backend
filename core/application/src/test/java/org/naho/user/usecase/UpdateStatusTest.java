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
import org.naho.user.type.UserStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateStatusTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private UserResultMapper userResultMapper;

    @Mock
    private RoleRepositoryPort roleRepositoryPort;

    @InjectMocks
    private UpdateUserUseCase updateUserUseCase;

    @Test
    void UTCID01_Should_UpdateStatus_Successfully() {
        // Arrange (Given)
        Long userId = 1L;
        String statusStr = "ACTIVE";

        User user = spy(User.builder()
                .id(userId)
                .status(UserStatus.UNACTIVE)
                .build());

        User updatedUser = User.builder()
                .id(userId)
                .status(UserStatus.ACTIVE)
                .build();

        List<String> roleNames = List.of("LEARNER");
        UserResult expectedResult = mock(UserResult.class);

        when(userRepositoryPort.findById(userId))
                .thenReturn(Optional.of(user));

        when(userRepositoryPort.save(user))
                .thenReturn(updatedUser);

        when(roleRepositoryPort.findRoleNamesByUserId(userId))
                .thenReturn(roleNames);

        when(userResultMapper.domainToResult(updatedUser, roleNames))
                .thenReturn(expectedResult);

        // Act (When)
        UserResult result = updateUserUseCase.updateStatus(userId, statusStr);

        // Assert (Then)
        assertEquals(expectedResult, result);

        verify(userRepositoryPort, times(1))
                .findById(userId);

        verify(user, times(1))
                .setStatus(UserStatus.ACTIVE);

        verify(userRepositoryPort, times(1))
                .save(user);

        verify(roleRepositoryPort, times(1))
                .findRoleNamesByUserId(userId);

        verify(userResultMapper, times(1))
                .domainToResult(updatedUser, roleNames);

        verifyNoMoreInteractions(userRepositoryPort, roleRepositoryPort, userResultMapper);
    }

    @Test
    void UTCID02_Should_ThrowException_When_StatusInvalid() {
        // Arrange (Given)
        Long userId = 1L;
        String invalidStatus = "INVALID";

        User user = spy(User.builder()
                .id(userId)
                .status(UserStatus.UNACTIVE)
                .build());

        when(userRepositoryPort.findById(userId))
                .thenReturn(Optional.of(user));

        // Act (When) & Assert (Then)
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> updateUserUseCase.updateStatus(userId, invalidStatus)
        );

        assertEquals(UserErrorCode.USER_PERSIST_FAILED, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_UPDATE_STATUS_FAILED, exception.getMessage());

        verify(userRepositoryPort, times(1))
                .findById(userId);

        verify(userRepositoryPort, never()).save(any());
        verifyNoInteractions(roleRepositoryPort, userResultMapper);
    }

    @Test
    void UTCID03_Should_ThrowException_When_UserNotFound() {
        // Arrange (Given)
        Long userId = 999L;
        String statusStr = "ACTIVE";

        when(userRepositoryPort.findById(userId))
                .thenReturn(Optional.empty());

        // Act (When) & Assert (Then)
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> updateUserUseCase.updateStatus(userId, statusStr)
        );

        assertEquals(UserErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_ID_NOT_FOUND, exception.getMessage());

        verify(userRepositoryPort, times(1))
                .findById(userId);

        verify(userRepositoryPort, never()).save(any());
        verifyNoInteractions(roleRepositoryPort, userResultMapper);
    }

    @Test
    void UTCID04_Should_ThrowException_When_StatusNull() {
        // Arrange (Given)
        Long userId = 1L;
        String nullStatus = null;

        User user = User.builder()
                .id(userId)
                .status(UserStatus.UNACTIVE)
                .build();

        when(userRepositoryPort.findById(userId))
                .thenReturn(Optional.of(user));

        // Act (When) & Assert (Then)
        assertThrows(
                NullPointerException.class,
                () -> updateUserUseCase.updateStatus(userId, nullStatus)
        );

        verify(userRepositoryPort, times(1))
                .findById(userId);

        verify(userRepositoryPort, never()).save(any());
        verifyNoInteractions(roleRepositoryPort, userResultMapper);
    }

    @Test
    void UTCID05_Should_UpdateStatus_Successfully_When_StatusLowercase() {
        // Arrange (Given)
        Long userId = 1L;
        String lowercaseStatus = "active";

        User user = spy(User.builder()
                .id(userId)
                .status(UserStatus.UNACTIVE)
                .build());

        User updatedUser = User.builder()
                .id(userId)
                .status(UserStatus.ACTIVE)
                .build();

        List<String> roleNames = List.of("LEARNER");
        UserResult expectedResult = mock(UserResult.class);

        when(userRepositoryPort.findById(userId))
                .thenReturn(Optional.of(user));

        when(userRepositoryPort.save(user))
                .thenReturn(updatedUser);

        when(roleRepositoryPort.findRoleNamesByUserId(userId))
                .thenReturn(roleNames);

        when(userResultMapper.domainToResult(updatedUser, roleNames))
                .thenReturn(expectedResult);

        // Act (When)
        UserResult result = updateUserUseCase.updateStatus(userId, lowercaseStatus);

        // Assert (Then)
        assertEquals(expectedResult, result);

        verify(userRepositoryPort, times(1))
                .findById(userId);

        verify(user, times(1))
                .setStatus(UserStatus.ACTIVE);

        verify(userRepositoryPort, times(1))
                .save(user);

        verify(roleRepositoryPort, times(1))
                .findRoleNamesByUserId(userId);

        verify(userResultMapper, times(1))
                .domainToResult(updatedUser, roleNames);

        verifyNoMoreInteractions(userRepositoryPort, roleRepositoryPort, userResultMapper);
    }
}
