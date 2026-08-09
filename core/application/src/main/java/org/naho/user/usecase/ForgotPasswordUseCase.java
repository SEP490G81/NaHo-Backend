package org.naho.user.usecase;

import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.EmailPort;
import org.naho.user.command.ForgotPasswordCommand;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.model.User;
import org.naho.user.port.in.ForgotPasswordInputPort;
import org.naho.user.port.out.PasswordResetOtpPort;
import org.naho.user.port.out.UserRepositoryPort;

import java.util.Optional;

public class ForgotPasswordUseCase implements ForgotPasswordInputPort {

    private final UserRepositoryPort userRepository;
    private final PasswordResetOtpPort passwordResetOtpPort;
    private final EmailPort emailPort;

    public ForgotPasswordUseCase(
            UserRepositoryPort userRepository,
            PasswordResetOtpPort passwordResetOtpPort,
            EmailPort emailPort
    ) {
        this.userRepository = userRepository;
        this.passwordResetOtpPort = passwordResetOtpPort;
        this.emailPort = emailPort;
    }

    @Override
    public void forgotPassword(ForgotPasswordCommand command) {
        String email = command.email();

        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            throw new ApplicationException(
                    UserErrorCode.USER_NOT_FOUND,
                    UserDetailMessageKey.USER_EMAIL_NOT_FOUND,
                    email
            );
        }

        User user = userOpt.get();

        if (user.getHashPassword() == null || user.getHashPassword().isBlank()) {
            throw new ApplicationException(
                    UserErrorCode.USER_SOCIAL_LOGIN_CANNOT_RESET_PASSWORD,
                    UserDetailMessageKey.USER_SOCIAL_LOGIN_CANNOT_RESET_PASSWORD
            );
        }

        if (passwordResetOtpPort.hasValidOtp(email)) {
            long ttlSeconds = passwordResetOtpPort.getOtpTtlSeconds(email);
            long cooldownSeconds = 60; // 1 minute cooldown

            if (300 - ttlSeconds < cooldownSeconds) {
                throw new ApplicationException(
                        UserErrorCode.USER_OTP_COOLDOWN,
                        UserDetailMessageKey.USER_OTP_COOLDOWN_DETAIL
                );
            }
        }

        String otpCode = passwordResetOtpPort.generateOtp();
        passwordResetOtpPort.saveOtp(email, otpCode);
        emailPort.sendForgotPasswordOtpEmail(email, user.getFullName(), otpCode);
    }
}
