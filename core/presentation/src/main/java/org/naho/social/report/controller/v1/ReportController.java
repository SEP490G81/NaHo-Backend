package org.naho.social.report.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.social.report.command.GetReportCommand;
import org.naho.social.report.dto.mapper.ReportResponseMapper;
import org.naho.social.report.dto.response.ReportResponse;
import org.naho.social.report.port.in.GetReportInputPort;
import org.naho.social.report.result.ReportResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final GetReportInputPort getReportInputPort;
    private final ReportResponseMapper reportResponseMapper;

    @GetMapping("/admin")
    public ResponseEntity<List<ReportResponse>> getReportsByAdmin() {
        List<ReportResult> results = getReportInputPort.getReportsByAdmin();
        return ResponseEntity.ok(results.stream()
                .map(reportResponseMapper::resultToResponse)
                .toList());
    }

    @GetMapping("/content-manager")
    public ResponseEntity<List<ReportResponse>> getReportsByContentManager() {
        List<ReportResult> results = getReportInputPort.getReportsByContentManager();
        return ResponseEntity.ok(results.stream()
                .map(reportResponseMapper::resultToResponse)
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportResponse> getReport(@PathVariable("id") Long id) {
        ReportResult result = getReportInputPort.getReport(new GetReportCommand(id));
        return ResponseEntity.ok(reportResponseMapper.resultToResponse(result));
    }
}
