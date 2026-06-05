package org.naho.user.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.shared.constant.CookieProperty;
import org.naho.user.command.CredentialsLoginCommand;
import org.naho.user.command.LogoutCommand;
import org.naho.user.dto.mapper.LoginRequestMapper;
import org.naho.user.dto.mapper.UserResponseMapper;
import org.naho.user.dto.request.CredentialsLoginRequest;
import org.naho.user.dto.response.UserResponse;
import org.naho.user.helper.AuthControllerHelper;
import org.naho.user.port.in.AuthInputPort;
import org.naho.user.result.AccessTokenPayload;
import org.naho.user.result.LoginResult;
import org.naho.user.result.UserResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    public static final String REFRESH_TOKEN_TYPE = "refresh-token";

    private final AuthInputPort authInputPort;
    private final LoginRequestMapper loginRequestMapper;
    private final CookieProperty cookieProperty;
    private final AuthControllerHelper authControllerHelper;
    private final UserResponseMapper userResponseMapper;

    @ApiResponseMessage(message = UserDetailMessageKey.USER_GET_SUCCESSFULLY)
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentLoggedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AccessTokenPayload payload = (AccessTokenPayload) authentication.getPrincipal();

        UserResult result = authInputPort.findUserById(payload.userId());
        UserResponse response = userResponseMapper.resultToResponse(result);
        
        return ResponseEntity.ok(response);
    }

    @ApiResponseMessage(message = UserDetailMessageKey.USER_LOGIN_SUCCESSFULLY)
    @PostMapping("/login")
    public ResponseEntity<Void> credentialsLogin(
            @RequestBody CredentialsLoginRequest request
    ) {
        CredentialsLoginCommand command = loginRequestMapper.requestToCommand(request);
        LoginResult result = authInputPort.credentialsLogin(command);
        return authControllerHelper.attachTokensToResponseHeader(result);
    }

    @ApiResponseMessage(message = UserDetailMessageKey.USER_LOGOUT_SUCCESSFULLY)
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

    @ApiResponseMessage(message = UserDetailMessageKey.USER_ROTATE_TOKEN_SUCCESSFULLY)
    @PostMapping("/rotation")
    public ResponseEntity<Void> rotateToken(
            @CookieValue(REFRESH_TOKEN_TYPE) String refreshToken
    ) {
        LoginResult result = authInputPort.rotateToken(refreshToken);
        return authControllerHelper.attachTokensToResponseHeader(result);
    }

}
