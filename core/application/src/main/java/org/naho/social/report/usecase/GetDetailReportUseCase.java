package org.naho.social.report.usecase;

import org.naho.i18n.message.social.ReportDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.social.model.Report;
import org.naho.social.report.command.GetReportCommand;
import org.naho.social.report.exception.ReportErrorCode;
import org.naho.social.report.mapper.ReportResultMapper;
import org.naho.social.report.port.in.GetReportInputPort;
import org.naho.social.report.port.out.ReportRepositoryPort;
import org.naho.social.report.result.ReportResult;

public class GetDetailReportUseCase implements GetReportInputPort {

    private final ReportRepositoryPort reportRepositoryPort;
    private final ReportResultMapper reportResultMapper;

    public GetDetailReportUseCase(ReportRepositoryPort reportRepositoryPort,
                                  ReportResultMapper reportResultMapper) {
        this.reportRepositoryPort = reportRepositoryPort;
        this.reportResultMapper = reportResultMapper;
    }

    @Override
    public ReportResult getReport(GetReportCommand command) {
        Report report = reportRepositoryPort.findById(command.id())
                .orElseThrow(() -> new ApplicationException(
                        ReportErrorCode.REPORT_NOT_FOUND,
                        ReportDetailMessageKey.REPORT_ID_NOT_FOUND,
                        command.id()));

        return reportResultMapper.domainToResult(report);
    }
}
