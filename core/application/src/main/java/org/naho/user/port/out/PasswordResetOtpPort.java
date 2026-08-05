package org.naho.user.port.out;

public interface PasswordResetOtpPort {
    void saveOtp(String email, String otpCode);

    boolean verifyOtp(String email, String otpCode);

    void removeOtp(String email);

    String generateOtp();

    boolean hasValidOtp(String email);

    long getOtpTtlSeconds(String email);

    void incrementFailedAttempts(String email);

    int getFailedAttempts(String email);

    void clearFailedAttempts(String email);

    void saveResetToken(String email, String token);

    boolean verifyResetToken(String email, String token);

    void removeResetToken(String email);
}
