package org.naho.social.report.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.file.port.in.UploadFileInputPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.shared.port.out.EventPublisherPort;
import org.naho.shared.port.out.TransactionPort;
import org.naho.social.report.command.CreateReportCommand;
import org.naho.social.report.mapper.ReportResultMapper;
import org.naho.social.report.model.Report;
import org.naho.social.report.port.out.ReportRepositoryPort;
import org.naho.social.report.result.ReportResult;
import org.naho.social.report.type.ReportType;
import org.naho.user.port.out.UserRepositoryPort;

import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateReportTest {

    @Mock
    private ReportRepositoryPort reportRepositoryPort;
    @Mock
    private FileStorageServicePort fileStorageServicePort;
    @Mock
    private FileRepositoryPort fileRepositoryPort;
    @Mock
    private ReportResultMapper reportResultMapper;
    @Mock
    private UploadFileInputPort uploadFileInputPort;
    @Mock
    private TransactionPort transactionPort;
    @Mock
    private EventPublisherPort eventPublisherPort;
    @Mock
    private UserRepositoryPort userRepositoryPort;

    @InjectMocks
    private CreateReportUseCase createReportUseCase;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        lenient().when(transactionPort.execute(any(Supplier.class))).thenAnswer(inv -> {
            Supplier<?> s = inv.getArgument(0);
            return s.get();
        });
    }

    @Test
    @DisplayName("UTCID01 - Tạo báo cáo thành công")
    void UTCID01_CreateReport_Success() {
        CreateReportCommand command = mock(CreateReportCommand.class);
        Report report = mock(Report.class);
        ReportResult reportResult = mock(ReportResult.class);

        when(command.imageFiles()).thenReturn(List.of());
        when(command.userId()).thenReturn(1L);
        when(reportResultMapper.commandToDomain(command)).thenReturn(report);
        when(reportRepositoryPort.save(report)).thenReturn(report);
        when(report.getReportType()).thenReturn(ReportType.QUESTION);
        when(userRepositoryPort.findByFilters(null, "ADMIN", null)).thenReturn(List.of());
        when(reportResultMapper.domainToResult(report)).thenReturn(reportResult);

        ReportResult result = createReportUseCase.createReport(command);

        assertNotNull(result);
        verify(eventPublisherPort, times(1)).publish(any());
    }
}
