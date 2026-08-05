package org.naho.file.valueobject;

import java.time.Duration;
import java.time.Instant;

public final class NextRetryAt {
    private final Instant value;

    private NextRetryAt(Instant value) {
        this.value = value;
    }

    public static NextRetryAt of(Instant value) {
        if (value == null) {
            return null;
        }
        return new NextRetryAt(value);
    }

    public static NextRetryAt getFromRetryCount(int retryCount) {
        Instant now = Instant.now();

        return switch (retryCount) {
            // cần chờ 1 phút cho lần retry đầu tiên
            case 0 -> new NextRetryAt(now.plus(Duration.ofMinutes(1)));
            // cần chờ 5 phút cho lần retry thứ 2
            case 1 -> new NextRetryAt(now.plus(Duration.ofMinutes(5)));
            // cần chờ 10 phút cho lần retry thứ 3
            case 2 -> new NextRetryAt(now.plus(Duration.ofMinutes(10)));
            case 3 -> new NextRetryAt(now.plus(Duration.ofMinutes(30)));
            case 4 -> new NextRetryAt(now.plus(Duration.ofHours(1)));
            case 5 -> new NextRetryAt(now.plus(Duration.ofHours(2)));
            default -> new NextRetryAt(now.plus(Duration.ofMinutes(4)));
        };
    }

    public Instant getValue() {
        return value;
    }
}
