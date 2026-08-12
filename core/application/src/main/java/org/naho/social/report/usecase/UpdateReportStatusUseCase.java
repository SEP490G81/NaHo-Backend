package org.naho.social.report.usecase;

import org.naho.email.port.out.EmailPort;
import org.naho.i18n.message.social.ReportDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.EventPublisherPort;
import org.naho.social.report.command.UpdateReportStatusCommand;
import org.naho.social.report.event.ReportStatusUpdatedEvent;
import org.naho.social.report.exception.ReportErrorCode;
import org.naho.social.report.mapper.ReportResultMapper;
import org.naho.social.report.model.Report;
import org.naho.social.report.port.in.UpdateReportStatusInputPort;
import org.naho.social.report.port.out.ReportRepositoryPort;
import org.naho.social.report.result.ReportResult;
import org.naho.user.port.out.UserRepositoryPort;

public class UpdateReportStatusUseCase implements UpdateReportStatusInputPort {

    private final ReportRepositoryPort reportRepositoryPort;
    private final ReportResultMapper reportResultMapper;
    private final UserRepositoryPort userRepositoryPort;
    private final EmailPort emailPort;
    private final EventPublisherPort eventPublisherPort;

    public UpdateReportStatusUseCase(
            ReportRepositoryPort reportRepositoryPort,
            ReportResultMapper reportResultMapper,
            UserRepositoryPort userRepositoryPort,
            EmailPort emailPort,
            EventPublisherPort eventPublisherPort) {
        this.reportRepositoryPort = reportRepositoryPort;
        this.reportResultMapper = reportResultMapper;
        this.userRepositoryPort = userRepositoryPort;
        this.emailPort = emailPort;
        this.eventPublisherPort = eventPublisherPort;
    }

    @Override
    public ReportResult updateStatus(UpdateReportStatusCommand command) {
        if (command == null || command.reportId() == null) {
            throw new ApplicationException(
                    ReportErrorCode.REPORT_NOT_FOUND,
                    ReportDetailMessageKey.REPORT_TITLE_BLANK);
        }

        Report existingReport = reportRepositoryPort.findById(command.reportId())
                .orElseThrow(() -> new ApplicationException(
                        ReportErrorCode.REPORT_NOT_FOUND,
                        ReportDetailMessageKey.REPORT_TITLE_BLANK));

        boolean shouldSendEmail = command.isResolved() && !existingReport.isResolved();

        Report updatedReport = existingReport.updateStatus(command.isResolved(), command.adminReply());
        Report savedReport = reportRepositoryPort.save(updatedReport);

        if (shouldSendEmail) {
            eventPublisherPort.publish(new ReportStatusUpdatedEvent(
                    savedReport.getUserId(),
                    savedReport.getId(),
                    savedReport.getTitle(),
                    savedReport.getAdminReply(),
                    savedReport.getReportType().name()));
        }
        
        return reportResultMapper.domainToResult(savedReport);
    }
}
