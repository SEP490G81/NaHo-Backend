package org.naho.chest.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.chest.command.OpenChestCommand;
import org.naho.chest.dto.mapper.ChestResponseMapper;
import org.naho.chest.dto.request.OpenChestRequest;
import org.naho.chest.dto.response.OpenChestResponse;
import org.naho.chest.port.in.OpenChestInputPort;
import org.naho.chest.result.OpenChestResult;
import org.naho.i18n.message.chest.ChestDetailMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.user.result.AccessTokenPayload;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/chests")
@RequiredArgsConstructor
public class ChestController {

    private final OpenChestInputPort openChestInputPort;
    private final ChestResponseMapper chestResponseMapper;

    // ROLE: LEARNER
    @PreAuthorize("hasRole('LEARNER')")
    @ApiResponseMessage(message = ChestDetailMessageKey.CHEST_OPEN_SUCCESS)
    @PostMapping("/open")
    public ResponseEntity<OpenChestResponse> openChest(
            @RequestBody OpenChestRequest openChestRequest,
            @AuthenticationPrincipal AccessTokenPayload payload
    ) {
        OpenChestCommand command = new OpenChestCommand(
                openChestRequest.learningPathNodeId(),
                payload.userId()
        );

        OpenChestResult result = openChestInputPort.openChest(command);
        OpenChestResponse response = chestResponseMapper.resultToResponse(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
