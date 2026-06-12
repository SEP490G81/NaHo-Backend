package org.naho.user.result;

public record GoogleUserInfoResult(
        String sub,
        String email,
        String fullName,
        String pictureUrl
) {
}
