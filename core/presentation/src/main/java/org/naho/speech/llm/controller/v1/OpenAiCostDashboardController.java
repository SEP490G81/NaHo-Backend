package org.naho.speech.llm.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.speech.llm.dto.response.OpenAiCostStatResponse;
import org.naho.speech.llm.port.in.GetOpenAiCostInputPort;
import org.naho.speech.llm.result.OpenAiCostStatResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/v1/admin/dashboard/openai-cost")
@RequiredArgsConstructor
public class OpenAiCostDashboardController {

    private final GetOpenAiCostInputPort getOpenAiCostInputPort;

    /**
     * Get OpenAI costs.
     * - If yearMonth is provided (e.g. ?yearMonth=2026-08): returns monthly stat for that month.
     * - If all=true (e.g. ?all=true): returns all historical daily costs from DB.
     * - Default: returns monthly stat for current month.
     */
    @GetMapping
    public ResponseEntity<OpenAiCostStatResponse> getCostStat(
            @RequestParam(name = "yearMonth", required = false) String yearMonthStr,
            @RequestParam(name = "all", required = false, defaultValue = "false") boolean all) {

        if (all) {
            OpenAiCostStatResult result = getOpenAiCostInputPort.getAllDailyCosts();
            return ResponseEntity.ok(OpenAiCostStatResponse.fromResult(result));
        }

        YearMonth yearMonth;
        if (yearMonthStr != null && !yearMonthStr.isBlank()) {
            yearMonth = YearMonth.parse(yearMonthStr);
        } else {
            yearMonth = YearMonth.now();
        }

        OpenAiCostStatResult result = getOpenAiCostInputPort.getMonthlyCostStat(yearMonth);
        return ResponseEntity.ok(OpenAiCostStatResponse.fromResult(result));
    }

    /**
     * Explicit API endpoint for getting all historical daily costs from DB.
     */
    @GetMapping("/all-daily")
    public ResponseEntity<OpenAiCostStatResponse> getAllDailyCosts() {
        OpenAiCostStatResult result = getOpenAiCostInputPort.getAllDailyCosts();
        return ResponseEntity.ok(OpenAiCostStatResponse.fromResult(result));
    }

    /**
     * Explicit API endpoint for getting monthly cost stats.
     */
    @GetMapping("/monthly")
    public ResponseEntity<OpenAiCostStatResponse> getMonthlyCostStat(
            @RequestParam(name = "yearMonth", required = false) String yearMonthStr) {

        YearMonth yearMonth;
        if (yearMonthStr != null && !yearMonthStr.isBlank()) {
            yearMonth = YearMonth.parse(yearMonthStr);
        } else {
            yearMonth = YearMonth.now();
        }

        OpenAiCostStatResult result = getOpenAiCostInputPort.getMonthlyCostStat(yearMonth);
        return ResponseEntity.ok(OpenAiCostStatResponse.fromResult(result));
    }
}
