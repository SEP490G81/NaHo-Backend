package org.naho.social.report.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.shared.exception.ApplicationException;
import org.naho.social.report.command.GetReportCommand;
import org.naho.social.report.exception.ReportErrorCode;
import org.naho.social.report.mapper.ReportResultMapper;
import org.naho.social.report.model.Report;
import org.naho.social.report.port.out.ReportRepositoryPort;
import org.naho.social.report.result.ReportResult;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetReportTest {

    @Mock
    private ReportRepositoryPort reportRepositoryPort;
    @Mock
    private ReportResultMapper reportResultMapper;

    @InjectMocks
    private GetReportUseCase getReportUseCase;

    @Test
    @DisplayName("UTCID01 - Lấy chi tiết báo cáo thành công")
    void UTCID01_GetReport_Success() {
        GetReportCommand command = new GetReportCommand(1L);
        Report report = mock(Report.class);
        ReportResult reportResult = mock(ReportResult.class);

        when(reportRepositoryPort.findById(1L)).thenReturn(Optional.of(report));
        when(reportResultMapper.domainToResult(report)).thenReturn(reportResult);

        ReportResult result = getReportUseCase.getReport(command);

        assertNotNull(result);
    }

    @Test
    @DisplayName("UTCID02 - Lấy chi tiết báo cáo thất bại do không tìm thấy")
    void UTCID02_GetReport_NotFound() {
        GetReportCommand command = new GetReportCommand(99L);
        when(reportRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        ApplicationException ex = assertThrows(ApplicationException.class, () -> getReportUseCase.getReport(command));
        assertEquals(ReportErrorCode.REPORT_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    @DisplayName("UTCID03 - Lấy danh sách báo cáo cho Admin")
    void UTCID03_GetReportsByAdmin() {
        Report report = mock(Report.class);
        when(reportRepositoryPort.findByReportTypeIn(any())).thenReturn(List.of(report));

        List<ReportResult> results = getReportUseCase.getReportsByAdmin();

        assertNotNull(results);
        assertEquals(1, results.size());
    }
}
