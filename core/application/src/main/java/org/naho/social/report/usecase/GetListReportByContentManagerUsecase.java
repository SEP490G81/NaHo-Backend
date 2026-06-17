package org.naho.social.report.usecase;

import org.naho.social.model.Report;
import org.naho.social.report.mapper.ReportResultMapper;
import org.naho.social.report.port.in.GetListReportByContentManagerInputPort;
import org.naho.social.report.port.out.ReportRepositoryPort;
import org.naho.social.report.result.ReportResult;
import org.naho.social.type.ReportType;

import java.util.List;

public class GetListReportByContentManagerUsecase implements GetListReportByContentManagerInputPort {

    private final ReportRepositoryPort reportRepositoryPort;
    private final ReportResultMapper reportResultMapper;

    public GetListReportByContentManagerUsecase(ReportRepositoryPort reportRepositoryPort,
                                                ReportResultMapper reportResultMapper) {
        this.reportRepositoryPort = reportRepositoryPort;
        this.reportResultMapper = reportResultMapper;
    }

    @Override
    public List<ReportResult> getReportsByContentManager() {
        List<Report> reports = reportRepositoryPort.findByReportTypeIn(List.of(ReportType.QUESTION, ReportType.COMMENT));
        return reports.stream()
                .map(reportResultMapper::domainToResult)
                .toList();
    }
}
