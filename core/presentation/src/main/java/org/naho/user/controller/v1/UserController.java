package org.naho.user.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.user.command.RegisterCommand;
import org.naho.user.constant.UserApplicationMessageKey;
import org.naho.user.dto.mapper.RegisterRequestMapper;
import org.naho.user.dto.mapper.RegisterResponseMapper;
import org.naho.user.dto.request.RegisterRequest;
import org.naho.user.dto.response.RegisterResponse;
import org.naho.user.port.in.RegisterInputPort;
import org.naho.user.result.RegisterResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final RegisterInputPort registerInputPort;
    private final RegisterResponseMapper registerResponseMapper;
    private final RegisterRequestMapper registerRequestMapper;

    @PostMapping("/register")
    @ApiResponseMessage(message = UserApplicationMessageKey.USER_REGISTER_SUCCESSFULLY)
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {

        RegisterCommand command = registerRequestMapper.requestToCommand(request);
        RegisterResult result = registerInputPort.register(command);
        RegisterResponse response = registerResponseMapper.resultToResponse(result);

        return ResponseEntity.ok(response);
    }
}
