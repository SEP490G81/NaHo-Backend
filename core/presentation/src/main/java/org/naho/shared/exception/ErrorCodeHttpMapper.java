package org.naho.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.net.URI;

import static org.naho.user.exception.UserErrorCode.*;

@Component
public class ErrorCodeHttpMapper {
    private static final String ERROR_TYPE_BASE = "https://api.v1.naho/errors/";

    public URI toType(ErrorCode errorCode) {
        return URI.create(ERROR_TYPE_BASE + errorCode.getCode().toLowerCase());
    }

    public HttpStatus toStatus(ErrorCode errorCode) {
        return switch (errorCode) {
            case AGE_NOT_VALID,
                 EMAIL_REQUIRED,
                 EMAIL_NOT_VALID,
                 PASSWORD_REQUIRED,
                 PASSWORD_NOT_VALID,
                 USERNAME_REQUIRED,
                 USERNAME_NOT_VALID -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
