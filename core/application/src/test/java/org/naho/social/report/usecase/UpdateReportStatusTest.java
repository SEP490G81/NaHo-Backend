package org.naho.social.report.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.email.port.out.EmailPort;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.EventPublisherPort;
import org.naho.social.report.command.UpdateReportStatusCommand;
import org.naho.social.report.exception.ReportErrorCode;
import org.naho.social.report.mapper.ReportResultMapper;
import org.naho.social.report.model.Report;
import org.naho.social.report.port.out.ReportRepositoryPort;
import org.naho.social.report.result.ReportResult;
import org.naho.social.report.type.ReportType;
import org.naho.user.port.out.UserRepositoryPort;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateReportStatusTest {

    @Mock
    private ReportRepositoryPort reportRepositoryPort;
    @Mock
    private ReportResultMapper reportResultMapper;
    @Mock
    private UserRepositoryPort userRepositoryPort;
    @Mock
    private EmailPort emailPort;
    @Mock
    private EventPublisherPort eventPublisherPort;

    @InjectMocks
    private UpdateReportStatusUseCase updateReportStatusUseCase;

    @Test
    @DisplayName("UTCID01 - Cập nhật trạng thái báo cáo thành công")
    void UTCID01_UpdateStatus_Success() {
        UpdateReportStatusCommand command = new UpdateReportStatusCommand(1L, true, "Resolved reply");
        Report existingReport = mock(Report.class);
        Report updatedReport = mock(Report.class);
        Report savedReport = mock(Report.class);
        ReportResult reportResult = mock(ReportResult.class);

        when(existingReport.isResolved()).thenReturn(false);
        when(existingReport.updateStatus(true, "Resolved reply")).thenReturn(updatedReport);
        when(reportRepositoryPort.findById(1L)).thenReturn(Optional.of(existingReport));
        when(reportRepositoryPort.save(updatedReport)).thenReturn(savedReport);
        when(savedReport.getUserId()).thenReturn(10L);
        when(savedReport.getId()).thenReturn(1L);
        when(savedReport.getTitle()).thenReturn("Title");
        when(savedReport.getAdminReply()).thenReturn("Resolved reply");
        when(savedReport.getReportType()).thenReturn(ReportType.SYSTEM);
        when(reportResultMapper.domainToResult(savedReport)).thenReturn(reportResult);

        ReportResult result = updateReportStatusUseCase.updateStatus(command);

        assertNotNull(result);
        verify(eventPublisherPort, times(1)).publish(any());
    }

    @Test
    @DisplayName("UTCID02 - Thất bại do reportId null")
    void UTCID02_UpdateStatus_ReportIdNull() {
        UpdateReportStatusCommand command = new UpdateReportStatusCommand(null, true, "Reply");

        ApplicationException ex = assertThrows(ApplicationException.class, () -> updateReportStatusUseCase.updateStatus(command));
        assertEquals(ReportErrorCode.REPORT_NOT_FOUND, ex.getErrorCode());
    }
}
