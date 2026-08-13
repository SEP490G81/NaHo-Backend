package org.naho.email.port.out;

public interface EmailPort {
    void sendOtpEmail(String toEmail, String fullName, String otpCode);

    void sendForgotPasswordOtpEmail(String toEmail, String fullName, String otpCode);

    void sendPasswordChangedEmail(String toEmail, String fullName);

    void sendEmail(String toEmail, String subject, String templateName, java.util.Map<String, Object> variables);
}
