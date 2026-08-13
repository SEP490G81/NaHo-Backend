package org.naho.user.constant;

import java.time.Duration;

public final class UserLoginConstant {
    private UserLoginConstant() {
    }

    public static final int MAX_FAILED_ATTEMPTS = 5;
    public static final Duration LOCK_DURATION_MINUTES = Duration.ofMinutes(15);
}
