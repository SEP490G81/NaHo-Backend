package org.naho.user.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.shared.constant.CookieProperty;
import org.naho.user.command.CredentialsLoginCommand;
import org.naho.user.command.LogoutCommand;
import org.naho.user.constant.UserApplicationMessageKey;
import org.naho.user.dto.mapper.LoginRequestMapper;
import org.naho.user.dto.mapper.LoginResponseMapper;
import org.naho.user.dto.request.CredentialsLoginRequest;
import org.naho.user.dto.response.LoginResponse;
import org.naho.user.port.in.AuthInputPort;
import org.naho.user.result.AccessTokenPayload;
import org.naho.user.result.LoginResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private static final String REFRESH_TOKEN_TYPE = "refresh-token";

    private final AuthInputPort authInputPort;
    private final LoginResponseMapper loginResponseMapper;
    private final LoginRequestMapper loginRequestMapper;
    private final CookieProperty cookieProperty;

    @ApiResponseMessage(message = UserApplicationMessageKey.USER_LOGIN_SUCCESSFULLY)
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> credentialsLogin(
            @RequestBody CredentialsLoginRequest request
    ) {
        CredentialsLoginCommand command = loginRequestMapper.requestToCommand(request);
        LoginResult result = authInputPort.credentialsLogin(command);
        LoginResponse response = loginResponseMapper.resultToResponse(result);

        Duration maxAge = Duration.between(
                Instant.now(),
                result.refreshToken().expiresAt()
        );

        if (maxAge.isNegative()) {
            maxAge = Duration.ZERO;
        }

        ResponseCookie refreshTokenCookie = ResponseCookie
                .from(REFRESH_TOKEN_TYPE, result.refreshToken().value())
                .httpOnly(true)
                .secure(cookieProperty.isSecure())
                .sameSite("Lax")
                .path("/")
                .maxAge(maxAge)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(response);
    }

    @ApiResponseMessage(message = UserApplicationMessageKey.USER_LOGOUT_SUCCESSFULLY)
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AccessTokenPayload payload = (AccessTokenPayload) authentication.getPrincipal();

        authInputPort.logout(new LogoutCommand(
                payload.userId(),
                payload.userSessionId()
        ));

        ResponseCookie clearCookie = ResponseCookie
                .from(REFRESH_TOKEN_TYPE, "")
                .httpOnly(true)
                .secure(cookieProperty.isSecure())
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, clearCookie.toString())
                .build();
    }
}
