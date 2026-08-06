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
import org.naho.user.command.UpdateUserInfoCommand;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.mapper.UserResultMapper;
import org.naho.user.model.User;
import org.naho.user.port.out.RoleRepositoryPort;
import org.naho.user.port.out.UserRepositoryPort;
import org.naho.user.result.UserResult;
import org.naho.user.type.Gender;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateUserInfoTest {

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
    @DisplayName("UTCID01 - Cập nhật thông tin người dùng thành công khi command hợp lệ")
    void UTCID01_UpdateUserInfoSuccess() {
        // Arrange
        UpdateUserInfoCommand command = UpdateUserInfoCommand.builder()
                .id(1L)
                .username("vuongtruc")
                .fullName("Nguyen Vuong Truc")
                .gender(Gender.MALE)
                .dob(LocalDate.of(2000, 1, 1))
                .build();

        User savedUser = User.builder()
                .id(1L)
                .fullName("Nguyen Vuong Truc")
                .build();
        UserResult expectedResult = mock(UserResult.class);

        when(userRepositoryPort.updateUserInfo(command)).thenReturn(savedUser);
        when(userResultMapper.domainToResult(savedUser)).thenReturn(expectedResult);

        // Act
        UserResult result = crudUserUseCase.updateUserInfo(command);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResult, result);
        verify(userRepositoryPort, times(1)).updateUserInfo(command);
        verify(userResultMapper, times(1)).domainToResult(savedUser);
    }

    @Test
    @DisplayName("UTCID02 - Cập nhật thông tin người dùng thất bại khi command là null")
    void UTCID02_CommandNull() {
        // Arrange
        UpdateUserInfoCommand command = null;

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> crudUserUseCase.updateUserInfo(command)
        );

        assertEquals(UserErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_ID_NULL, exception.getMessage());
        verify(userRepositoryPort, never()).updateUserInfo(any());
    }

    @Test
    @DisplayName("UTCID03 - Cập nhật thông tin người dùng thất bại khi id trong command là null")
    void UTCID03_CommandIdNull() {
        // Arrange
        UpdateUserInfoCommand command = UpdateUserInfoCommand.builder()
                .id(null)
                .fullName("Nguyen Vuong Truc")
                .build();

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> crudUserUseCase.updateUserInfo(command)
        );

        assertEquals(UserErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_ID_NULL, exception.getMessage());
        verify(userRepositoryPort, never()).updateUserInfo(any());
    }
}
