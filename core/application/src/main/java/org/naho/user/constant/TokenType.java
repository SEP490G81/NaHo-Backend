package org.naho.user.constant;

public final class TokenType {
    private TokenType() {
    }

    public static final String ACCESS_TOKEN_NAME = "access_token";
    public static final String REFRESH_TOKEN_NAME = "refresh_token";

    public static final String ACCESS_TOKEN_COOKIE_NAME = "__Host_access_token";
    public static final String REFRESH_TOKEN_COOKIE_NAME = "__Host_refresh_token";
}
