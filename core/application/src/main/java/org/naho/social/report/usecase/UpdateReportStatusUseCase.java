package org.naho.social.report.usecase;

import org.naho.i18n.message.social.ReportDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.social.report.command.UpdateReportStatusCommand;
import org.naho.social.report.exception.ReportErrorCode;
import org.naho.social.report.mapper.ReportResultMapper;
import org.naho.social.report.model.Report;
import org.naho.social.report.port.in.UpdateReportStatusInputPort;
import org.naho.social.report.port.out.ReportRepositoryPort;
import org.naho.social.report.result.ReportResult;

public class UpdateReportStatusUseCase implements UpdateReportStatusInputPort {

    private final ReportRepositoryPort reportRepositoryPort;
    private final ReportResultMapper reportResultMapper;

    public UpdateReportStatusUseCase(
            ReportRepositoryPort reportRepositoryPort,
            ReportResultMapper reportResultMapper
    ) {
        this.reportRepositoryPort = reportRepositoryPort;
        this.reportResultMapper = reportResultMapper;
    }

    @Override
    public ReportResult updateStatus(UpdateReportStatusCommand command) {
        if (command == null || command.reportId() == null) {
            throw new ApplicationException(
                    ReportErrorCode.REPORT_NOT_FOUND,
                    ReportDetailMessageKey.REPORT_TITLE_BLANK
            );
        }

        Report existingReport = reportRepositoryPort.findById(command.reportId())
                .orElseThrow(() -> new ApplicationException(
                        ReportErrorCode.REPORT_NOT_FOUND,
                        ReportDetailMessageKey.REPORT_TITLE_BLANK
                ));

        Report updatedReport = existingReport.updateStatus(command.isResolved());
        Report savedReport = reportRepositoryPort.save(updatedReport);

        return reportResultMapper.domainToResult(savedReport);
    }
}
