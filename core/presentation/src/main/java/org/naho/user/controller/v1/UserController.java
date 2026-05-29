package org.naho.user.controller.v1;

import org.naho.user.dto.mapper.UserResponseMapper;
import org.naho.user.dto.request.UpdateStatusRequest;
import org.naho.user.dto.response.UserResponse;
import org.naho.user.port.in.GetUserInputPort;
import org.naho.user.port.in.UpdateUserInputPort;
import org.naho.user.result.UserResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final GetUserInputPort getUserInputPort;
    private final UpdateUserInputPort updateUserInputPort;
    private final UserResponseMapper userResponseMapper;
    public UserController(GetUserInputPort getUserInputPort, UpdateUserInputPort updateUserInputPort, UserResponseMapper userResponseMapper) {
        this.getUserInputPort = getUserInputPort;
        this.updateUserInputPort = updateUserInputPort;
        this.userResponseMapper = userResponseMapper;
    }

    @GetMapping( "/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id){
        UserResult result = getUserInputPort.getUserById(id);
        return ResponseEntity.ok(userResponseMapper.resultToResponse(result));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getListUser(
            @RequestParam(required = false) String userNameOrEmail,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String jlptLevel) {

        List<UserResult> results = getUserInputPort.searchUsers(userNameOrEmail, role, status, jlptLevel);
        return ResponseEntity.ok(results.stream().map(userResponseMapper::resultToResponse).toList());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateStatusRequest request
    ) {
        updateUserInputPort.updateStatus(id, request.newStatus());
        return ResponseEntity.noContent().build();
    }
}
