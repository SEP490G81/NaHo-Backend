package org.naho.social.report.usecase;

import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.social.report.mapper.ReportResultMapper;
import org.naho.social.report.model.Report;
import org.naho.social.report.port.in.GetListReportByUserInputPort;
import org.naho.social.report.port.out.ReportRepositoryPort;
import org.naho.social.report.result.ReportResult;
import org.naho.user.exception.UserErrorCode;

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
    public List<ReportResult> getReportsByUser(Long userId) {
        if (userId == null) {
            throw new ApplicationException(
                    UserErrorCode.USER_NOT_FOUND,
                    UserDetailMessageKey.USER_ID_NULL
            );
        }

        List<Report> reports = reportRepositoryPort.findAllByUserId(userId);
        
        return reports.stream()
                .map(reportResultMapper::domainToResult)
                .toList();
    }
}
