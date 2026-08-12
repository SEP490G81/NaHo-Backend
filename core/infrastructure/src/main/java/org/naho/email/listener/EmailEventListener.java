package org.naho.email.listener;

import org.naho.email.port.out.EmailPort;
import org.naho.social.report.event.ReportStatusUpdatedEvent;
import org.naho.user.event.PasswordChangedEvent;
import org.naho.user.event.UserPlanUpgradedEvent;
import org.naho.user.event.UserRegisteredEvent;
import org.naho.user.event.UserStatusChangedEvent;
import org.naho.user.model.User;
import org.naho.user.port.out.UserRepositoryPort;
import org.naho.user.type.UserStatus;
import org.springframework.context.MessageSource;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Component
public class EmailEventListener {

    private static final Locale DEFAULT_LOCALE = new Locale("vi", "VN");

    private final UserRepositoryPort userRepositoryPort;
    private final EmailPort emailPort;
    private final MessageSource messageSource;

    public EmailEventListener(UserRepositoryPort userRepositoryPort, EmailPort emailPort, MessageSource messageSource) {
        this.userRepositoryPort = userRepositoryPort;
        this.emailPort = emailPort;
        this.messageSource = messageSource;
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

            String subject = messageSource.getMessage("email.report.resolved.subject", new Object[]{event.reportId()}, "NaHo - Báo cáo #" + event.reportId() + " của bạn đã được xử lý", DEFAULT_LOCALE);
            Map<String, Object> variables = new HashMap<>();
            variables.put("fullName", fullName);
            variables.put("reportId", event.reportId());
            variables.put("reportTitle", event.reportTitle() != null ? event.reportTitle() : "N/A");
            if (event.adminNote() != null && !event.adminNote().isBlank()) {
                variables.put("adminNote", event.adminNote());
            }

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

    @Async
    @EventListener
    public void handleUserPlanUpgraded(UserPlanUpgradedEvent event) {
        try {
            Optional<User> userOpt = userRepositoryPort.findById(event.userId());
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

            String subject = messageSource.getMessage("email.plan.upgraded.subject", null, "Chúc mừng bạn đã nâng cấp gói thành công - NaHo App", DEFAULT_LOCALE);
            Map<String, Object> variables = new HashMap<>();
            variables.put("fullName", fullName);
            variables.put("planName", event.newPlanName());

            emailPort.sendEmail(email, subject, "plan-upgraded-email", variables);
        } catch (Exception ignored) {
            // Log error in production
        }
    }

    @Async
    @EventListener
    public void handleUserStatusChanged(UserStatusChangedEvent event) {
        try {
            String subject;
            String templateName;

            if (UserStatus.UNACTIVE.equals(event.newStatus())) {
                subject = messageSource.getMessage("email.account.locked.subject", null, "Tài khoản của bạn đã bị khóa - NaHo App", DEFAULT_LOCALE);
                templateName = "account-locked-email";
            } else if (UserStatus.ACTIVE.equals(event.newStatus())) {
                subject = messageSource.getMessage("email.account.unlocked.subject", null, "Tài khoản của bạn đã được mở khóa - NaHo App", DEFAULT_LOCALE);
                templateName = "account-unlocked-email";
            } else {
                return;
            }

            Map<String, Object> variables = new HashMap<>();
            variables.put("fullName", event.fullName());

            emailPort.sendEmail(event.email(), subject, templateName, variables);
        } catch (Exception ignored) {
            // Log error in production
            System.err.println(ignored.getMessage());
        }
    }
}
