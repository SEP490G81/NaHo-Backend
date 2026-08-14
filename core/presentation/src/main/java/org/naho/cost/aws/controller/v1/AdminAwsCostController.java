package org.naho.cost.aws.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.cost.aws.dto.mapper.AwsCostRequestMapper;
import org.naho.cost.aws.dto.mapper.AwsCostResponseMapper;
import org.naho.cost.aws.dto.request.AwsCostChartRequest;
import org.naho.cost.aws.dto.response.AwsCostChartResponse;
import org.naho.cost.aws.dto.response.AwsCostSummaryResponse;
import org.naho.cost.command.AwsCostQueryCommand;
import org.naho.cost.port.in.GetAwsCostInputPort;
import org.naho.cost.result.AwsCostChartResult;
import org.naho.cost.result.AwsCostSummaryResult;
import org.naho.i18n.message.speech.SpeechDetailMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/aws-cost")
@RequiredArgsConstructor
public class AdminAwsCostController {

    private final GetAwsCostInputPort getAwsCostInputPort;
    private final AwsCostRequestMapper awsCostRequestMapper;
    private final AwsCostResponseMapper awsCostResponseMapper;

    @GetMapping("/summary")
    @ApiResponseMessage(message = SpeechDetailMessageKey.AWS_COST_SUMMARY_GET_SUCCESS)
    public ResponseEntity<AwsCostSummaryResponse> getSummary() {
        AwsCostSummaryResult result = getAwsCostInputPort.getSummary();
        AwsCostSummaryResponse response = awsCostResponseMapper.resultToSummaryResponse(result);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/chart")
    @ApiResponseMessage(message = SpeechDetailMessageKey.AWS_COST_CHART_GET_SUCCESS)
    public ResponseEntity<AwsCostChartResponse> getChartData(AwsCostChartRequest request) {
        AwsCostQueryCommand command = awsCostRequestMapper.requestToCommand(request);
        AwsCostChartResult result = getAwsCostInputPort.getChartData(command);
        AwsCostChartResponse response = awsCostResponseMapper.resultToChartResponse(result);
        return ResponseEntity.ok(response);
    }
}
