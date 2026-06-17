package org.naho.social.report.usecase;

import org.naho.social.model.Report;
import org.naho.social.report.mapper.ReportResultMapper;
import org.naho.social.report.port.in.GetListReportByAdminInputPort;
import org.naho.social.report.port.out.ReportRepositoryPort;
import org.naho.social.report.result.ReportResult;
import org.naho.social.type.ReportType;

import java.util.List;

public class GetListReportByAdminUsecase implements GetListReportByAdminInputPort {

    private final ReportRepositoryPort reportRepositoryPort;
    private final ReportResultMapper reportResultMapper;

    public GetListReportByAdminUsecase(ReportRepositoryPort reportRepositoryPort,
                                       ReportResultMapper reportResultMapper) {
        this.reportRepositoryPort = reportRepositoryPort;
        this.reportResultMapper = reportResultMapper;
    }

    @Override
    public List<ReportResult> getReportsByAdmin() {
        List<Report> reports = reportRepositoryPort.findByReportTypeIn(List.of(ReportType.SYSTEM));
        return reports.stream()
                .map(reportResultMapper::domainToResult)
                .toList();
    }
}
