package org.naho.social.report.controller.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.naho.file.port.out.FileValidatorPort;
import org.naho.i18n.message.social.ReportDetailMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.shared.exception.PresentationException;
import org.naho.social.report.command.CreateReportCommand;
import org.naho.social.report.command.GetReportCommand;
import org.naho.social.report.command.GetReportsByUserCommand;
import org.naho.social.report.command.UpdateReportStatusCommand;
import org.naho.social.report.dto.mapper.ReportRequestMapper;
import org.naho.social.report.dto.mapper.ReportResponseMapper;
import org.naho.social.report.dto.request.CreateReportRequest;
import org.naho.social.report.dto.request.UpdateReportStatusRequest;
import org.naho.social.report.dto.response.ReportResponse;
import org.naho.social.report.exception.ReportErrorCode;
import org.naho.social.report.port.in.CreateReportInputPort;
import org.naho.social.report.port.in.GetListReportByUserInputPort;
import org.naho.social.report.port.in.GetReportInputPort;
import org.naho.social.report.port.in.UpdateReportStatusInputPort;
import org.naho.social.report.result.ReportResult;
import org.naho.user.result.AccessTokenPayload;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final GetReportInputPort getReportInputPort;
    private final CreateReportInputPort createReportInputPort;
    private final GetListReportByUserInputPort getListReportByUserInputPort;
    private final UpdateReportStatusInputPort updateReportStatusInputPort;
    private final ReportResponseMapper reportResponseMapper;
    private final ReportRequestMapper reportRequestMapper;
    private final FileValidatorPort fileValidatorPort;

    @PostMapping(value = { "", "/" }, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponseMessage(message = ReportDetailMessageKey.REPORT_CREATE_SUCCESS)
    public ResponseEntity<ReportResponse> createReport(
            @Valid @ModelAttribute CreateReportRequest request,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @AuthenticationPrincipal AccessTokenPayload payload) {
        Long userId = payload != null ? payload.userId() : null;

        List<MultipartFile> uploadFiles = (files != null && !files.isEmpty())
                ? files
                : request.getFiles();

        if (uploadFiles != null) {
            for (MultipartFile file : uploadFiles) {
                if (file != null && !file.isEmpty()) {
                    try {
                        fileValidatorPort.validateImageFile(file.getInputStream());
                    } catch (IOException e) {
                        throw new PresentationException(
                                ReportErrorCode.REPORT_NOT_FOUND,
                                ReportDetailMessageKey.REPORT_TITLE_BLANK,
                                e.getMessage());
                    }
                }
            }
        }

        CreateReportCommand command = reportRequestMapper.requestToCommand(request, userId, uploadFiles);
        ReportResult result = createReportInputPort.createReport(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(reportResponseMapper.resultToResponse(result));
    }

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

    @GetMapping("/user")
    @ApiResponseMessage(message = ReportDetailMessageKey.REPORT_GET_LIST_USER_SUCCESS)
    public ResponseEntity<List<ReportResponse>> getReportsByUser(
            @AuthenticationPrincipal AccessTokenPayload payload) {
        Long userId = payload != null ? payload.userId() : null;
        List<ReportResult> results = getListReportByUserInputPort.getReportsByUser(new GetReportsByUserCommand(userId));
        return ResponseEntity.ok(results.stream()
                .map(reportResponseMapper::resultToResponse)
                .toList());
    }

    @PatchMapping("/{id}/status")
    @ApiResponseMessage(message = ReportDetailMessageKey.REPORT_UPDATE_STATUS_SUCCESS)
    public ResponseEntity<ReportResponse> updateReportStatus(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateReportStatusRequest request) {
        ReportResult result = updateReportStatusInputPort.updateStatus(
                new UpdateReportStatusCommand(id, Boolean.TRUE.equals(request.getIsResolved()))
        );
        return ResponseEntity.ok(reportResponseMapper.resultToResponse(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportResponse> getReport(@PathVariable("id") Long id) {
        ReportResult result = getReportInputPort.getReport(new GetReportCommand(id));
        return ResponseEntity.ok(reportResponseMapper.resultToResponse(result));
    }
}
