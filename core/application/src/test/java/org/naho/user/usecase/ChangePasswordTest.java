package org.naho.user.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.exception.DomainException;
import org.naho.shared.port.out.EmailPort;
import org.naho.user.command.ChangePasswordCommand;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.model.User;
import org.naho.user.port.out.EncoderPort;
import org.naho.user.port.out.UserRepositoryPort;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangePasswordTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;
    @Mock
    private EncoderPort encoderPort;
    @Mock
    private EmailPort emailPort;

    @InjectMocks
    private ChangePasswordUseCase changePasswordUseCase;

    @Test
    @DisplayName("UTCID01 - Đổi mật khẩu thành công khi thông tin hợp lệ")
    void UTCID01_ChangePasswordSuccess() {
        // Arrange
        ChangePasswordCommand command = new ChangePasswordCommand(
                1L,
                "OldPassword123@",
                "NewPassword123@",
                "NewPassword123@"
        );

        User user = User.builder()
                .id(1L)
                .email(org.naho.user.valueobject.Email.of("user@example.com"))
                .hashPassword("$2a$10$oldHashedPassword")
                .build();

        when(userRepositoryPort.findById(1L)).thenReturn(Optional.of(user));
        when(encoderPort.matches("OldPassword123@", "$2a$10$oldHashedPassword")).thenReturn(true);
        when(encoderPort.matches("NewPassword123@", "$2a$10$oldHashedPassword")).thenReturn(false);
        when(encoderPort.hashPassword("NewPassword123@")).thenReturn("$2a$10$newHashedPassword");

        // Act
        assertDoesNotThrow(() -> changePasswordUseCase.changePassword(command));

        // Assert
        verify(userRepositoryPort, times(1)).findById(1L);
        verify(encoderPort, times(1)).hashPassword("NewPassword123@");
        verify(userRepositoryPort, times(1)).save(user);
        verify(emailPort, times(1)).sendPasswordChangedEmail(anyString(), any());
        assertEquals("$2a$10$newHashedPassword", user.getHashPassword());
    }

    @Test
    @DisplayName("UTCID02 - Đổi mật khẩu thất bại khi mật khẩu mới và xác nhận mật khẩu không khớp")
    void UTCID02_NewPasswordAndConfirmPasswordNotMatch() {
        // Arrange
        ChangePasswordCommand command = new ChangePasswordCommand(
                1L,
                "OldPassword123@",
                "NewPassword123@",
                "DifferentPassword123@"
        );

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> changePasswordUseCase.changePassword(command)
        );

        assertEquals(UserErrorCode.USER_PASSWORD_NOT_MATCH, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_PASSWORD_NOT_MATCH_DETAIL, exception.getMessage());
        verify(userRepositoryPort, never()).findById(any());
    }

    @Test
    @DisplayName("UTCID03 - Đổi mật khẩu thất bại khi người dùng không tồn tại")
    void UTCID03_UserNotFound() {
        // Arrange
        ChangePasswordCommand command = new ChangePasswordCommand(
                99L,
                "OldPassword123@",
                "NewPassword123@",
                "NewPassword123@"
        );

        when(userRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> changePasswordUseCase.changePassword(command)
        );

        assertEquals(UserErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_ID_NOT_FOUND, exception.getMessage());
        verify(userRepositoryPort, times(1)).findById(99L);
        verify(encoderPort, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("UTCID04 - Đổi mật khẩu thất bại khi tài khoản chưa có mật khẩu (tài khoản Social Login Google)")
    void UTCID04_UserHashPasswordBlank_SocialLogin() {
        // Arrange
        ChangePasswordCommand command = new ChangePasswordCommand(
                1L,
                "OldPassword123@",
                "NewPassword123@",
                "NewPassword123@"
        );

        User socialUser = User.builder()
                .id(1L)
                .hashPassword(null)
                .build();

        when(userRepositoryPort.findById(1L)).thenReturn(Optional.of(socialUser));

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> changePasswordUseCase.changePassword(command)
        );

        assertEquals(UserErrorCode.USER_SOCIAL_LOGIN_CANNOT_RESET_PASSWORD, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_SOCIAL_LOGIN_CANNOT_RESET_PASSWORD, exception.getMessage());
        verify(encoderPort, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("UTCID05 - Đổi mật khẩu thất bại khi mật khẩu cũ không đúng")
    void UTCID05_OldPasswordNotMatch() {
        // Arrange
        ChangePasswordCommand command = new ChangePasswordCommand(
                1L,
                "WrongOldPassword123@",
                "NewPassword123@",
                "NewPassword123@"
        );

        User user = User.builder()
                .id(1L)
                .hashPassword("$2a$10$oldHashedPassword")
                .build();

        when(userRepositoryPort.findById(1L)).thenReturn(Optional.of(user));
        when(encoderPort.matches("WrongOldPassword123@", "$2a$10$oldHashedPassword")).thenReturn(false);

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> changePasswordUseCase.changePassword(command)
        );

        assertEquals(UserErrorCode.USER_OLD_PASSWORD_NOT_MATCH, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_OLD_PASSWORD_NOT_MATCH_DETAIL, exception.getMessage());
        verify(userRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("UTCID06 - Đổi mật khẩu thất bại khi mật khẩu mới trùng với mật khẩu cũ")
    void UTCID06_NewPasswordSameAsOldPassword() {
        // Arrange
        ChangePasswordCommand command = new ChangePasswordCommand(
                1L,
                "OldPassword123@",
                "OldPassword123@",
                "OldPassword123@"
        );

        User user = User.builder()
                .id(1L)
                .hashPassword("$2a$10$oldHashedPassword")
                .build();

        when(userRepositoryPort.findById(1L)).thenReturn(Optional.of(user));
        when(encoderPort.matches("OldPassword123@", "$2a$10$oldHashedPassword")).thenReturn(true);

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> changePasswordUseCase.changePassword(command)
        );

        assertEquals(UserErrorCode.USER_PASSWORD_SAME_AS_OLD, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_PASSWORD_SAME_AS_OLD_DETAIL, exception.getMessage());
        verify(userRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("UTCID07 - Đổi mật khẩu thất bại khi mật khẩu mới không đúng định dạng (Boundary)")
    void UTCID07_NewPasswordInvalidFormat() {
        // Arrange
        ChangePasswordCommand command = new ChangePasswordCommand(
                1L,
                "OldPassword123@",
                "123", // invalid password format
                "123"
        );

        User user = User.builder()
                .id(1L)
                .hashPassword("$2a$10$oldHashedPassword")
                .build();

        when(userRepositoryPort.findById(1L)).thenReturn(Optional.of(user));
        when(encoderPort.matches("OldPassword123@", "$2a$10$oldHashedPassword")).thenReturn(true);
        when(encoderPort.matches("123", "$2a$10$oldHashedPassword")).thenReturn(false);

        // Act & Assert
        assertThrows(
                DomainException.class,
                () -> changePasswordUseCase.changePassword(command)
        );

        verify(userRepositoryPort, never()).save(any());
    }
}
