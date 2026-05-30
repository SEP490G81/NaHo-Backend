package org.naho.user.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.user.command.RegisterCommand;
import org.naho.user.constant.UserApplicationMessageKey;
import org.naho.user.dto.mapper.RegisterRequestMapper;
import org.naho.user.dto.mapper.RegisterResponseMapper;
import org.naho.user.dto.mapper.UserResponseMapper;
import org.naho.user.dto.request.RegisterRequest;
import org.naho.user.dto.request.UpdateStatusRequest;
import org.naho.user.dto.response.RegisterResponse;
import org.naho.user.dto.response.UserResponse;
import org.naho.user.port.in.GetUserInputPort;
import org.naho.user.port.in.RegisterInputPort;
import org.naho.user.port.in.UpdateUserInputPort;
import org.naho.user.result.RegisterResult;
import org.naho.user.result.UserResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final GetUserInputPort getUserInputPort;
    private final UpdateUserInputPort updateUserInputPort;
    private final UserResponseMapper userResponseMapper;

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

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        UserResult result = getUserInputPort.getUserById(id);
        return ResponseEntity.ok(userResponseMapper.resultToResponse(result));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getListUser(
            @RequestParam(required = false) String userNameOrEmail,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String jlptLevel
    ) {
        List<UserResult> results = getUserInputPort.searchUsers(userNameOrEmail, role, status, jlptLevel);
        return ResponseEntity.ok(results.stream().map(userResponseMapper::resultToResponse).toList());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<UserResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateStatusRequest request
    ) {
        UserResult user = updateUserInputPort.updateStatus(id, request.newStatus());
        UserResponse response = userResponseMapper.resultToResponse(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
