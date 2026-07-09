package org.naho.point.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.point.PointHistoryDetailMessageKey;
import org.naho.point.command.PointHistoryCommand;
import org.naho.point.command.PointHistoryQueryCommand;
import org.naho.point.dto.mapper.PointHistoryRequestMapper;
import org.naho.point.dto.mapper.PointHistoryResponseMapper;
import org.naho.point.dto.request.PointHistoryQueryRequest;
import org.naho.point.dto.request.PointHistoryRequest;
import org.naho.point.dto.response.PointHistoryResponse;
import org.naho.point.port.in.CrudPointHistoryInputPort;
import org.naho.point.result.PointHistoryResult;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.user.result.AccessTokenPayload;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/point-history")
@RequiredArgsConstructor
public class PointHistoryController {
    private final CrudPointHistoryInputPort crudPointHistoryInputPort;
    private final PointHistoryRequestMapper pointHistoryRequestMapper;
    private final PointHistoryResponseMapper pointHistoryResponseMapper;

    @ApiResponseMessage(message = PointHistoryDetailMessageKey.POINT_HISTORY_CREATE_SUCCESS)
    @PostMapping
    public ResponseEntity<PointHistoryResponse> createPointHistory(
            @RequestBody PointHistoryRequest pointHistoryRequest
    ) {
        PointHistoryCommand command = pointHistoryRequestMapper.requestToCommand(pointHistoryRequest);
        PointHistoryResult result = crudPointHistoryInputPort.createPointHistory(command);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(pointHistoryResponseMapper.resultToResponse(result));
    }

    @ApiResponseMessage(message = PointHistoryDetailMessageKey.POINT_HISTORY_GET_ALL_SUCCESS)
    @PostMapping("/all")
    public ResponseEntity<List<PointHistoryResponse>> findAllByUserId(
            @AuthenticationPrincipal AccessTokenPayload payload,
            @RequestBody PointHistoryQueryRequest request
    ) {
        PointHistoryQueryCommand command = pointHistoryRequestMapper.requestToCommand(request);

        List<PointHistoryResult> results =
                crudPointHistoryInputPort.findAllByUserId(command, payload.userId());

        return ResponseEntity.ok(
                results.stream()
                        .map(pointHistoryResponseMapper::resultToResponse)
                        .toList()
        );
    }
}
