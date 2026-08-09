package org.naho.shared.port.out;

public interface EmailPort {
    void sendOtpEmail(String toEmail, String otpCode);

    void sendForgotPasswordOtpEmail(String toEmail, String otpCode);

    void sendPasswordChangedEmail(String toEmail);

    void sendEmail(String toEmail, String subject, String templateName, java.util.Map<String, Object> variables);
}
