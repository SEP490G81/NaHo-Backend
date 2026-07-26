package org.naho.user.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.user.port.out.OtpPort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class RedisOtpAdapter implements OtpPort {

    private static final String OTP_PREFIX = "email_otp:";
    private static final long OTP_TTL_MINUTES = 5;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void saveOtp(String email, String otpCode) {
        stringRedisTemplate.opsForValue().set(
                OTP_PREFIX + email,
                otpCode,
                Duration.ofMinutes(OTP_TTL_MINUTES)
        );
    }

    @Override
    public boolean verifyOtp(String email, String otpCode) {
        String savedOtp = stringRedisTemplate.opsForValue().get(OTP_PREFIX + email);
        return savedOtp != null && savedOtp.equals(otpCode);
    }

    @Override
    public void removeOtp(String email) {
        stringRedisTemplate.delete(OTP_PREFIX + email);
    }

    @Override
    public String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // 6-digit OTP
        return String.valueOf(otp);
    }

    @Override
    public boolean hasValidOtp(String email) {
        return stringRedisTemplate.hasKey(OTP_PREFIX + email);
    }

    @Override
    public long getOtpTtlSeconds(String email) {
        Long expire = stringRedisTemplate.getExpire(OTP_PREFIX + email);
        return expire != null ? expire : 0;
    }

    @Override
    public void incrementFailedAttempts(String email) {
        String key = "otp_attempts:" + email;
        stringRedisTemplate.opsForValue().increment(key);
        stringRedisTemplate.expire(key, Duration.ofMinutes(OTP_TTL_MINUTES));
    }

    @Override
    public int getFailedAttempts(String email) {
        String key = "otp_attempts:" + email;
        String val = stringRedisTemplate.opsForValue().get(key);
        return val != null ? Integer.parseInt(val) : 0;
    }

    @Override
    public void clearFailedAttempts(String email) {
        stringRedisTemplate.delete("otp_attempts:" + email);
    }
}
