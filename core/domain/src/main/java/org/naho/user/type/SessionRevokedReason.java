package org.naho.user.type;

public enum SessionRevokedReason {
    LOGIN_AGAIN,
    USER_LOGOUT,
    USER_LOGOUT_ALL,
    LOGOUT_OTHERS,
    PASSWORD_CHANGED,
    ADMIN_REVOKED,
    TOKEN_REUSE_DETECTED,
    EXPIRED,
    ROTATED
}
