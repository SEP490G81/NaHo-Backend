package org.naho.shared.adapter;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.shared.exception.InfrastructureException;
import org.naho.shared.port.out.EmailPort;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
@RequiredArgsConstructor
@Slf4j
public class SmtpEmailAdapter implements EmailPort {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Async
    @Override
    public void sendOtpEmail(String toEmail, String fullName, String otpCode) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            Context context = new Context();
            context.setVariable("otpCode", otpCode);
            context.setVariable("fullName", fullName != null && !fullName.isBlank() ? fullName : "bạn");

            String htmlContent = templateEngine.process("otp-email", context);

            helper.setTo(toEmail);
            helper.setSubject("Mã xác nhận OTP của bạn - NaHo App");
            helper.setText(htmlContent, true);

            javaMailSender.send(mimeMessage);
            log.info("Sent OTP email to {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send OTP email to {}", toEmail, e);
            throw new InfrastructureException(
                    org.naho.shared.exception.CommonErrorCode.COMMON_INTERNAL_SERVER_ERROR,
                    "Lỗi gửi email xác nhận. Vui lòng thử lại sau.", e);
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
            context.setVariable("fullName", fullName != null && !fullName.isBlank() ? fullName : "bạn");

            String htmlContent = templateEngine.process("forgot-password-email", context);

            helper.setTo(toEmail);
            helper.setSubject("Yêu cầu khôi phục mật khẩu - NaHo App");
            helper.setText(htmlContent, true);

            javaMailSender.send(mimeMessage);
            log.info("Sent Forgot Password OTP email to {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send Forgot Password OTP email to {}", toEmail, e);
            throw new InfrastructureException(
                    org.naho.shared.exception.CommonErrorCode.COMMON_INTERNAL_SERVER_ERROR,
                    "Lỗi gửi email khôi phục mật khẩu. Vui lòng thử lại sau.", e);
        }
    }

    @Async
    @Override
    public void sendPasswordChangedEmail(String toEmail, String fullName) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            Context context = new Context();
            context.setVariable("fullName", fullName != null && !fullName.isBlank() ? fullName : "bạn");
            String htmlContent = templateEngine.process("password-changed-email", context);

            helper.setTo(toEmail);
            helper.setSubject("Thay đổi mật khẩu thành công - NaHo App");
            helper.setText(htmlContent, true);

            javaMailSender.send(mimeMessage);
            log.info("Sent Password Changed email to {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send Password Changed email to {}", toEmail, e);
            throw new InfrastructureException(
                    org.naho.shared.exception.CommonErrorCode.COMMON_INTERNAL_SERVER_ERROR,
                    "Lỗi gửi email thông báo đổi mật khẩu.", e);
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
            throw new InfrastructureException(
                    org.naho.shared.exception.CommonErrorCode.COMMON_INTERNAL_SERVER_ERROR,
                    "Lỗi gửi email. Vui lòng thử lại sau.", e);
        }
    }
}
