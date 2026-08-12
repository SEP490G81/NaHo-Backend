package org.naho.email.adapter;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.email.port.out.EmailPort;
import org.naho.shared.exception.InfrastructureException;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;

@Component
@RequiredArgsConstructor
@Slf4j
public class SmtpEmailAdapter implements EmailPort {

    private static final Locale DEFAULT_LOCALE = new Locale("vi", "VN");
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    private final MessageSource messageSource;

    @Async
    @Override
    public void sendOtpEmail(String toEmail, String fullName, String otpCode) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            Context context = new Context();
            context.setVariable("otpCode", otpCode);
            String defaultName = messageSource.getMessage("email.default.user.name", null, "ban", DEFAULT_LOCALE);
            context.setVariable("fullName", fullName != null && !fullName.isBlank() ? fullName : defaultName);

            String htmlContent = templateEngine.process("otp-email", context);

            helper.setTo(toEmail);
            String subject = messageSource.getMessage("email.otp.subject", null, "Ma xac nhan OTP cua ban - NaHo App", DEFAULT_LOCALE);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            javaMailSender.send(mimeMessage);
            log.info("Sent OTP email to {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send OTP email to {}", toEmail, e);
            String errorMsg = messageSource.getMessage("email.otp.error", null, "Loi gui email xac nhan. Vui long thu lai sau.", DEFAULT_LOCALE);
            throw new InfrastructureException(
                    org.naho.shared.exception.CommonErrorCode.COMMON_INTERNAL_SERVER_ERROR,
                    errorMsg, e);
        }
    }

    @Async
    @Override
    public void sendForgotPasswordOtpEmail(String toEmail, String fullName, String otpCode) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            Context context = new Context();
            context.setVariable("otpCode", otpCode);
            String defaultName = messageSource.getMessage("email.default.user.name", null, "ban", DEFAULT_LOCALE);
            context.setVariable("fullName", fullName != null && !fullName.isBlank() ? fullName : defaultName);

            String htmlContent = templateEngine.process("forgot-password-email", context);

            helper.setTo(toEmail);
            String subject = messageSource.getMessage("email.forgot.password.subject", null, "Yeu cau khoi phuc mat khau - NaHo App", DEFAULT_LOCALE);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            javaMailSender.send(mimeMessage);
            log.info("Sent Forgot Password OTP email to {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send Forgot Password OTP email to {}", toEmail, e);
            String errorMsg = messageSource.getMessage("email.forgot.password.error", null, "Loi gui email khoi phuc mat khau. Vui long thu lai sau.", DEFAULT_LOCALE);
            throw new InfrastructureException(
                    org.naho.shared.exception.CommonErrorCode.COMMON_INTERNAL_SERVER_ERROR,
                    errorMsg, e);
        }
    }

    @Async
    @Override
    public void sendPasswordChangedEmail(String toEmail, String fullName) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            Context context = new Context();
            String defaultName = messageSource.getMessage("email.default.user.name", null, "ban", DEFAULT_LOCALE);
            context.setVariable("fullName", fullName != null && !fullName.isBlank() ? fullName : defaultName);
            String htmlContent = templateEngine.process("password-changed-email", context);

            helper.setTo(toEmail);
            String subject = messageSource.getMessage("email.password.changed.subject", null, "Thay doi mat khau thanh cong - NaHo App", DEFAULT_LOCALE);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            javaMailSender.send(mimeMessage);
            log.info("Sent Password Changed email to {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send Password Changed email to {}", toEmail, e);
            String errorMsg = messageSource.getMessage("email.password.changed.error", null, "Loi gui email thong bao doi mat khau.", DEFAULT_LOCALE);
            throw new InfrastructureException(
                    org.naho.shared.exception.CommonErrorCode.COMMON_INTERNAL_SERVER_ERROR,
                    errorMsg, e);
        }
    }

    @Async
    @Override
    public void sendEmail(String toEmail, String subject, String templateName, java.util.Map<String, Object> variables) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            Context context = new Context();
            if (variables != null) {
                variables.forEach(context::setVariable);
            }

            String htmlContent = templateEngine.process(templateName, context);

            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            javaMailSender.send(mimeMessage);
            log.info("Sent email to {} with subject '{}'", toEmail, subject);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}", toEmail, e);
            String errorMsg = messageSource.getMessage("email.generic.error", null, "Loi gui email. Vui long thu lai sau.", DEFAULT_LOCALE);
            throw new InfrastructureException(
                    org.naho.shared.exception.CommonErrorCode.COMMON_INTERNAL_SERVER_ERROR,
                    errorMsg, e);
        } catch (Exception e) {
            log.error("Unexpected error sending email to {}", toEmail, e);
            throw e;
        }
    }
}
