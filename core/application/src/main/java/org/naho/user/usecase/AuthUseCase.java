package org.naho.user.usecase;

import org.naho.email.port.out.EmailPort;
import org.naho.i18n.message.user.RoleDetailMessageKey;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.i18n.message.user.UserTitleMessageKey;
import org.naho.learning.port.in.CrudUserLearningProgressInputPort;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;
import org.naho.user.command.CredentialsLoginCommand;
import org.naho.user.command.ForceLogoutCommand;
import org.naho.user.command.GoogleLoginCommand;
import org.naho.user.command.LogoutCommand;
import org.naho.user.constant.UserLoginConstant;
import org.naho.user.exception.RoleErrorCode;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.model.AuthProvider;
import org.naho.user.model.Role;
import org.naho.user.model.User;
import org.naho.user.model.UserSession;
import org.naho.user.port.in.AuthInputPort;
import org.naho.user.port.out.*;
import org.naho.user.result.LoginResult;
import org.naho.user.result.TokenResult;
import org.naho.user.type.AuthProviderName;
import org.naho.user.type.RoleName;
import org.naho.user.type.SessionRevokedReason;
import org.naho.user.type.UserStatus;
import org.naho.user.valueobject.Email;

import java.time.Instant;

public class AuthUseCase implements AuthInputPort {
    private final UserRepositoryPort userRepositoryPort;
    private final EncoderPort encoderPort;
    private final TokenServicePort tokenServicePort;
    private final UserSessionRepositoryPort userSessionRepositoryPort;
    private final RoleRepositoryPort roleRepositoryPort;
    private final TransactionPort transactionPort;
    private final UserSessionServicePort userSessionServicePort;
    private final UserSessionEventPublisherPort userSessionEventPublisherPort;
    private final CrudUserLearningProgressInputPort crudUserLearningProgressInputPort;
    private final OtpPort otpPort;
    private final EmailPort emailPort;

    public AuthUseCase(
            UserRepositoryPort userRepositoryPort,
            EncoderPort encoderPort,
            TokenServicePort tokenServicePort,
            UserSessionRepositoryPort userSessionRepositoryPort,
            RoleRepositoryPort roleRepositoryPort,
            TransactionPort transactionPort,
            UserSessionServicePort userSessionServicePort,
            UserSessionEventPublisherPort userSessionEventPublisherPort,
            CrudUserLearningProgressInputPort crudUserLearningProgressInputPort,
            OtpPort otpPort,
            EmailPort emailPort
    ) {
        this.userRepositoryPort = userRepositoryPort;
        this.encoderPort = encoderPort;
        this.tokenServicePort = tokenServicePort;
        this.userSessionRepositoryPort = userSessionRepositoryPort;
        this.roleRepositoryPort = roleRepositoryPort;
        this.transactionPort = transactionPort;
        this.userSessionServicePort = userSessionServicePort;
        this.userSessionEventPublisherPort = userSessionEventPublisherPort;
        this.crudUserLearningProgressInputPort = crudUserLearningProgressInputPort;
        this.otpPort = otpPort;
        this.emailPort = emailPort;
    }

    /**
     * Đăng nhập bằng tài khoản, mật khẩu
     *
     * @param command chứa các thông tin như username, email, raw password
     * @return LoginResult: gồm AT và RT
     */
    @Override
    public LoginResult credentialsLogin(CredentialsLoginCommand command) {
        // trước tiên phải tìm người dùng qua username hoặc email
        User user = userRepositoryPort
                .findByUsernameOrEmail(command.usernameOrEmail())
                .orElseThrow(() -> new ApplicationException(
                        UserErrorCode.USER_LOGIN_FAILED,
                        UserDetailMessageKey.USER_WRONG_LOGIN_INFO
                ));

        Instant now = Instant.now();

        // nếu tài khoản bị khóa do đăng nhập quá nhiều
        if (user.isLockedByLoginFailed(now)) {
            throw new ApplicationException(
                    UserErrorCode.USER_ACCOUNT_LOCKED,
                    UserDetailMessageKey.USER_ACCOUNT_LOCKED,
                    UserLoginConstant.LOCK_DURATION_MINUTES.toMinutes()
            );
        }

        // nếu mật khẩu sai thì tăng số lần đăng nhập failed
        // sau đó lưu lại rồi ném ra lỗi
        // vì lưu rồi ném ra lỗi nên không để trong transaction được
        if (!encoderPort.matches(command.rawPassword(), user.getHashPassword())) {
            user.incrementFailedLoginAttempt(now);
            userRepositoryPort.save(user);

            throw new ApplicationException(
                    UserErrorCode.USER_LOGIN_FAILED,
                    UserDetailMessageKey.USER_WRONG_LOGIN_INFO
            );
        }

        // 1 transaction
        return transactionPort.execute(() ->
                doCredentialsLogin(command, user, now));
    }

    private LoginResult doCredentialsLogin(
            CredentialsLoginCommand command,
            User user,
            Instant now
    ) {
        // Password đúng => reset failed login state
        user.resetFailedLoginAttempt();
        userRepositoryPort.save(user);

        // nếu tài khoản bị unactive
        if (!user.isActive()) {
            throw new ApplicationException(
                    UserErrorCode.USER_LOGIN_FAILED,
                    UserDetailMessageKey.USER_ACCOUNT_NOT_ACTIVE
            );
        }

        // nếu người dùng chưa verify email thì sẽ tạo otp và gửi mail cho nó
        // vì lưu rồi ném ra lỗi nên không để trong transaction được
        if (!user.isEmailVerified()) {
            String email = user.getEmail().getValue();

            if (!otpPort.hasValidOtp(email)) {
                String otp = otpPort.generateOtp();
                otpPort.saveOtp(email, otp);
                emailPort.sendOtpEmail(email, user.getFullName(), otp);
            }

            throw new ApplicationException(
                    UserErrorCode.USER_EMAIL_UNVERIFIED,
                    UserDetailMessageKey.USER_EMAIL_UNVERIFIED_DETAIL
            );
        }

        // thu hồi toàn bộ session khác
        // có 1 câu lệnh update
        userSessionServicePort.revokeAllActiveSessionsByUserId(
                user.getId(),
                SessionRevokedReason.LOGIN_ON_OTHER_DEVICE
        );

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

        // câu lệnh insert
        UserSession savedUserSession = userSessionRepositoryPort.save(userSession);

        TokenResult accessToken = tokenServicePort.generateAccessToken(savedUserSession);

        userSessionEventPublisherPort.publishForceLogoutEvent(new ForceLogoutCommand(
                savedUserSession.getUserId(),
                savedUserSession.getId(),
                SessionRevokedReason.LOGIN_ON_OTHER_DEVICE
        ));

        return new LoginResult(
                accessToken,
                refreshToken
        );
    }

    @Override
    public LoginResult googleLogin(GoogleLoginCommand command) {
        return transactionPort.execute(() -> doGoogleLogin(command));
    }

    private LoginResult doGoogleLogin(GoogleLoginCommand command) {
        // Google provider id
        String providerUserId = command.getSub();

        User currentUser = userRepositoryPort
                .findByProviderUserIdAndProviderName(providerUserId, AuthProviderName.GOOGLE)
                .orElse(null);

        if (currentUser == null) {
            AuthProvider authProvider = AuthProvider.builder()
                    .providerUserId(providerUserId)
                    .providerName(AuthProviderName.GOOGLE)
                    .avatarUrl(command.getPictureUrl())
                    .build();

            User emailUser = userRepositoryPort.findByEmail(command.getEmail())
                    .orElse(null);

            // if user is not found by Google providerUserId and email, create new
            if (emailUser == null) {
                Role learnerRole = roleRepositoryPort.findByName(RoleName.LEARNER)
                        .orElseThrow(() -> new ApplicationException(
                                RoleErrorCode.ROLE_NOT_FOUND,
                                RoleDetailMessageKey.ROLE_ROLE_NAME_NOT_FOUND,
                                RoleName.LEARNER.name()));

                User newUser = User.builder()
                        .email(Email.of(command.getEmail()))
                        .fullName(command.getFullName())
                        .status(UserStatus.ACTIVE)
                        .isEmailVerified(true)
                        .failedLoginAttemptCount(0)
                        .roleId(learnerRole.getId())
                        .build();

                currentUser = userRepositoryPort.createNew(newUser, authProvider);

                // init user learning progress
                crudUserLearningProgressInputPort.initUserLearningProgress(currentUser.getId());
            } else {
                // if user is found by email, update AuthProvider (link to Google)
                currentUser = userRepositoryPort.createNew(emailUser, authProvider);
            }
        }

        if (!currentUser.isActive()) {
            throw new ApplicationException(
                    UserErrorCode.USER_LOGIN_FAILED,
                    UserDetailMessageKey.USER_ACCOUNT_NOT_ACTIVE
            );
        }

        userSessionServicePort.revokeAllActiveSessionsByUserId(
                currentUser.getId(),
                SessionRevokedReason.LOGIN_ON_OTHER_DEVICE
        );

        Instant now = Instant.now();

        TokenResult refreshToken = tokenServicePort.generateRefreshToken(now);

        String hashRefreshToken = encoderPort.hashRefreshToken(refreshToken.value());

        UserSession userSession = UserSession.builder()
                .userId(currentUser.getId())
                .hashRefreshToken(hashRefreshToken)
                .deviceId(command.getDeviceId())
                .userAgent(command.getUserAgent())
                .ipAddress(command.getIpAddress())
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
                SessionRevokedReason.LOGIN_ON_OTHER_DEVICE
        ));

        return new LoginResult(
                accessToken,
                refreshToken
        );
    }

    @Override
    public void logout(LogoutCommand command) {
        if (command == null || command.userId() == null || command.userSessionId() == null) {
            throw new ApplicationException(
                    UserErrorCode.USER_UNAUTHORIZED,
                    UserTitleMessageKey.USER_UNAUTHORIZED_TITLE);
        }
        userSessionRepositoryPort.revokeActiveSessionsByUserIdAndUserSessionId(
                command.userId(),
                command.userSessionId(),
                Instant.now(),
                SessionRevokedReason.USER_LOGOUT
        );
    }

    @Override
    public LoginResult rotateToken(String refreshToken) {
        String hashRefreshToken = encoderPort.hashRefreshToken(refreshToken);
        UserSession userSession = userSessionRepositoryPort.findByHashRefreshToken(hashRefreshToken);
        Instant now = Instant.now();

        userSessionRepositoryPort.verifyUserSession(userSession, now);

        return transactionPort.execute(() -> doRotateToken(userSession, now));
    }

    private LoginResult doRotateToken(UserSession userSession, Instant now) {
        // revoke old refresh token
        userSession.setRevokedReason(SessionRevokedReason.ROTATED);
        userSession.setRevokedAt(now);
        userSession.setLastUsedAt(now);
        userSessionRepositoryPort.save(userSession);

        // generate new refresh token and new user session
        TokenResult newRefreshToken = tokenServicePort.generateRefreshToken(now);

        String newHashRefreshToken = encoderPort.hashRefreshToken(newRefreshToken.value());

        UserSession newUserSession = UserSession.builder()
                .userId(userSession.getUserId())
                .hashRefreshToken(newHashRefreshToken)
                .deviceId(userSession.getDeviceId())
                .userAgent(userSession.getUserAgent())
                .ipAddress(userSession.getIpAddress())
                .issuedAt(now)
                .refreshTokenExpiresAt(newRefreshToken.expiresAt())
                .accessTokenExpiresAt(tokenServicePort.getAccessTokenExpiry(now))
                .lastUsedAt(now)
                .build();

        UserSession savedUserSession = userSessionRepositoryPort.save(newUserSession);

        TokenResult newAccessToken = tokenServicePort.generateAccessToken(savedUserSession);

        return new LoginResult(
                newAccessToken,
                newRefreshToken);
    }
}
