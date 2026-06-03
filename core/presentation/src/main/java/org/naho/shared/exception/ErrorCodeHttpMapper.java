package org.naho.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.net.URI;

import static org.naho.furigana.exception.FuriganaApplicationErrorCode.FURIGANA_ANALYZE_FAILED;
import static org.naho.speech.azure.exception.AzureSpeechApplicationErrorCode.SPEECH_AUDIO_NOT_VALID;
import static org.naho.speech.azure.exception.AzureSpeechApplicationErrorCode.SPEECH_AZURE_SERVICE_ERROR;
import static org.naho.user.exception.UserApplicationErrorCode.*;
import static org.naho.user.exception.UserErrorCode.*;

@Component
public class ErrorCodeHttpMapper {
    private static final String ERROR_TYPE_BASE = "https://api.v1.naho/errors/";

    public URI toType(ErrorCode errorCode) {
        return URI.create(ERROR_TYPE_BASE + errorCode.getCode().toLowerCase());
    }

    public HttpStatus toStatus(ErrorCode errorCode) {
        return switch (errorCode) {
            case USER_AGE_NOT_VALID,
                 USER_EMAIL_NOT_VALID,
                 USER_USERNAME_NOT_VALID,
                 USER_TIMEZONE_NOT_VALID,
                 USER_NOT_FOUND,
                 SPEECH_AUDIO_NOT_VALID,
                 FURIGANA_ANALYZE_FAILED,
                 USER_LOGIN_FAILED -> HttpStatus.BAD_REQUEST;
            case USER_ALREADY_EXISTS -> HttpStatus.CONFLICT;
            case SPEECH_AZURE_SERVICE_ERROR -> HttpStatus.BAD_GATEWAY;
            case USER_UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
            case USER_ACCESS_DENIED -> HttpStatus.FORBIDDEN;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
