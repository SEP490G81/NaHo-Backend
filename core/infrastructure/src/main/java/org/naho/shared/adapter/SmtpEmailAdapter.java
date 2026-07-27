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
    public void sendOtpEmail(String toEmail, String otpCode) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            Context context = new Context();
            context.setVariable("otpCode", otpCode);

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
}
