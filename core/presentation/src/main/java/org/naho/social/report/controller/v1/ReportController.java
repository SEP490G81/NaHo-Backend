package org.naho.social.report.controller.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.naho.file.constant.FileAccessStatus;
import org.naho.file.constant.FileFolderConstant;
import org.naho.file.exception.FileErrorCode;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.file.port.out.FileValidatorPort;
import org.naho.file.result.StoredFile;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.i18n.message.social.ReportDetailMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.shared.exception.PresentationException;
import org.naho.social.report.command.CreateReportCommand;
import org.naho.social.report.command.GetReportCommand;
import org.naho.social.report.command.UpdateReportStatusCommand;
import org.naho.social.report.dto.mapper.ReportRequestMapper;
import org.naho.social.report.dto.mapper.ReportResponseMapper;
import org.naho.social.report.dto.request.CreateReportRequest;
import org.naho.social.report.dto.request.UpdateReportStatusRequest;
import org.naho.social.report.dto.response.ReportResponse;
import org.naho.social.report.port.in.CreateReportInputPort;
import org.naho.social.report.port.in.GetListReportByUserInputPort;
import org.naho.social.report.port.in.GetReportInputPort;
import org.naho.social.report.port.in.UpdateReportStatusInputPort;
import org.naho.social.report.result.ReportResult;
import org.naho.user.result.AccessTokenPayload;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
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
    private final FileStorageServicePort fileStorageServicePort;

    // ROLE: LEARNER
    @PreAuthorize("hasRole('LEARNER')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponseMessage(message = ReportDetailMessageKey.REPORT_CREATE_SUCCESS)
    public ResponseEntity<ReportResponse> createReport(
            @Valid @ModelAttribute CreateReportRequest request,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @AuthenticationPrincipal AccessTokenPayload payload) {
        if (files == null || files.isEmpty()) {
            throw new PresentationException(
                    FileErrorCode.FILE_NOT_VALID,
                    FileDetailMessageKey.FILE_NOT_VALID);
        }

        List<StoredFile> storedFiles = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                throw new PresentationException(
                        FileErrorCode.FILE_NOT_VALID,
                        FileDetailMessageKey.FILE_EMPTY);
            }

            try {
                fileValidatorPort.validateImageFile(file.getBytes());
                StoredFile storedFile = fileStorageServicePort.saveFileToLocal(file, FileFolderConstant.REPORTS,
                        FileAccessStatus.PRIVATE);
                storedFiles.add(storedFile);
            } catch (IOException e) {
                throw new PresentationException(
                        FileErrorCode.FILE_UPLOAD_FAILED,
                        FileDetailMessageKey.FILE_UPLOAD_FAILED,
                        e.getMessage());
            }
        }

        CreateReportCommand command = reportRequestMapper.requestToCommand(request, payload.userId(), storedFiles);
        ReportResult result = createReportInputPort.createReport(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(reportResponseMapper.resultToResponse(result));
    }

    // ROLE: ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    @ApiResponseMessage(message = ReportDetailMessageKey.REPORT_GET_LIST_ADMIN_SUCCESS)
    public ResponseEntity<List<ReportResponse>> getReportsByAdmin() {
        List<ReportResult> results = getReportInputPort.getReportsByAdmin();
        return ResponseEntity.ok(results.stream()
                .map(reportResponseMapper::resultToResponse)
                .toList());
    }

    // ROLE: CONTENT_MANAGER
    @PreAuthorize("hasRole('CONTENT_MANAGER')")
    @GetMapping("/content-manager")
    @ApiResponseMessage(message = ReportDetailMessageKey.REPORT_GET_LIST_CONTENT_MANAGER_SUCCESS)
    public ResponseEntity<List<ReportResponse>> getReportsByContentManager() {
        List<ReportResult> results = getReportInputPort.getReportsByContentManager();
        return ResponseEntity.ok(results.stream()
                .map(reportResponseMapper::resultToResponse)
                .toList());
    }

    // ROLE: LEARNER
    @PreAuthorize("hasRole('LEARNER')")
    @GetMapping("/user")
    @ApiResponseMessage(message = ReportDetailMessageKey.REPORT_GET_LIST_USER_SUCCESS)
    public ResponseEntity<List<ReportResponse>> getReportsByUser(
            @AuthenticationPrincipal AccessTokenPayload payload) {
        List<ReportResult> results = getListReportByUserInputPort.getReportsByUser(payload.userId());

        List<ReportResponse> responses = results.stream().map(reportResponseMapper::resultToResponse).toList();

        return ResponseEntity.ok(responses);
    }

    // ROLE: ADMIN, CONTENT_MANAGER
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTENT_MANAGER')")
    @PatchMapping("/{id}/status")
    @ApiResponseMessage(message = ReportDetailMessageKey.REPORT_UPDATE_STATUS_SUCCESS)
    public ResponseEntity<ReportResponse> updateReportStatus(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateReportStatusRequest request) {
        ReportResult result = updateReportStatusInputPort.updateStatus(
                new UpdateReportStatusCommand(id, Boolean.TRUE.equals(request.getIsResolved()),
                        request.getAdminReply()));
        return ResponseEntity.ok(reportResponseMapper.resultToResponse(result));
    }

    // ROLE: ADMIN, CONTENT_MANAGER, LEARNER
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTENT_MANAGER', 'LEARNER')")
    @GetMapping("/{id}")
    @ApiResponseMessage(message = ReportDetailMessageKey.REPORT_GET_DETAIL_SUCCESS)
    public ResponseEntity<ReportResponse> getReport(@PathVariable("id") Long id) {
        ReportResult result = getReportInputPort.getReport(new GetReportCommand(id));
        return ResponseEntity.ok(reportResponseMapper.resultToResponse(result));
    }
}
