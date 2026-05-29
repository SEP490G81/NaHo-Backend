package org.naho.user.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.user.dto.response.RoleResponse;
import org.naho.user.port.in.GetRoleInputPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/roles")
public class RoleController {
    private final GetRoleInputPort getRoleInputPort;

    @GetMapping
    public ResponseEntity<List<RoleResponse>> getRoles() {
        List<RoleResponse> responses = getRoleInputPort.getAllRoles().stream()
                .map(role -> new RoleResponse(role.roleName().name(), role.roleName().name(), role.description()))
                .toList();
        return ResponseEntity.ok(responses);
    }
}
