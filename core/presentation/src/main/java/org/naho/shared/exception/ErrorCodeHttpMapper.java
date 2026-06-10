package org.naho.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.net.URI;

import static org.naho.furigana.exception.FuriganaApplicationErrorCode.FURIGANA_ANALYZE_FAILED;
import static org.naho.speech.azure.exception.AzureSpeechErrorCode.SPEECH_AUDIO_NOT_VALID;
import static org.naho.speech.azure.exception.AzureSpeechErrorCode.SPEECH_AZURE_SERVICE_ERROR;
import static org.naho.speech.exception.TopicDomainErrorCode.TOPIC_DESCRIPTION_EMPTY;
import static org.naho.speech.exception.TopicDomainErrorCode.TOPIC_NAME_EMPTY;
import static org.naho.speech.topic.exception.TopicErrorCode.TOPIC_ALREADY_EXISTS;
import static org.naho.user.exception.UserDomainErrorCode.*;
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
                 TOPIC_NAME_EMPTY,
                 TOPIC_DESCRIPTION_EMPTY,
                 USER_LOGIN_FAILED -> HttpStatus.BAD_REQUEST;
            case TOPIC_ALREADY_EXISTS,
                 USER_ALREADY_EXISTS -> HttpStatus.CONFLICT;
            case SPEECH_AZURE_SERVICE_ERROR -> HttpStatus.BAD_GATEWAY;
            case USER_UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
            case USER_ACCESS_DENIED -> HttpStatus.FORBIDDEN;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
