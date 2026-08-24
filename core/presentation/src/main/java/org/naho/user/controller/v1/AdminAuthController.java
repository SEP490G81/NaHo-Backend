package org.naho.user.controller.v1;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.user.command.CredentialsLoginCommand;
import org.naho.user.command.LogoutCommand;
import org.naho.user.constant.TokenType;
import org.naho.user.dto.mapper.LoginRequestMapper;
import org.naho.user.dto.request.CredentialsLoginRequest;
import org.naho.user.helper.CookieFactory;
import org.naho.user.helper.LoginRequestResolver;
import org.naho.user.port.in.AuthInputPort;
import org.naho.user.result.AccessTokenPayload;
import org.naho.user.result.LoginResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {
    private final AuthInputPort authInputPort;
    private final LoginRequestMapper loginRequestMapper;
    private final CookieFactory cookieFactory;
    private final LoginRequestResolver loginRequestResolver;

    // PUBLIC RESOURCE - ADMIN / CONTENT MANAGER LOGIN ONLY
    @ApiResponseMessage(message = UserDetailMessageKey.USER_LOGIN_SUCCESSFULLY)
    @PostMapping("/login")
    public ResponseEntity<Void> credentialsLogin(
            @RequestBody CredentialsLoginRequest request,
            HttpServletRequest httpServletRequest
    ) {
        request.setIpAddress(loginRequestResolver.getIpAddress(httpServletRequest));
        request.setUserAgent(loginRequestResolver.getUserAgent(httpServletRequest));

        CredentialsLoginCommand command = loginRequestMapper.requestToCommand(request);
        LoginResult result = authInputPort.credentialsAdminLogin(command);

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
        if (payload != null) {
            authInputPort.logout(new LogoutCommand(
                    payload.userId(),
                    payload.userSessionId()
            ));
        }

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
}
