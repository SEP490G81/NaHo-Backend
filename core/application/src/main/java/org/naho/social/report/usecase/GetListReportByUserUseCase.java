package org.naho.social.report.usecase;

import org.naho.social.report.command.GetReportsByUserCommand;
import org.naho.social.report.mapper.ReportResultMapper;
import org.naho.social.report.model.Report;
import org.naho.social.report.port.in.GetListReportByUserInputPort;
import org.naho.social.report.port.out.ReportRepositoryPort;
import org.naho.social.report.result.ReportResult;

import java.util.List;

public class GetListReportByUserUseCase implements GetListReportByUserInputPort {

    private final ReportRepositoryPort reportRepositoryPort;
    private final ReportResultMapper reportResultMapper;

    public GetListReportByUserUseCase(
            ReportRepositoryPort reportRepositoryPort,
            ReportResultMapper reportResultMapper
    ) {
        this.reportRepositoryPort = reportRepositoryPort;
        this.reportResultMapper = reportResultMapper;
    }

    @Override
    public List<ReportResult> getReportsByUser(GetReportsByUserCommand command) {
        if (command == null || command.userId() == null) {
            return List.of();
        }

        List<Report> reports = reportRepositoryPort.findByUserId(command.userId());
        return reports.stream()
                .map(reportResultMapper::domainToResult)
                .toList();
    }
}
