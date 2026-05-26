package org.naho.user.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.user.command.CredentialsLoginCommand;
import org.naho.user.constant.UserApplicationMessageKey;
import org.naho.user.dto.mapper.LoginResponseMapper;
import org.naho.user.dto.request.CredentialsLoginRequest;
import org.naho.user.dto.response.LoginResponse;
import org.naho.user.port.in.AuthPort;
import org.naho.user.result.LoginResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthPort authPort;
    private final LoginResponseMapper loginResponseMapper;

    @ApiResponseMessage(message = UserApplicationMessageKey.USER_LOGIN_SUCCESSFULLY)
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> credentialsLogin(
            @RequestBody CredentialsLoginRequest request
    ) {
        CredentialsLoginCommand command = new CredentialsLoginCommand(
                request.username(),
                request.rawPassword()
        );
        LoginResult result = authPort.credentialsLogin(command);
        LoginResponse response = loginResponseMapper.resultToResponse(result);
        return ResponseEntity.ok(response);
    }
}
