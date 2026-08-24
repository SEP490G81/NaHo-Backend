package org.naho.cost.llm.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.cost.llm.dto.mapper.OpenAiCostRequestMapper;
import org.naho.cost.llm.dto.mapper.OpenAiCostResponseMapper;
import org.naho.cost.llm.dto.request.OpenAiCostChartRequest;
import org.naho.cost.llm.dto.response.OpenAiCostChartResponse;
import org.naho.cost.llm.dto.response.OpenAiCostSummaryResponse;
import org.naho.cost.port.in.SyncOpenAiCostInputPort;
import org.naho.cost.result.OpenAiCostChartResult;
import org.naho.cost.result.OpenAiCostSummaryResult;
import org.naho.speech.llm.conversation.command.OpenAiCostQueryCommand;
import org.naho.speech.llm.conversation.port.in.GetOpenAiCostInputPort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/openai-cost")
@RequiredArgsConstructor
public class AdminOpenAiCostController {

    private final GetOpenAiCostInputPort getOpenAiCostInputPort;
    private final SyncOpenAiCostInputPort syncOpenAiCostInputPort;
    private final OpenAiCostRequestMapper openAiCostRequestMapper;
    private final OpenAiCostResponseMapper openAiCostResponseMapper;

    // ROLE: ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/summary")
    public ResponseEntity<OpenAiCostSummaryResponse> getSummary() {
        OpenAiCostSummaryResult result = getOpenAiCostInputPort.getSummary();
        OpenAiCostSummaryResponse response = openAiCostResponseMapper.resultToSummaryResponse(result);
        return ResponseEntity.ok(response);
    }

    // ROLE: ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/chart")
    public ResponseEntity<OpenAiCostChartResponse> getChartData(OpenAiCostChartRequest request) {
        OpenAiCostQueryCommand command = openAiCostRequestMapper.requestToCommand(request);
        OpenAiCostChartResult result = getOpenAiCostInputPort.getChartData(command);
        OpenAiCostChartResponse response = openAiCostResponseMapper.resultToChartResponse(result);
        return ResponseEntity.ok(response);
    }

    // ROLE: ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/sync")
    public ResponseEntity<Void> triggerManualSync() {
        syncOpenAiCostInputPort.syncIncremental(3);
        return ResponseEntity.ok().build();
    }
}
