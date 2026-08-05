package org.naho.user.usecase;

import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.user.command.VerifyForgotPasswordOtpCommand;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.model.User;
import org.naho.user.port.in.VerifyForgotPasswordOtpInputPort;
import org.naho.user.port.out.PasswordResetOtpPort;
import org.naho.user.port.out.UserRepositoryPort;

import java.util.Optional;
import java.util.UUID;

public class VerifyForgotPasswordOtpUseCase implements VerifyForgotPasswordOtpInputPort {

    private final UserRepositoryPort userRepository;
    private final PasswordResetOtpPort passwordResetOtpPort;

    public VerifyForgotPasswordOtpUseCase(
            UserRepositoryPort userRepository,
            PasswordResetOtpPort passwordResetOtpPort
    ) {
        this.userRepository = userRepository;
        this.passwordResetOtpPort = passwordResetOtpPort;
    }

    @Override
    public String verifyOtp(VerifyForgotPasswordOtpCommand command) {
        String email = command.email();
        String otpCode = command.otpCode();

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new ApplicationException(
                    UserErrorCode.USER_NOT_FOUND,
                    UserDetailMessageKey.USER_EMAIL_NOT_FOUND,
                    email
            );
        }

        if (passwordResetOtpPort.getFailedAttempts(email) >= 5) {
            passwordResetOtpPort.removeOtp(email);
            passwordResetOtpPort.clearFailedAttempts(email);
            throw new ApplicationException(
                    UserErrorCode.USER_OTP_ATTEMPTS_EXCEEDED,
                    UserDetailMessageKey.USER_OTP_ATTEMPTS_EXCEEDED_DETAIL
            );
        }

        if (!passwordResetOtpPort.verifyOtp(email, otpCode)) {
            passwordResetOtpPort.incrementFailedAttempts(email);
            throw new ApplicationException(
                    UserErrorCode.USER_INVALID_OTP,
                    UserDetailMessageKey.USER_INVALID_OTP_DETAIL
            );
        }

        // OTP is valid. Clean it up.
        passwordResetOtpPort.removeOtp(email);
        passwordResetOtpPort.clearFailedAttempts(email);

        // Generate reset token and save
        String resetToken = UUID.randomUUID().toString();
        passwordResetOtpPort.saveResetToken(email, resetToken);

        return resetToken;
    }
}
