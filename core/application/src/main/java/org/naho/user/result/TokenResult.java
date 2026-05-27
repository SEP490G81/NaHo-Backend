package org.naho.user.result;

public record TokenResult(
        String value,
        Long expireIn
) {
}
