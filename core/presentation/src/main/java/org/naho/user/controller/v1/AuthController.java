package org.naho.user.controller.v1;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.user.command.CredentialsLoginCommand;
import org.naho.user.command.GoogleLoginCommand;
import org.naho.user.command.LogoutCommand;
import org.naho.user.constant.TokenType;
import org.naho.user.dto.mapper.LoginRequestMapper;
import org.naho.user.dto.mapper.UserResponseMapper;
import org.naho.user.dto.request.CredentialsLoginRequest;
import org.naho.user.dto.request.GoogleLoginRequest;
import org.naho.user.dto.response.UserResponse;
import org.naho.user.helper.CookieFactory;
import org.naho.user.helper.LoginRequestResolver;
import org.naho.user.port.in.AuthInputPort;
import org.naho.user.port.out.TokenServicePort;
import org.naho.user.result.AccessTokenPayload;
import org.naho.user.result.GoogleUserInfoResult;
import org.naho.user.result.LoginResult;
import org.naho.user.result.UserResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthInputPort authInputPort;
    private final LoginRequestMapper loginRequestMapper;
    private final CookieFactory cookieFactory;
    private final UserResponseMapper userResponseMapper;
    private final LoginRequestResolver loginRequestResolver;
    private final TokenServicePort tokenServiceAdapter;
    private final TokenServicePort tokenServicePort;

    @ApiResponseMessage(message = UserDetailMessageKey.USER_GET_SUCCESSFULLY)
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentLoggedUser(
            @AuthenticationPrincipal AccessTokenPayload payload
    ) {
        UserResult result = authInputPort.findUserById(payload.userId());
        UserResponse response = userResponseMapper.resultToResponse(result);

        return ResponseEntity.ok(response);
    }

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

    @ApiResponseMessage(message = UserDetailMessageKey.USER_LOGIN_SUCCESSFULLY)
    @PostMapping("/login/google")
    public ResponseEntity<Void> googleLogin(
            @RequestBody GoogleLoginRequest request,
            HttpServletRequest httpServletRequest
    ) {
        GoogleUserInfoResult googleUserInfoResult =
                tokenServicePort.verifyGoogleToken(request.getIdToken());

        GoogleLoginCommand command = GoogleLoginCommand.builder()
                .sub(googleUserInfoResult.sub())
                .email(googleUserInfoResult.email())
                .fullName(googleUserInfoResult.fullName())
                .pictureUrl(googleUserInfoResult.pictureUrl())
                .deviceId(request.getDeviceId())
                .userAgent(loginRequestResolver.getUserAgent(httpServletRequest))
                .ipAddress(loginRequestResolver.getIpAddress(httpServletRequest))
                .build();

        LoginResult result = authInputPort.googleLogin(command);

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
                cookieFactory.clearCookieForJWTToken(TokenType.ACCESS_TOKEN_NAME);

        ResponseCookie clearRefreshTokenCookie =
                cookieFactory.clearCookieForJWTToken(TokenType.REFRESH_TOKEN_NAME);

        return ResponseEntity
                .noContent()
                .header(HttpHeaders.SET_COOKIE, clearAccessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, clearRefreshTokenCookie.toString())
                .build();
    }

    @ApiResponseMessage(message = UserDetailMessageKey.USER_LOGOUT_ALL_SUCCESSFULLY)
    @PostMapping("/logout-all")
    public ResponseEntity<Void> logoutAllSessions(
            @AuthenticationPrincipal AccessTokenPayload payload
    ) {
        authInputPort.logoutAllSessions(payload.userId());

        ResponseCookie clearAccessTokenCookie =
                cookieFactory.clearCookieForJWTToken(TokenType.ACCESS_TOKEN_NAME);

        ResponseCookie clearRefreshTokenCookie =
                cookieFactory.clearCookieForJWTToken(TokenType.REFRESH_TOKEN_NAME);

        return ResponseEntity
                .noContent()
                .header(HttpHeaders.SET_COOKIE, clearAccessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, clearRefreshTokenCookie.toString())
                .build();
    }

    @ApiResponseMessage(message = UserDetailMessageKey.USER_ROTATE_TOKEN_SUCCESSFULLY)
    @PostMapping("/rotation")
    public ResponseEntity<Void> rotateToken(
            @CookieValue(TokenType.REFRESH_TOKEN_NAME) String refreshToken
    ) {
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
