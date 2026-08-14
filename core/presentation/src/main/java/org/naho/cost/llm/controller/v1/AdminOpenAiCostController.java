package org.naho.cost.llm.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.cost.llm.dto.mapper.OpenAiCostRequestMapper;
import org.naho.cost.llm.dto.mapper.OpenAiCostResponseMapper;
import org.naho.cost.llm.dto.request.OpenAiCostChartRequest;
import org.naho.cost.llm.dto.response.OpenAiCostChartResponse;
import org.naho.cost.llm.dto.response.OpenAiCostSummaryResponse;
import org.naho.speech.llm.command.OpenAiCostQueryCommand;
import org.naho.speech.llm.port.in.GetOpenAiCostInputPort;
import org.naho.speech.llm.port.in.SyncOpenAiCostInputPort;
import org.naho.speech.llm.result.OpenAiCostChartResult;
import org.naho.speech.llm.result.OpenAiCostSummaryResult;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/summary")
    public ResponseEntity<OpenAiCostSummaryResponse> getSummary() {
        OpenAiCostSummaryResult result = getOpenAiCostInputPort.getSummary();
        OpenAiCostSummaryResponse response = openAiCostResponseMapper.resultToSummaryResponse(result);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/chart")
    public ResponseEntity<OpenAiCostChartResponse> getChartData(OpenAiCostChartRequest request) {
        OpenAiCostQueryCommand command = openAiCostRequestMapper.requestToCommand(request);
        OpenAiCostChartResult result = getOpenAiCostInputPort.getChartData(command);
        OpenAiCostChartResponse response = openAiCostResponseMapper.resultToChartResponse(result);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sync")
    public ResponseEntity<Void> triggerManualSync() {
        syncOpenAiCostInputPort.syncIncremental(3);
        return ResponseEntity.ok().build();
    }
}
