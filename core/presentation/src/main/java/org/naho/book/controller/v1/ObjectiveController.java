package org.naho.book.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.book.command.GetObjectiveDetailCommand;
import org.naho.book.dto.mapper.ObjectiveResponseMapper;
import org.naho.book.dto.response.ObjectiveDetailResponse;
import org.naho.book.port.in.GetObjectiveDetailInputPort;
import org.naho.i18n.message.book.ObjectiveDetailMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/objectives")
@RequiredArgsConstructor
public class ObjectiveController {

    private final GetObjectiveDetailInputPort getObjectiveDetailInputPort;
    private final ObjectiveResponseMapper objectiveResponseMapper;

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
}
