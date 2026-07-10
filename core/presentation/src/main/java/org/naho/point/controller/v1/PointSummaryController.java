package org.naho.point.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.point.PointSummaryDetailMessageKey;
import org.naho.point.command.PointSummaryCommand;
import org.naho.point.dto.mapper.PointSummaryRequestMapper;
import org.naho.point.dto.mapper.PointSummaryResponseMapper;
import org.naho.point.dto.request.PointSummaryRequest;
import org.naho.point.dto.response.PointSummaryResponse;
import org.naho.point.port.in.CrudPointSummaryInputPort;
import org.naho.point.result.PointSummaryResult;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.user.result.AccessTokenPayload;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/point-summary")
@RequiredArgsConstructor
public class PointSummaryController {
    private final CrudPointSummaryInputPort crudPointSummaryInputPort;
    private final PointSummaryRequestMapper pointSummaryRequestMapper;
    private final PointSummaryResponseMapper pointSummaryResponseMapper;

    @ApiResponseMessage(message = PointSummaryDetailMessageKey.POINT_SUMMARY_GET_SUCCESS)
    @GetMapping
    public ResponseEntity<PointSummaryResponse> findPointSummaryByUserId(
            @AuthenticationPrincipal AccessTokenPayload payload
    ) {
        Long userId = payload.userId();
        PointSummaryResult result = crudPointSummaryInputPort.findPointSummaryByUserId(userId);
        return ResponseEntity.ok(pointSummaryResponseMapper.resultToResponse(result));
    }

    @ApiResponseMessage(message = PointSummaryDetailMessageKey.POINT_SUMMARY_UPDATE_SUCCESS)
    @PutMapping("/total-point")
    public ResponseEntity<PointSummaryResponse> updateTotalPoint(@RequestBody PointSummaryRequest request) {
        PointSummaryCommand command = pointSummaryRequestMapper.requestToCommand(request);
        PointSummaryResult result = crudPointSummaryInputPort.updateTotalPoint(command);
        return ResponseEntity.ok(pointSummaryResponseMapper.resultToResponse(result));
    }

    @ApiResponseMessage(message = PointSummaryDetailMessageKey.POINT_SUMMARY_ADD_SUCCESS)
    @PatchMapping("/total-point")
    public ResponseEntity<PointSummaryResponse> addPoint(@RequestBody PointSummaryRequest request) {
        PointSummaryCommand command = pointSummaryRequestMapper.requestToCommand(request);
        PointSummaryResult result = crudPointSummaryInputPort.addPoint(command);
        return ResponseEntity.ok(pointSummaryResponseMapper.resultToResponse(result));
    }
}
