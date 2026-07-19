package org.naho.chest.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.chest.command.OpenChestCommand;
import org.naho.chest.dto.mapper.ChestRequestMapper;
import org.naho.chest.dto.request.OpenChestRequest;
import org.naho.chest.port.in.OpenChestInputPort;
import org.naho.i18n.message.chest.ChestDetailMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/chests")
@RequiredArgsConstructor
public class ChestController {

    private final OpenChestInputPort openChestInputPort;
    private final ChestRequestMapper chestRequestMapper;

    @ApiResponseMessage(message = ChestDetailMessageKey.CHEST_OPEN_SUCCESS)
    @PostMapping("/open")
    public ResponseEntity<Void> openChest(@RequestBody OpenChestRequest request) {
        OpenChestCommand command = chestRequestMapper.requestToCommand(request);
        openChestInputPort.openChest(command);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
