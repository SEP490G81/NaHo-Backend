package org.naho.social.report.usecase;

import org.naho.i18n.message.social.ReportDetailMessageKey;
import org.naho.notification.event.SendNotificationEvent;
import org.naho.notification.type.NotificationType;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.EmailPort;
import org.naho.shared.port.out.EventPublisherPort;
import org.naho.social.report.command.UpdateReportStatusCommand;
import org.naho.social.report.exception.ReportErrorCode;
import org.naho.social.report.mapper.ReportResultMapper;
import org.naho.social.report.model.Report;
import org.naho.social.report.port.in.UpdateReportStatusInputPort;
import org.naho.social.report.port.out.ReportRepositoryPort;
import org.naho.social.report.result.ReportResult;
import org.naho.user.model.User;
import org.naho.user.port.out.UserRepositoryPort;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
            EventPublisherPort eventPublisherPort
    ) {
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
                    ReportDetailMessageKey.REPORT_TITLE_BLANK
            );
        }

        Report existingReport = reportRepositoryPort.findById(command.reportId())
                .orElseThrow(() -> new ApplicationException(
                        ReportErrorCode.REPORT_NOT_FOUND,
                        ReportDetailMessageKey.REPORT_TITLE_BLANK
                ));

        boolean shouldSendEmail = command.isResolved() && !existingReport.isResolved();

        Report updatedReport = existingReport.updateStatus(command.isResolved());
        Report savedReport = reportRepositoryPort.save(updatedReport);

        if (shouldSendEmail) {
            sendReportResolvedEmail(savedReport);

            // Send in-app notification to the reported user (SYSTEM type)
            // Or the user who created the report (if we want to notify them it was resolved).
            // Usually we notify the user who got reported if their content was deleted.
            // Let's notify the reporter that their report was resolved as SYSTEM type.
            String metadata = "{\"reportId\": " + savedReport.getId() + ", \"action\": \"RESOLVED\"}";
            eventPublisherPort.publish(new SendNotificationEvent(
                    this,
                    savedReport.getUserId(),
                    NotificationType.SYSTEM,
                    "Báo cáo đã được xử lý",
                    "Báo cáo của bạn đã được quản trị viên xử lý thành công.",
                    null,
                    metadata
            ));
        }

        return reportResultMapper.domainToResult(savedReport);
    }

    private void sendReportResolvedEmail(Report report) {
        if (report.getUserId() == null) {
            return;
        }
        try {
            Optional<User> userOpt = userRepositoryPort.findById(report.getUserId());
            if (userOpt.isEmpty()) {
                return;
            }
            User user = userOpt.get();
            if (user.getEmail() == null || user.getEmail().getValue() == null || user.getEmail().getValue().isBlank()) {
                return;
            }
            String email = user.getEmail().getValue();
            String fullName = user.getFullName() != null && !user.getFullName().isBlank()
                    ? user.getFullName()
                    : (user.getUsername() != null ? user.getUsername().getValue() : "User");

            String subject = "NaHo - Báo cáo #" + report.getId() + " của bạn đã được xử lý";
            Map<String, Object> variables = new HashMap<>();
            variables.put("fullName", fullName);
            variables.put("reportId", report.getId());
            variables.put("reportTitle", report.getTitle());
            variables.put("reportType", report.getReportType() != null ? report.getReportType().name() : "");

            emailPort.sendEmail(email, subject, "report-resolved-email", variables);
        } catch (Exception ignored) {
        }
    }
}

