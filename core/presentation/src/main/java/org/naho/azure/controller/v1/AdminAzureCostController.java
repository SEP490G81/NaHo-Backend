package org.naho.azure.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.azure.dto.mapper.AzureCostRequestMapper;
import org.naho.azure.dto.mapper.AzureCostResponseMapper;
import org.naho.azure.dto.request.AzureCostChartRequest;
import org.naho.azure.dto.response.AzureCostChartResponse;
import org.naho.azure.dto.response.AzureCostSummaryResponse;
import org.naho.i18n.message.speech.SpeechDetailMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.speech.azure.command.AzureCostQueryCommand;
import org.naho.speech.azure.port.in.GetAzureCostInputPort;
import org.naho.speech.azure.port.in.SyncAzureCostInputPort;
import org.naho.speech.azure.result.AzureCostChartResult;
import org.naho.speech.azure.result.AzureCostSummaryResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/azure-cost")
@RequiredArgsConstructor
public class AdminAzureCostController {

    private final GetAzureCostInputPort getAzureCostInputPort;
    private final SyncAzureCostInputPort syncAzureCostInputPort;
    private final AzureCostRequestMapper azureCostRequestMapper;
    private final AzureCostResponseMapper azureCostResponseMapper;

    @GetMapping("/summary")
    @ApiResponseMessage(message = SpeechDetailMessageKey.AZURE_COST_SUMMARY_GET_SUCCESS)
    public ResponseEntity<AzureCostSummaryResponse> getSummary() {
        AzureCostSummaryResult result = getAzureCostInputPort.getSummary();
        AzureCostSummaryResponse response = azureCostResponseMapper.resultToSummaryResponse(result);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/chart")
    @ApiResponseMessage(message = SpeechDetailMessageKey.AZURE_COST_CHART_GET_SUCCESS)
    public ResponseEntity<AzureCostChartResponse> getChartData(AzureCostChartRequest request) {
        AzureCostQueryCommand command = azureCostRequestMapper.requestToCommand(request);
        AzureCostChartResult result = getAzureCostInputPort.getChartData(command);
        AzureCostChartResponse response = azureCostResponseMapper.resultToChartResponse(result);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sync")
    public ResponseEntity<Void> triggerManualSync() {
        syncAzureCostInputPort.syncIncremental(3);
        return ResponseEntity.ok().build();
    }
}
