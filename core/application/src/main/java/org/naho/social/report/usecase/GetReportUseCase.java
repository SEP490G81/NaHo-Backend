package org.naho.social.report.usecase;

import org.naho.i18n.message.social.ReportDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.social.report.model.Report;
import org.naho.social.report.type.ReportType;
import org.naho.social.report.command.GetReportCommand;
import org.naho.social.report.exception.ReportErrorCode;
import org.naho.social.report.mapper.ReportResultMapper;
import org.naho.social.report.port.in.GetReportInputPort;
import org.naho.social.report.port.out.ReportRepositoryPort;
import org.naho.social.report.result.ReportResult;

import java.util.List;

public class GetReportUseCase implements GetReportInputPort {

    private final ReportRepositoryPort reportRepositoryPort;
    private final ReportResultMapper reportResultMapper;

    public GetReportUseCase(ReportRepositoryPort reportRepositoryPort,
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

    @Override
    public List<ReportResult> getReportsByAdmin() {
        List<Report> reports = reportRepositoryPort.findByReportTypeIn(List.of(ReportType.SYSTEM));
        return reports.stream()
                .map(reportResultMapper::domainToResult)
                .toList();
    }

    @Override
    public List<ReportResult> getReportsByContentManager() {
        List<Report> reports = reportRepositoryPort.findByReportTypeIn(List.of(ReportType.QUESTION, ReportType.COMMENT));
        return reports.stream()
                .map(reportResultMapper::domainToResult)
                .toList();
    }

}
