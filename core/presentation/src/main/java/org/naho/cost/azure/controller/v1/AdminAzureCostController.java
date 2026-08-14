package org.naho.cost.azure.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.cost.azure.dto.mapper.AzureCostRequestMapper;
import org.naho.cost.azure.dto.mapper.AzureCostResponseMapper;
import org.naho.cost.azure.dto.request.AzureCostChartRequest;
import org.naho.cost.azure.dto.response.AzureCostChartResponse;
import org.naho.cost.azure.dto.response.AzureCostSummaryResponse;
import org.naho.cost.command.AzureCostQueryCommand;
import org.naho.cost.port.in.GetAzureCostInputPort;
import org.naho.cost.port.in.SyncAzureCostInputPort;
import org.naho.cost.result.AzureCostChartResult;
import org.naho.cost.result.AzureCostSummaryResult;
import org.naho.i18n.message.speech.SpeechDetailMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/admin/azure-cost")
@RequiredArgsConstructor
public class AdminAzureCostController {

    private final GetAzureCostInputPort getAzureCostInputPort;
    private final SyncAzureCostInputPort syncAzureCostInputPort;
    private final AzureCostRequestMapper azureCostRequestMapper;
    private final AzureCostResponseMapper azureCostResponseMapper;

    @Value("${app.start-date:2026-01-01}")
    private LocalDate appStartDate;

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
    public ResponseEntity<Void> triggerManualFullSync() {
        syncAzureCostInputPort.syncFullBackfill(appStartDate);
        return ResponseEntity.ok().build();
    }
}
