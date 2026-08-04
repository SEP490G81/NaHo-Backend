package org.naho.user.usecase;

import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.user.command.ResetPasswordCommand;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.model.User;
import org.naho.user.port.in.ResetPasswordInputPort;
import org.naho.user.port.out.EncoderPort;
import org.naho.user.port.out.PasswordResetOtpPort;
import org.naho.user.port.out.UserRepositoryPort;
import org.naho.user.valueobject.Password;

import java.util.Optional;

public class ResetPasswordUseCase implements ResetPasswordInputPort {

    private final UserRepositoryPort userRepository;
    private final PasswordResetOtpPort passwordResetOtpPort;
    private final EncoderPort encoderPort;

    public ResetPasswordUseCase(
            UserRepositoryPort userRepository,
            PasswordResetOtpPort passwordResetOtpPort,
            EncoderPort encoderPort
    ) {
        this.userRepository = userRepository;
        this.passwordResetOtpPort = passwordResetOtpPort;
        this.encoderPort = encoderPort;
    }

    @Override
    public void resetPassword(ResetPasswordCommand command) {
        String email = command.email();
        String resetToken = command.resetToken();
        String newPassword = command.newPassword();
        String confirmPassword = command.confirmPassword();

        if (!newPassword.equals(confirmPassword)) {
            throw new ApplicationException(
                    UserErrorCode.USER_PASSWORD_NOT_MATCH,
                    UserDetailMessageKey.USER_PASSWORD_NOT_MATCH_DETAIL
            );
        }

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new ApplicationException(
                    UserErrorCode.USER_NOT_FOUND,
                    UserDetailMessageKey.USER_EMAIL_NOT_FOUND,
                    email
            );
        }

        User user = userOpt.get();

        if (!passwordResetOtpPort.verifyResetToken(email, resetToken)) {
            throw new ApplicationException(
                    UserErrorCode.USER_INVALID_RESET_TOKEN,
                    UserDetailMessageKey.USER_INVALID_RESET_TOKEN_DETAIL
            );
        }

        if (encoderPort.matches(newPassword, user.getHashPassword())) {
            throw new ApplicationException(
                    UserErrorCode.USER_PASSWORD_SAME_AS_OLD,
                    UserDetailMessageKey.USER_PASSWORD_SAME_AS_OLD_DETAIL
            );
        }

        Password newValidPassword = Password.of(newPassword);
        String encodedPassword = encoderPort.hashPassword(newValidPassword.getValue());
        user.setHashPassword(encodedPassword);

        if (!user.isEmailVerified()) {
            user.verifyEmail();
        }

        userRepository.save(user, null);

        passwordResetOtpPort.removeResetToken(email);
    }
}
