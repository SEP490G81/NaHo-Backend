package org.naho.user.usecase;

import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.EmailPort;
import org.naho.user.command.ResendOtpCommand;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.model.User;
import org.naho.user.port.in.ResendOtpInputPort;
import org.naho.user.port.out.OtpPort;
import org.naho.user.port.out.UserRepositoryPort;

public class ResendOtpUseCase implements ResendOtpInputPort {

    private final UserRepositoryPort userRepositoryPort;
    private final OtpPort otpPort;
    private final EmailPort emailPort;

    public ResendOtpUseCase(
            UserRepositoryPort userRepositoryPort,
            OtpPort otpPort,
            EmailPort emailPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.otpPort = otpPort;
        this.emailPort = emailPort;
    }

    @Override
    public void resendOtp(ResendOtpCommand command) {
        User user = userRepositoryPort.findByEmail(command.email())
                .orElseThrow(() -> new ApplicationException(
                        UserErrorCode.USER_NOT_FOUND,
                        UserDetailMessageKey.USER_EMAIL_NOT_FOUND));

        if (user.isEmailVerified()) {
            throw new ApplicationException(
                    UserErrorCode.USER_ALREADY_EXISTS,
                    UserDetailMessageKey.USER_EMAIL_ALREADY_VERIFIED);
        }

        if (otpPort.hasValidOtp(command.email())) {
            long ttl = otpPort.getOtpTtlSeconds(command.email());
            // If OTP was generated less than 1 minute ago (TTL > 4 minutes = 240 seconds), block resend
            if (ttl > 240) {
                throw new ApplicationException(
                        UserErrorCode.USER_OTP_COOLDOWN,
                        UserDetailMessageKey.USER_OTP_COOLDOWN_DETAIL);
            }
        }

        String otp = otpPort.generateOtp();
        otpPort.saveOtp(command.email(), otp);
        emailPort.sendOtpEmail(command.email(), user.getFullName(), otp);
    }
}
