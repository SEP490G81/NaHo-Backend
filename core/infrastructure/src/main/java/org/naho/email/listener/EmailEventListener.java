package org.naho.email.listener;

import org.naho.shared.port.out.EmailPort;
import org.naho.social.report.event.ReportStatusUpdatedEvent;
import org.naho.user.event.PasswordChangedEvent;
import org.naho.user.event.UserRegisteredEvent;
import org.naho.user.model.User;
import org.naho.user.port.out.UserRepositoryPort;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class EmailEventListener {

    private final UserRepositoryPort userRepositoryPort;
    private final EmailPort emailPort;

    public EmailEventListener(UserRepositoryPort userRepositoryPort, EmailPort emailPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.emailPort = emailPort;
    }

    /**
     * Gui email thong bao khi trang thai bao cao duoc cap nhat (VD: Da xu ly).
     * Ham chay Async de khong lam cham luong chinh cua UseCase.
     */
    @Async
    @EventListener
    public void handleReportStatusUpdated(ReportStatusUpdatedEvent event) {
        if (!"RESOLVED".equals(event.newStatus()) || event.reporterId() == null) {
            return;
        }

        try {
            Optional<User> userOpt = userRepositoryPort.findById(event.reporterId());
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

            String subject = "NaHo - Báo cáo #" + event.reportId() + " của bạn đã được xử lý";
            Map<String, Object> variables = new HashMap<>();
            variables.put("fullName", fullName);
            variables.put("reportId", event.reportId());
            variables.put("reportTitle", event.reportTitle() != null ? event.reportTitle() : "N/A");

            emailPort.sendEmail(email, subject, "report-resolved-email", variables);
        } catch (Exception ignored) {
            // Log error in production
        }
    }

    @Async
    @EventListener
    public void handlePasswordChanged(PasswordChangedEvent event) {
        try {
            emailPort.sendPasswordChangedEmail(event.email(), event.fullName());
        } catch (Exception ignored) {
            // Log error in production
        }
    }

    @Async
    @EventListener
    public void handleUserRegistered(UserRegisteredEvent event) {
        try {
            emailPort.sendOtpEmail(event.email(), event.fullName(), event.otpCode());
        } catch (Exception ignored) {
            // Log error in production
        }
    }
}
