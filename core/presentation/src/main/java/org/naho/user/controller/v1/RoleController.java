package org.naho.user.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.user.dto.mapper.RoleResponseMapper;
import org.naho.user.dto.response.RoleResponse;
import org.naho.user.port.in.CrudRoleInputPort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/roles")
public class RoleController {
    private final CrudRoleInputPort crudRoleInputPort;
    private final RoleResponseMapper roleResponseMapper;

    // ROLE: USER, ADMIN, CONTENT_MANAGER
    @PreAuthorize("hasAnyRole('LEARNER', 'ADMIN', 'CONTENT_MANAGER')")
    @GetMapping
    public ResponseEntity<List<RoleResponse>> getRoles() {
        List<RoleResponse> responses = crudRoleInputPort.getAllRoles()
                .stream()
                .map(roleResponseMapper::resultToResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }
}
