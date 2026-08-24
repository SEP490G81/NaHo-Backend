package org.naho.user.controller.v1;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.user.command.*;
import org.naho.user.constant.TokenType;
import org.naho.user.dto.mapper.*;
import org.naho.user.dto.request.*;
import org.naho.user.helper.CookieFactory;
import org.naho.user.helper.LoginRequestResolver;
import org.naho.user.port.in.*;
import org.naho.user.result.AccessTokenPayload;
import org.naho.user.result.LoginResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthInputPort authInputPort;
    private final LoginRequestMapper loginRequestMapper;
    private final CookieFactory cookieFactory;
    private final LoginRequestResolver loginRequestResolver;
    private final VerifyEmailInputPort verifyEmailInputPort;
    private final ResendOtpInputPort resendOtpInputPort;
    private final VerifyEmailRequestMapper verifyEmailRequestMapper;
    private final ResendOtpRequestMapper resendOtpRequestMapper;
    private final ForgotPasswordInputPort forgotPasswordInputPort;
    private final ResetPasswordInputPort resetPasswordInputPort;
    private final ForgotPasswordRequestMapper forgotPasswordRequestMapper;
    private final ResetPasswordRequestMapper resetPasswordRequestMapper;
    private final VerifyForgotPasswordOtpInputPort verifyForgotPasswordOtpInputPort;
    private final VerifyForgotPasswordOtpRequestMapper verifyForgotPasswordOtpRequestMapper;
    private final ChangePasswordInputPort changePasswordInputPort;
    private final ChangePasswordRequestMapper changePasswordRequestMapper;

    // PUBLIC RESOURCE
    @ApiResponseMessage(message = UserDetailMessageKey.USER_LOGIN_SUCCESSFULLY)
    @PostMapping("/login")
    public ResponseEntity<Void> credentialsLogin(
            @RequestBody CredentialsLoginRequest request,
            HttpServletRequest httpServletRequest
    ) {
        request.setIpAddress(loginRequestResolver.getIpAddress(httpServletRequest));
        request.setUserAgent(loginRequestResolver.getUserAgent(httpServletRequest));

        CredentialsLoginCommand command = loginRequestMapper.requestToCommand(request);
        LoginResult result = authInputPort.credentialsLogin(command);

        ResponseCookie accessTokenCookie =
                cookieFactory.createCookieForJWTToken(result.accessToken());
        ResponseCookie refreshTokenCookie =
                cookieFactory.createCookieForJWTToken(result.refreshToken());

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .build();
    }

    // PUBLIC RESOURCE
    @ApiResponseMessage(message = UserDetailMessageKey.USER_LOGOUT_SUCCESSFULLY)
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @AuthenticationPrincipal AccessTokenPayload payload
    ) {
        authInputPort.logout(new LogoutCommand(
                payload.userId(),
                payload.userSessionId()
        ));

        ResponseCookie clearAccessTokenCookie =
                cookieFactory.clearCookieForJWTToken(TokenType.ACCESS_TOKEN_COOKIE_NAME);

        ResponseCookie clearRefreshTokenCookie =
                cookieFactory.clearCookieForJWTToken(TokenType.REFRESH_TOKEN_COOKIE_NAME);

        ResponseCookie clearAdminAccessTokenCookie =
                cookieFactory.clearCookieForJWTToken(TokenType.ADMIN_ACCESS_TOKEN_COOKIE_NAME);

        ResponseCookie clearAdminRefreshTokenCookie =
                cookieFactory.clearCookieForJWTToken(TokenType.ADMIN_REFRESH_TOKEN_COOKIE_NAME);

        return ResponseEntity
                .noContent()
                .header(HttpHeaders.SET_COOKIE, clearAccessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, clearRefreshTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, clearAdminAccessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, clearAdminRefreshTokenCookie.toString())
                .build();
    }

    // PUBLIC RESOURCE
    @ApiResponseMessage(message = UserDetailMessageKey.USER_ROTATE_TOKEN_SUCCESSFULLY)
    @PostMapping("/rotation")
    public ResponseEntity<Void> rotateToken(
            @CookieValue(value = TokenType.REFRESH_TOKEN_COOKIE_NAME, required = false) String userRefreshToken,
            @CookieValue(value = TokenType.ADMIN_REFRESH_TOKEN_COOKIE_NAME, required = false) String adminRefreshToken
    ) {
        String refreshToken = (adminRefreshToken != null && !adminRefreshToken.isBlank())
                ? adminRefreshToken
                : userRefreshToken;

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new org.naho.shared.exception.ApplicationException(
                    org.naho.user.exception.UserErrorCode.USER_UNAUTHORIZED,
                    UserDetailMessageKey.USER_UNAUTHORIZED
            );
        }

        LoginResult result = authInputPort.rotateToken(refreshToken);

        ResponseCookie accessTokenCookie =
                cookieFactory.createCookieForJWTToken(result.accessToken());
        ResponseCookie refreshTokenCookie =
                cookieFactory.createCookieForJWTToken(result.refreshToken());

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .build();
    }

    // PUBLIC RESOURCE
    @ApiResponseMessage(message = UserDetailMessageKey.USER_EMAIL_VERIFIED_SUCCESSFULLY)
    @PostMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(
            @Valid @RequestBody VerifyEmailRequest request
    ) {
        VerifyEmailCommand command = verifyEmailRequestMapper.toCommand(request);
        LoginResult result = verifyEmailInputPort.verifyEmail(command);

        ResponseCookie accessTokenCookie =
                cookieFactory.createCookieForJWTToken(result.accessToken());
        ResponseCookie refreshTokenCookie =
                cookieFactory.createCookieForJWTToken(result.refreshToken());

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .build();
    }

    // PUBLIC RESOURCE
    @ApiResponseMessage(message = UserDetailMessageKey.USER_OTP_RESENT_SUCCESSFULLY)
    @PostMapping("/resend-otp")
    public ResponseEntity<Void> resendOtp(
            @Valid @RequestBody ResendOtpRequest request
    ) {
        ResendOtpCommand command = resendOtpRequestMapper.toCommand(request);
        resendOtpInputPort.resendOtp(command);
        return ResponseEntity.ok().build();
    }

    // PUBLIC RESOURCE
    @ApiResponseMessage(message = UserDetailMessageKey.USER_FORGOT_PASSWORD_EMAIL_SENT)
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request
    ) {
        ForgotPasswordCommand command = forgotPasswordRequestMapper.requestToCommand(request);
        forgotPasswordInputPort.forgotPassword(command);
        return ResponseEntity.ok().build();
    }

    // PUBLIC RESOURCE
    @ApiResponseMessage(message = UserDetailMessageKey.USER_OTP_VERIFIED_SUCCESSFULLY)
    @PostMapping("/forgot-password-otp")
    public ResponseEntity<java.util.Map<String, String>> verifyForgotPasswordOtp(
            @Valid @RequestBody VerifyForgotPasswordOtpRequest request
    ) {
        VerifyForgotPasswordOtpCommand command = verifyForgotPasswordOtpRequestMapper.toCommand(request);
        String resetToken = verifyForgotPasswordOtpInputPort.verifyOtp(command);
        return ResponseEntity.ok(java.util.Map.of("resetToken", resetToken));
    }

    // PUBLIC RESOURCE
    @ApiResponseMessage(message = UserDetailMessageKey.USER_PASSWORD_RESET_SUCCESSFULLY)
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request
    ) {
        ResetPasswordCommand command = resetPasswordRequestMapper.toCommand(request);
        resetPasswordInputPort.resetPassword(command);
        return ResponseEntity.ok().build();
    }

    // ROLE: USER, ADMIN, CONTENT_MANAGER

    /**
     * Đổi mật khẩu (khi người dùng nhớ mật khẩu cũ)
     *
     * @param payload chứa userId của người đăng nhập (lấy từ JWT token)
     * @param request bao gồm old password và password mới
     * @return Void
     */
    @PreAuthorize("hasAnyRole('LEARNER', 'ADMIN', 'CONTENT_MANAGER')")
    @ApiResponseMessage(message = UserDetailMessageKey.USER_CHANGE_PASSWORD_SUCCESSFULLY)
    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal AccessTokenPayload payload,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        ChangePasswordCommand command = changePasswordRequestMapper.toCommand(payload.userId(), request);
        changePasswordInputPort.changePassword(command);
        return ResponseEntity.ok().build();
    }
}
