package org.naho.shared.port.out;

public interface EmailPort {
    void sendOtpEmail(String toEmail, String otpCode);
}
