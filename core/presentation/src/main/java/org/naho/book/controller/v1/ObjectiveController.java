package org.naho.book.controller.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.naho.book.command.GetObjectiveDetailCommand;
import org.naho.book.dto.mapper.ObjectiveRequestMapper;
import org.naho.book.dto.mapper.ObjectiveResponseMapper;
import org.naho.book.dto.request.UpdateObjectiveRequest;
import org.naho.book.dto.response.ObjectiveDetailResponse;
import org.naho.book.dto.response.ObjectiveResponse;
import org.naho.book.port.in.GetObjectiveDetailInputPort;
import org.naho.book.port.in.UpdateObjectiveInputPort;
import org.naho.i18n.message.book.ObjectiveDetailMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.user.port.out.RoleRepositoryPort;
import org.naho.user.result.AccessTokenPayload;
import org.naho.user.type.RoleName;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/objectives")
@RequiredArgsConstructor
public class ObjectiveController {

    private final GetObjectiveDetailInputPort getObjectiveDetailInputPort;
    private final ObjectiveResponseMapper objectiveResponseMapper;

    private final RoleRepositoryPort roleRepositoryPort;
    private final UpdateObjectiveInputPort updateObjectiveInputPort;
    private final ObjectiveRequestMapper objectiveRequestMapper;

    // ROLE: ADMIN, CONTENT_MANAGER, LEARNER
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTENT_MANAGER', 'LEARNER')")
    @GetMapping("/{id}")
    @ApiResponseMessage(message = ObjectiveDetailMessageKey.OBJECTIVE_GET_DETAIL_SUCCESS)
    public ResponseEntity<ObjectiveDetailResponse> getObjectiveDetail(
            @PathVariable("id") Long id
    ) {
        var command = new GetObjectiveDetailCommand(id);
        var result = getObjectiveDetailInputPort.getObjectiveDetail(command);
        var response = objectiveResponseMapper.detailResultToResponse(result);
        return ResponseEntity.ok(response);
    }

    // ROLE: CONTENT_MANAGER
    @PreAuthorize("hasRole('CONTENT_MANAGER')")
    @PutMapping("/{id}")
    @ApiResponseMessage(message = ObjectiveDetailMessageKey.OBJECTIVE_UPDATE_SUCCESS)
    public ResponseEntity<ObjectiveResponse> updateObjective(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateObjectiveRequest request,
            @AuthenticationPrincipal AccessTokenPayload payload
    ) {
        List<String> roleSet = roleRepositoryPort.findRoleNamesByUserId(payload.userId());
        boolean isAdminOrManager =
                roleSet.contains(RoleName.ADMIN.name()) ||
                        roleSet.contains(RoleName.CONTENT_MANAGER.name());

        var command = objectiveRequestMapper.toUpdateCommand(request, id, payload.userId(), isAdminOrManager);
        var result = updateObjectiveInputPort.updateObjective(command);
        var response = objectiveResponseMapper.listItemResultToResponse(result);
        return ResponseEntity.ok(response);
    }
}
