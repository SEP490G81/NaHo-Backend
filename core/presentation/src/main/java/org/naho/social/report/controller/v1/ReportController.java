package org.naho.social.report.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.social.report.command.GetReportCommand;
import org.naho.social.report.dto.mapper.ReportResponseMapper;
import org.naho.social.report.dto.response.ReportResponse;
import org.naho.social.report.port.in.GetListReportByAdminInputPort;
import org.naho.social.report.port.in.GetListReportByContentManagerInputPort;
import org.naho.social.report.port.in.GetReportInputPort;
import org.naho.social.report.result.ReportResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final GetReportInputPort getReportInputPort;
    private final GetListReportByAdminInputPort getListReportByAdminInputPort;
    private final GetListReportByContentManagerInputPort getListReportByContentManagerInputPort;
    private final ReportResponseMapper reportResponseMapper;

    @GetMapping("/admin")
    public ResponseEntity<List<ReportResponse>> getReportsByAdmin() {
        List<ReportResult> results = getListReportByAdminInputPort.getReportsByAdmin();
        return ResponseEntity.ok(results.stream()
                .map(reportResponseMapper::resultToResponse)
                .toList());
    }

    @GetMapping("/content-manager")
    public ResponseEntity<List<ReportResponse>> getReportsByContentManager() {
        List<ReportResult> results = getListReportByContentManagerInputPort.getReportsByContentManager();
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
