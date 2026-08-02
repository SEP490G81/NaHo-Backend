package org.naho.user.usecase;

import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;
import org.naho.user.command.ForceLogoutCommand;
import org.naho.user.command.VerifyEmailCommand;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.model.User;
import org.naho.user.model.UserSession;
import org.naho.user.port.in.VerifyEmailInputPort;
import org.naho.user.port.out.*;
import org.naho.user.result.LoginResult;
import org.naho.user.result.TokenResult;
import org.naho.user.type.SessionRevokedReason;

import java.time.Instant;

public class VerifyEmailUseCase implements VerifyEmailInputPort {

        private final UserRepositoryPort userRepositoryPort;
        private final OtpPort otpPort;
        private final TransactionPort transactionPort;
        private final TokenServicePort tokenServicePort;
        private final EncoderPort encoderPort;
        private final UserSessionRepositoryPort userSessionRepositoryPort;
        private final UserSessionServicePort userSessionServicePort;
        private final UserSessionEventPublisherPort userSessionEventPublisherPort;

        public VerifyEmailUseCase(
                        UserRepositoryPort userRepositoryPort,
                        OtpPort otpPort,
                        TransactionPort transactionPort,
                        TokenServicePort tokenServicePort,
                        EncoderPort encoderPort,
                        UserSessionRepositoryPort userSessionRepositoryPort,
                        UserSessionServicePort userSessionServicePort,
                        UserSessionEventPublisherPort userSessionEventPublisherPort) {
                this.userRepositoryPort = userRepositoryPort;
                this.otpPort = otpPort;
                this.transactionPort = transactionPort;
                this.tokenServicePort = tokenServicePort;
                this.encoderPort = encoderPort;
                this.userSessionRepositoryPort = userSessionRepositoryPort;
                this.userSessionServicePort = userSessionServicePort;
                this.userSessionEventPublisherPort = userSessionEventPublisherPort;
        }

        @Override
        public LoginResult verifyEmail(VerifyEmailCommand command) {
                User user = userRepositoryPort.findByEmail(command.email())
                                .orElseThrow(() -> new ApplicationException(
                                                UserErrorCode.USER_NOT_FOUND,
                                                UserDetailMessageKey.USER_EMAIL_NOT_FOUND));

                if (user.isEmailVerified()) {
                        throw new ApplicationException(
                                        UserErrorCode.USER_ALREADY_EXISTS,
                                        UserDetailMessageKey.USER_EMAIL_ALREADY_VERIFIED);
                }

                if (!otpPort.verifyOtp(command.email(), command.otpCode())) {
                        otpPort.incrementFailedAttempts(command.email());
                        int attempts = otpPort.getFailedAttempts(command.email());
                        if (attempts >= 5) {
                                otpPort.removeOtp(command.email());
                                otpPort.clearFailedAttempts(command.email());
                                throw new ApplicationException(
                                                UserErrorCode.USER_OTP_ATTEMPTS_EXCEEDED,
                                                UserDetailMessageKey.USER_OTP_ATTEMPTS_EXCEEDED_DETAIL);
                        }
                        throw new ApplicationException(
                                        UserErrorCode.USER_INVALID_OTP,
                                        UserDetailMessageKey.USER_INVALID_OTP_DETAIL);
                }

                return transactionPort.execute(() -> doVerifyEmail(user, command));
        }

        private LoginResult doVerifyEmail(User user, VerifyEmailCommand command) {
                user.verifyEmail();
                userRepositoryPort.save(user);
                otpPort.removeOtp(command.email());
                otpPort.clearFailedAttempts(command.email());

                userSessionServicePort.revokeAllSessionsByUserId(
                                user.getId(),
                                SessionRevokedReason.LOGIN_ON_OTHER_DEVICE);

                Instant now = Instant.now();
                TokenResult refreshToken = tokenServicePort.generateRefreshToken(now);
                String hashRefreshToken = encoderPort.hashRefreshToken(refreshToken.value());

                UserSession userSession = UserSession.builder()
                                .userId(user.getId())
                                .hashRefreshToken(hashRefreshToken)
                                .deviceId(command.deviceId())
                                .userAgent(command.userAgent())
                                .ipAddress(command.ipAddress())
                                .issuedAt(now)
                                .refreshTokenExpiresAt(refreshToken.expiresAt())
                                .accessTokenExpiresAt(tokenServicePort.getAccessTokenExpiry(now))
                                .lastUsedAt(now)
                                .build();

                UserSession savedUserSession = userSessionRepositoryPort.save(userSession);
                TokenResult accessToken = tokenServicePort.generateAccessToken(savedUserSession);

                userSessionEventPublisherPort.publishForceLogoutEvent(new ForceLogoutCommand(
                                savedUserSession.getUserId(),
                                savedUserSession.getId(),
                                SessionRevokedReason.LOGIN_ON_OTHER_DEVICE));

                return new LoginResult(accessToken, refreshToken);
        }
}
