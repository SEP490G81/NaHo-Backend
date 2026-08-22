package org.naho.social.report.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.shared.exception.ApplicationException;
import org.naho.social.report.mapper.ReportResultMapper;
import org.naho.social.report.model.Report;
import org.naho.social.report.port.out.ReportRepositoryPort;
import org.naho.social.report.result.ReportResult;
import org.naho.user.exception.UserErrorCode;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetListReportByUserTest {

    @Mock
    private ReportRepositoryPort reportRepositoryPort;
    @Mock
    private ReportResultMapper reportResultMapper;

    @InjectMocks
    private GetListReportByUserUseCase getListReportByUserUseCase;

    @Test
    @DisplayName("UTCID01 - Lấy danh sách báo cáo theo userId thành công")
    void UTCID01_GetReportsByUser_Success() {
        Report report = mock(Report.class);
        ReportResult reportResult = mock(ReportResult.class);

        when(reportRepositoryPort.findAllByUserId(1L)).thenReturn(List.of(report));
        when(reportResultMapper.domainToResult(any())).thenReturn(reportResult);

        List<ReportResult> results = getListReportByUserUseCase.getReportsByUser(1L);

        assertNotNull(results);
        assertEquals(1, results.size());
    }

    @Test
    @DisplayName("UTCID02 - Thất bại do userId null")
    void UTCID02_GetReportsByUser_UserIdNull() {
        ApplicationException ex = assertThrows(ApplicationException.class, () -> getListReportByUserUseCase.getReportsByUser(null));
        assertEquals(UserErrorCode.USER_NOT_FOUND, ex.getErrorCode());
    }
}
